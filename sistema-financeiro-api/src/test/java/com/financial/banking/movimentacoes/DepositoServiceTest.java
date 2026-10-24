package com.financial.banking.movimentacoes;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.contas.infrastructure.persistence.StatusConta;
import com.financial.banking.movimentacoes.api.DepositoRequestDTO;
import com.financial.banking.movimentacoes.api.TransacaoSucessoResponseDTO;
import com.financial.banking.movimentacoes.infrastructure.persistence.MovimentacaoRepository;
import com.financial.banking.movimentacoes.infrastructure.service.DepositoService;
import com.financial.banking.shared.kafka.KafkaTransactionProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Depósito")
class DepositoServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private KafkaTransactionProducer kafkaProducer;

    @InjectMocks
    private DepositoService depositoService;

    @Test
    @DisplayName("Deve realizar depósito com sucesso e atualizar saldo")
    void deveRealizarDepositoComSucesso() {
        Conta conta = new Conta(
                1L, "10001", "8", "52998224725", "Cliente Teste",
                new BigDecimal("500.00"), StatusConta.ATIVA, OffsetDateTime.now(), OffsetDateTime.now()
        );

        DepositoRequestDTO request = new DepositoRequestDTO(new BigDecimal("150.00"));

        when(contaRepository.findByNumeroContaWithLock("10001")).thenReturn(Optional.of(conta));

        TransacaoSucessoResponseDTO response = depositoService.depositar("10001", request);

        assertTrue(response.sucesso());
        assertEquals(new BigDecimal("650.00"), response.saldoAtual());
        verify(contaRepository, times(1)).save(conta);
        verify(movimentacaoRepository, times(1)).save(any());
    }
}
