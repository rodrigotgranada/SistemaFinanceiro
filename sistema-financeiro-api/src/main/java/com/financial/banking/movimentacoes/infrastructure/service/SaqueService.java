package com.financial.banking.movimentacoes.infrastructure.service;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.movimentacoes.api.SaqueRequestDTO;
import com.financial.banking.movimentacoes.api.TransacaoSucessoResponseDTO;
import com.financial.banking.movimentacoes.infrastructure.persistence.Movimentacao;
import com.financial.banking.movimentacoes.infrastructure.persistence.MovimentacaoRepository;
import com.financial.banking.movimentacoes.infrastructure.persistence.TipoMovimentacao;
import com.financial.banking.shared.exception.ContaNaoEncontradaException;
import com.financial.banking.shared.kafka.KafkaTransactionProducer;
import com.financial.banking.shared.kafka.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class SaqueService {

    private static final Logger log = LoggerFactory.getLogger(SaqueService.class);

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final KafkaTransactionProducer kafkaProducer;

    public SaqueService(ContaRepository contaRepository,
                        MovimentacaoRepository movimentacaoRepository,
                        KafkaTransactionProducer kafkaProducer) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional(rollbackFor = Exception.class)
    public TransacaoSucessoResponseDTO sacar(String numeroConta, SaqueRequestDTO request) {
        String cleanNumero = extrairNumeroConta(numeroConta);
        log.info("Processando saque de R$ {} na conta {}", request.valor(), cleanNumero);

        Conta conta = contaRepository.findByNumeroContaWithLock(cleanNumero)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));

        conta.sacar(request.valor());
        contaRepository.save(conta);

        Movimentacao mov = Movimentacao.criar(
                conta.getId(),
                TipoMovimentacao.SAQUE,
                request.valor(),
                conta.getSaldo(),
                "Saque em conta corrente",
                null
        );
        movimentacaoRepository.save(mov);

        String protocolo = UUID.randomUUID().toString();
        notificarKafka(conta, request.valor());

        return new TransacaoSucessoResponseDTO(
                true,
                TipoMovimentacao.SAQUE.name(),
                "Saque realizado com sucesso",
                protocolo,
                conta.getNumeroFormatado(),
                request.valor().setScale(2, RoundingMode.HALF_EVEN),
                conta.getSaldo().setScale(2, RoundingMode.HALF_EVEN),
                OffsetDateTime.now()
        );
    }

    private String extrairNumeroConta(String raw) {
        if (raw == null) return "";
        String[] partes = raw.trim().split("-");
        return partes[0].replaceAll("\\D", "");
    }

    private void notificarKafka(Conta conta, java.math.BigDecimal valor) {
        try {
            TransactionEvent event = new TransactionEvent(
                    UUID.randomUUID().toString(),
                    conta.getNumeroFormatado(),
                    "SAQUE",
                    valor.setScale(2, RoundingMode.HALF_EVEN),
                    conta.getSaldo().setScale(2, RoundingMode.HALF_EVEN),
                    "Saque em conta corrente",
                    null,
                    OffsetDateTime.now()
            );
            kafkaProducer.publicar(event);
        } catch (Exception e) {
            log.warn("Falha ao publicar evento no Kafka para saque na conta {}: {}", conta.getNumeroFormatado(), e.getMessage());
        }
    }
}
