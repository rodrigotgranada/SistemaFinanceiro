package com.financial.banking.shared.exception;

public class TransferenciaMesmaContaException extends BusinessException {
    public TransferenciaMesmaContaException() {
        super("Conta de origem e conta de destino não podem ser iguais");
    }
}
