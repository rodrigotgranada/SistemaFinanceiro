package com.financial.banking.shared.exception;

public class ContaInativaException extends BusinessException {
    public ContaInativaException(String numeroConta, String status) {
        super(String.format("Conta corrente %s está com status %s e não permite operações financeiras", numeroConta, status));
    }
}
