package com.financial.banking.shared.kafka;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionEvent(
        String transactionId,
        String numeroConta,
        String tipo,
        BigDecimal valor,
        BigDecimal saldoResultante,
        String descricao,
        String contaContraparte,
        OffsetDateTime timestamp
) {}
