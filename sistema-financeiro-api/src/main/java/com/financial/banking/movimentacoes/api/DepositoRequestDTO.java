package com.financial.banking.movimentacoes.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositoRequestDTO(
        @NotNull(message = "Valor do depósito é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor do depósito deve ser maior que zero")
        BigDecimal valor
) {}
