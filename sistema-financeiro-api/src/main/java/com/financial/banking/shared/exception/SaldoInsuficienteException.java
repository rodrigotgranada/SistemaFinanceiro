package com.financial.banking.shared.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends BusinessException {
    public SaldoInsuficienteException(BigDecimal saldoDisponivel, BigDecimal valorSolicitado) {
        super(String.format("Saldo insuficiente para a operação. Saldo disponível: R$ %s, Valor solicitado: R$ %s",
                saldoDisponivel, valorSolicitado));
    }
}
