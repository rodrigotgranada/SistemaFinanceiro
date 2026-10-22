package com.financial.banking.contas.infrastructure.service;

import com.financial.banking.contas.api.AbrirContaRequestDTO;
import com.financial.banking.contas.api.ContaResponseDTO;
import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.contas.infrastructure.persistence.StatusConta;
import com.financial.banking.shared.exception.ContaJaExisteException;
import com.financial.banking.shared.exception.DocumentoInvalidoException;
import com.financial.banking.shared.kafka.KafkaTransactionProducer;
import com.financial.banking.shared.kafka.TransactionEvent;
import com.financial.banking.shared.util.CpfCnpjValidator;
import com.financial.banking.shared.util.DigitoVerificadorCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AbrirContaService {

    private static final Logger log = LoggerFactory.getLogger(AbrirContaService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ContaRepository contaRepository;
    private final KafkaTransactionProducer kafkaProducer;

    public AbrirContaService(ContaRepository contaRepository, KafkaTransactionProducer kafkaProducer) {
        this.contaRepository = contaRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional(rollbackFor = Exception.class)
    public ContaResponseDTO abrirConta(AbrirContaRequestDTO request) {
        log.info("Iniciando abertura de conta para titular: {}", request.nomeTitular());

        if (!CpfCnpjValidator.isValid(request.cpfCnpj())) {
            throw new DocumentoInvalidoException(request.cpfCnpj());
        }

        String documentoLimpo = CpfCnpjValidator.normalizar(request.cpfCnpj());

        String numeroConta = (request.numeroConta() != null && !request.numeroConta().trim().isEmpty())
                ? extrairNumeroConta(request.numeroConta())
                : gerarNumeroContaUnico();

        if (contaRepository.existsByNumeroConta(numeroConta)) {
            throw new ContaJaExisteException("Já existe uma conta cadastrada com o número " + numeroConta);
        }

        String dv = DigitoVerificadorCalculator.calcularModulo11(numeroConta);

        Conta novaConta = new Conta(
                null,
                numeroConta,
                dv,
                documentoLimpo,
                request.nomeTitular().trim(),
                BigDecimal.ZERO,
                StatusConta.ATIVA,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        Conta contaSalva = contaRepository.save(novaConta);
        log.info("Conta aberta com sucesso: {}", contaSalva.getNumeroFormatado());

        publicarEventoKafka(contaSalva);

        return ContaResponseDTO.fromEntity(contaSalva);
    }

    private String extrairNumeroConta(String raw) {
        String[] partes = raw.trim().split("-");
        return partes[0].replaceAll("\\D", "");
    }

    private String gerarNumeroContaUnico() {
        String numero;
        do {
            int randomInt = 100000 + RANDOM.nextInt(900000);
            numero = String.valueOf(randomInt);
        } while (contaRepository.existsByNumeroConta(numero));
        return numero;
    }

    private void publicarEventoKafka(Conta conta) {
        kafkaProducer.publicar(new TransactionEvent(
                UUID.randomUUID().toString(),
                conta.getNumeroFormatado(),
                "ABERTURA_CONTA",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                "Abertura de nova conta corrente",
                null,
                OffsetDateTime.now()
        ));
    }
}
