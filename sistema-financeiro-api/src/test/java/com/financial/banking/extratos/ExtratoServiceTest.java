package com.financial.banking.extratos;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.contas.infrastructure.persistence.StatusConta;
import com.financial.banking.extratos.api.ExtratoResponseDTO;
import com.financial.banking.extratos.infrastructure.service.ExtratoService;
import com.financial.banking.movimentacoes.infrastructure.persistence.MovimentacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Extrato Bancário")
class ExtratoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @InjectMocks
    private ExtratoService extratoService;

    @Test
    @DisplayName("Deve consultar extrato com sucesso e retornar saldo consolidado")
    void deveConsultarExtratoComSucesso() {
        Conta conta = new Conta(
                1L, "10001", "8", "52998224725", "Cliente Teste",
                new BigDecimal("500.00"), StatusConta.ATIVA, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(contaRepository.findByNumeroConta("10001")).thenReturn(Optional.of(conta));
        when(movimentacaoRepository.findByContaIdOrderByDataHoraDesc(1L)).thenReturn(Collections.emptyList());

        ExtratoResponseDTO extrato = extratoService.consultarExtrato("10001", null, null);

        assertNotNull(extrato);
        assertEquals("10001", extrato.numeroConta());
        assertEquals(new BigDecimal("500.00"), extrato.saldoAtual());
        assertEquals(0, extrato.totalMovimentacoes());
    }
}
