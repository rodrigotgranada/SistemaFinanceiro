package com.financial.banking.shared.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionConsumer.class);

    @KafkaListener(
            topics = "${banking.kafka.topic.transactions:banking.transactions}",
            groupId = "${spring.kafka.consumer.group-id:banking-core-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEventoTransacao(TransactionEvent event) {
        log.info("================ [KAFKA EVENT CONSUMED] ================");
        log.info("Evento Recebido ID: {}", event.transactionId());
        log.info("Conta: {}", event.numeroConta());
        log.info("Tipo: {}", event.tipo());
        log.info("Valor: R$ {}", event.valor());
        log.info("Saldo Resultante: R$ {}", event.saldoResultante());
        log.info("Descrição: {}", event.descricao());
        if (event.contaContraparte() != null) {
            log.info("Contraparte: {}", event.contaContraparte());
        }
        log.info("Data/Hora: {}", event.timestamp());
        log.info("Simulação de Auditoria e Notificação Assíncrona executada com sucesso.");
        log.info("========================================================");
    }
}
