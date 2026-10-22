package com.financial.banking.contas.api;

import com.financial.banking.contas.infrastructure.persistence.Conta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

public record ContaResponseDTO(
        Long id,
        String numeroConta,
        String digitoVerificador,
        String numeroFormatado,
        String cpfCnpj,
        String nomeTitular,
        BigDecimal saldo,
        String status,
        OffsetDateTime createdAt
) {
    public static ContaResponseDTO fromEntity(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getDigitoVerificador(),
                conta.getNumeroFormatado(),
                conta.getCpfCnpj(),
                conta.getNomeTitular(),
                conta.getSaldo().setScale(2, RoundingMode.HALF_EVEN),
                conta.getStatus().name(),
                conta.getCreatedAt()
        );
    }
}
