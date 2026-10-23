package com.financial.banking.movimentacoes.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaqueRequestDTO(
        @NotNull(message = "Valor do saque é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor do saque deve ser maior que zero")
        BigDecimal valor
) {}
