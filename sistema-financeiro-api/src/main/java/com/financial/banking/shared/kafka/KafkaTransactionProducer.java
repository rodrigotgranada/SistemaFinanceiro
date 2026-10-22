package com.financial.banking.shared.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaTransactionProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaTransactionProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;

    public KafkaTransactionProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                    @Value("${banking.kafka.topic.transactions:banking.transactions}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void publicar(TransactionEvent event) {
        log.info("Publicando evento de transação no Kafka [Tópico: {}]: {}", topicName, event);
        try {
            kafkaTemplate.send(topicName, event.numeroConta(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Evento entregue com sucesso no Kafka [Tópico: {}, Partition: {}, Offset: {}]",
                                    topicName,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Falha ao entregar evento de transação no Kafka: {}", ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.warn("Falha ao disparar envio para Kafka: {}", e.getMessage());
        }
    }
}
