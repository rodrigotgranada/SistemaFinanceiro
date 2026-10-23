package com.financial.banking.movimentacoes.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransacaoSucessoResponseDTO(
        boolean sucesso,
        String tipo,
        String mensagem,
        String protocolo,
        String numeroConta,
        BigDecimal valor,
        BigDecimal saldoAtual,
        OffsetDateTime timestamp
) {}
