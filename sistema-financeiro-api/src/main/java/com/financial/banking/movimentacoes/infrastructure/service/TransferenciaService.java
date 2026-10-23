package com.financial.banking.movimentacoes.infrastructure.service;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.movimentacoes.api.TransacaoSucessoResponseDTO;
import com.financial.banking.movimentacoes.api.TransferenciaRequestDTO;
import com.financial.banking.movimentacoes.infrastructure.persistence.Movimentacao;
import com.financial.banking.movimentacoes.infrastructure.persistence.MovimentacaoRepository;
import com.financial.banking.movimentacoes.infrastructure.persistence.TipoMovimentacao;
import com.financial.banking.shared.exception.ContaNaoEncontradaException;
import com.financial.banking.shared.exception.TransferenciaMesmaContaException;
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
public class TransferenciaService {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaService.class);

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final KafkaTransactionProducer kafkaProducer;

    public TransferenciaService(ContaRepository contaRepository,
            MovimentacaoRepository movimentacaoRepository,
            KafkaTransactionProducer kafkaProducer) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional(rollbackFor = Exception.class)
    public TransacaoSucessoResponseDTO transferir(TransferenciaRequestDTO request) {
        String cleanOrigem = extrairNumeroConta(request.contaOrigem());
        String cleanDestino = extrairNumeroConta(request.contaDestino());

        if (cleanOrigem.equals(cleanDestino)) {
            throw new TransferenciaMesmaContaException();
        }

        log.info("Iniciando transferência de R$ {} de {} para {}", request.valor(), cleanOrigem, cleanDestino);

        String primeiraConta = cleanOrigem.compareTo(cleanDestino) < 0 ? cleanOrigem : cleanDestino;
        String segundaConta = cleanOrigem.compareTo(cleanDestino) < 0 ? cleanDestino : cleanOrigem;

        Conta contaA = contaRepository.findByNumeroContaWithLock(primeiraConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(primeiraConta));
        Conta contaB = contaRepository.findByNumeroContaWithLock(segundaConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(segundaConta));

        Conta contaOrigem = cleanOrigem.equals(contaA.getNumeroConta()) ? contaA : contaB;
        Conta contaDestino = cleanDestino.equals(contaA.getNumeroConta()) ? contaA : contaB;

        contaOrigem.sacar(request.valor());
        contaDestino.depositar(request.valor());

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Movimentacao movOrigem = Movimentacao.criar(
                contaOrigem.getId(),
                TipoMovimentacao.TRANSFERENCIA_ENVIO,
                request.valor(),
                contaOrigem.getSaldo(),
                String.format("Transferência enviada para %s", contaDestino.getNumeroFormatado()),
                contaDestino.getNumeroFormatado());
        movimentacaoRepository.save(movOrigem);

        Movimentacao movDestino = Movimentacao.criar(
                contaDestino.getId(),
                TipoMovimentacao.TRANSFERENCIA_RECEBIMENTO,
                request.valor(),
                contaDestino.getSaldo(),
                String.format("Transferência recebida de %s", contaOrigem.getNumeroFormatado()),
                contaOrigem.getNumeroFormatado());
        movimentacaoRepository.save(movDestino);

        String protocolo = UUID.randomUUID().toString();
        log.info("Transferência concluída com sucesso. Protocolo: {}", protocolo);

        notificarKafka(contaOrigem, contaDestino, request.valor(), protocolo);

        return new TransacaoSucessoResponseDTO(
                true,
                "TRANSFERENCIA",
                String.format("Transferência de R$ %s realizada com sucesso para %s",
                        request.valor().setScale(2, RoundingMode.HALF_EVEN),
                        contaDestino.getNumeroFormatado()),
                protocolo,
                contaOrigem.getNumeroFormatado(),
                request.valor().setScale(2, RoundingMode.HALF_EVEN),
                contaOrigem.getSaldo().setScale(2, RoundingMode.HALF_EVEN),
                OffsetDateTime.now());
    }

    private String extrairNumeroConta(String raw) {
        if (raw == null)
            return "";
        String[] partes = raw.trim().split("-");
        return partes[0].replaceAll("\\D", "");
    }

    private void notificarKafka(Conta origem, Conta destino, java.math.BigDecimal valor, String protocolo) {
        try {
            TransactionEvent event = new TransactionEvent(
                    protocolo,
                    origem.getNumeroFormatado(),
                    "TRANSFERENCIA",
                    valor.setScale(2, RoundingMode.HALF_EVEN),
                    origem.getSaldo().setScale(2, RoundingMode.HALF_EVEN),
                    String.format("Transferência enviada para %s", destino.getNumeroFormatado()),
                    destino.getNumeroFormatado(),
                    OffsetDateTime.now());
            kafkaProducer.publicar(event);
        } catch (Exception e) {
            log.warn("Falha ao publicar evento no Kafka para transferência: {}", e.getMessage());
        }
    }
}
