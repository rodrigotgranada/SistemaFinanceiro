package com.financial.banking.extratos.api;

import com.financial.banking.movimentacoes.api.MovimentacaoResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record ExtratoResponseDTO(
        String numeroConta,
        String digitoVerificador,
        String numeroFormatado,
        String nomeTitular,
        BigDecimal saldoAtual,
        int totalMovimentacoes,
        List<MovimentacaoResponseDTO> movimentacoes
) {}
