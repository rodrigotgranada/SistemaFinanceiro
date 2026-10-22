package com.financial.banking.shared.exception;

public class DocumentoInvalidoException extends BusinessException {
    public DocumentoInvalidoException(String documento) {
        super(String.format("CPF ou CNPJ informado é inválido: %s", documento));
    }
}
