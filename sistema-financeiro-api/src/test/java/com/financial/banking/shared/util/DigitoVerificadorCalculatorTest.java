package com.financial.banking.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testes do Cálculo de Dígito Verificador Módulo 11")
class DigitoVerificadorCalculatorTest {

    @Test
    @DisplayName("Deve calcular dígito verificador módulo 11 corretamente")
    void deveCalcularDigitoVerificadorCorretamente() {
        String dv = DigitoVerificadorCalculator.calcularModulo11("10001");
        assertEquals("3", dv);
    }

    @Test
    @DisplayName("Deve lançar exceção para número de conta nulo ou vazio")
    void deveLancarExcecaoContaVazia() {
        assertThrows(IllegalArgumentException.class, () -> DigitoVerificadorCalculator.calcularModulo11(""));
        assertThrows(IllegalArgumentException.class, () -> DigitoVerificadorCalculator.calcularModulo11(null));
    }
}
