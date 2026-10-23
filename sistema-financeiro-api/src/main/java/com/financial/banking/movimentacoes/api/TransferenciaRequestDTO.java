package com.financial.banking.movimentacoes.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        @Schema(description = "Número da conta de origem (somente números ou formato conta-DV)", example = "10001")
        @NotBlank(message = "A conta de origem é obrigatória")
        String contaOrigem,

        @Schema(description = "Número da conta de destino (somente números ou formato conta-DV)", example = "10002")
        @NotBlank(message = "A conta de destino é obrigatória")
        String contaDestino,

        @Schema(description = "Valor a ser transferido (deve ser maior que zero)", example = "100.00")
        @NotNull(message = "O valor da transferência é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor da transferência deve ser no mínimo R$ 0,01")
        BigDecimal valor,

        @Schema(description = "Descrição opcional da transferência", example = "Pagamento de serviços")
        String descricao
) {}
