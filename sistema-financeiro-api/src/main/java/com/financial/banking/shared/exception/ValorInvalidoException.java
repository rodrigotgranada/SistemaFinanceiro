package com.financial.banking.shared.exception;

import java.math.BigDecimal;

public class ValorInvalidoException extends BusinessException {

    public ValorInvalidoException(String message) {
        super(message);
    }

    public static ValorInvalidoException valorNaoPositivo(BigDecimal valor) {
        return new ValorInvalidoException(String.format("O valor da operação deve ser estritamente positivo e maior que zero. Informado: %s", valor));
    }
}
