package com.financial.banking.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testes do Validador de CPF e CNPJ")
class CpfCnpjValidatorTest {

    @Test
    @DisplayName("Deve validar CPF válido formatado e não formatado")
    void deveValidarCpfValido() {
        assertTrue(CpfCnpjValidator.isValid("52998224725"));
        assertTrue(CpfCnpjValidator.isValid("529.982.247-25"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF inválido com dígitos verificadores incorretos")
    void deveRejeitarCpfInvalido() {
        assertFalse(CpfCnpjValidator.isValid("11111111111"));
        assertFalse(CpfCnpjValidator.isValid("12345678900"));
        assertFalse(CpfCnpjValidator.isValid(null));
    }

    @Test
    @DisplayName("Deve validar CNPJ válido")
    void deveValidarCnpjValido() {
        assertTrue(CpfCnpjValidator.isValid("11444777000161"));
        assertTrue(CpfCnpjValidator.isValid("11.444.777/0001-61"));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ inválido")
    void deveRejeitarCnpjInvalido() {
        assertFalse(CpfCnpjValidator.isValid("00000000000000"));
        assertFalse(CpfCnpjValidator.isValid("11444777000199"));
    }
}
