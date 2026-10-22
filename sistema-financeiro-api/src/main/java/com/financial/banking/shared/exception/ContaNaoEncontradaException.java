package com.financial.banking.shared.exception;

public class ContaNaoEncontradaException extends BusinessException {
    public ContaNaoEncontradaException(String numeroConta) {
        super(String.format("Conta corrente não encontrada: %s", numeroConta));
    }
}
