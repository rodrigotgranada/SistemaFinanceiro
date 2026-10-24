package com.financial.banking.contas;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.StatusConta;
import com.financial.banking.shared.exception.ContaInativaException;
import com.financial.banking.shared.exception.SaldoInsuficienteException;
import com.financial.banking.shared.exception.ValorInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários da Entidade Conta")
class ContaTest {

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta(
                1L, "10001", "3", "52998224725", "João Silva",
                new BigDecimal("1000.00"), StatusConta.ATIVA, OffsetDateTime.now(), OffsetDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve depositar valor positivo com sucesso")
    void deveDepositarComSucesso() {
        conta.depositar(new BigDecimal("250.00"));
        assertEquals(new BigDecimal("1250.0000"), conta.getSaldo());
    }

    @Test
    @DisplayName("Deve sacar valor quando houver saldo suficiente")
    void deveSacarComSucesso() {
        conta.sacar(new BigDecimal("300.00"));
        assertEquals(new BigDecimal("700.0000"), conta.getSaldo());
    }

    @Test
    @DisplayName("Deve lançar SaldoInsuficienteException ao tentar sacar valor maior que o saldo")
    void deveLancarExcecaoSaldoInsuficiente() {
        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(new BigDecimal("1500.00")));
    }

    @Test
    @DisplayName("Deve falhar depósito com valor zero ou negativo")
    void deveFalharDepositoValorInvalido() {
        assertThrows(ValorInvalidoException.class, () -> conta.depositar(BigDecimal.ZERO));
        assertThrows(ValorInvalidoException.class, () -> conta.depositar(new BigDecimal("-50.00")));
    }

    @Test
    @DisplayName("Deve falhar saque com valor zero ou negativo")
    void deveFalharSaqueValorInvalido() {
        assertThrows(ValorInvalidoException.class, () -> conta.sacar(BigDecimal.ZERO));
        assertThrows(ValorInvalidoException.class, () -> conta.sacar(new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Deve lançar ContaInativaException ao operar com conta bloqueada ou encerrada")
    void deveFalharOperacaoComContaInativa() {
        conta.setStatus(StatusConta.BLOQUEADA);
        assertThrows(ContaInativaException.class, () -> conta.depositar(new BigDecimal("100.00")));
        assertThrows(ContaInativaException.class, () -> conta.sacar(new BigDecimal("100.00")));
    }
}
