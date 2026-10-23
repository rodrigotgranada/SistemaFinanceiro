package com.financial.banking.movimentacoes.api;

import com.financial.banking.movimentacoes.infrastructure.persistence.Movimentacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

public record MovimentacaoResponseDTO(
        Long id,
        String tipo,
        BigDecimal valor,
        BigDecimal saldoResultante,
        String descricao,
        String contaContraparte,
        OffsetDateTime dataHora
) {
    public static MovimentacaoResponseDTO fromEntity(Movimentacao mov) {
        return new MovimentacaoResponseDTO(
                mov.getId(),
                mov.getTipo().name(),
                mov.getValor().setScale(2, RoundingMode.HALF_EVEN),
                mov.getSaldoResultante().setScale(2, RoundingMode.HALF_EVEN),
                mov.getDescricao(),
                mov.getContaContraparte(),
                mov.getDataHora()
        );
    }
}
