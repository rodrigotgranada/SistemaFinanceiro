package com.financial.banking.movimentacoes;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.contas.infrastructure.persistence.StatusConta;
import com.financial.banking.movimentacoes.api.TransacaoSucessoResponseDTO;
import com.financial.banking.movimentacoes.api.TransferenciaRequestDTO;
import com.financial.banking.movimentacoes.infrastructure.persistence.MovimentacaoRepository;
import com.financial.banking.movimentacoes.infrastructure.service.TransferenciaService;
import com.financial.banking.shared.exception.TransferenciaMesmaContaException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Transferência")
class TransferenciaServiceTest {

        @Mock
        private ContaRepository contaRepository;

        @Mock
        private MovimentacaoRepository movimentacaoRepository;

        @Mock
        private KafkaTransactionProducer kafkaProducer;

        @InjectMocks
        private TransferenciaService transferenciaService;

        @Test
        @DisplayName("Deve realizar transferência com sucesso")
        void deveRealizarTransferenciaComSucesso() {
                Conta contaOrigem = new Conta(
                                1L, "10001", "8", "52998224725", "Cliente Origem",
                                new BigDecimal("500.00"), StatusConta.ATIVA, OffsetDateTime.now(),
                                OffsetDateTime.now());
                Conta contaDestino = new Conta(
                                2L, "10002", "6", "11444777000161", "Cliente Destino",
                                new BigDecimal("100.00"), StatusConta.ATIVA, OffsetDateTime.now(),
                                OffsetDateTime.now());

                TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                                "10001", "10002", new BigDecimal("200.00"), "Pagamento");

                when(contaRepository.findByNumeroContaWithLock("10001")).thenReturn(Optional.of(contaOrigem));
                when(contaRepository.findByNumeroContaWithLock("10002")).thenReturn(Optional.of(contaDestino));

                TransacaoSucessoResponseDTO response = transferenciaService.transferir(request);

                assertTrue(response.sucesso());
                assertEquals(new BigDecimal("300.00"), contaOrigem.getSaldo().setScale(2));
                assertEquals(new BigDecimal("300.00"), contaDestino.getSaldo().setScale(2));
                verify(contaRepository, times(1)).save(contaOrigem);
                verify(contaRepository, times(1)).save(contaDestino);
                verify(movimentacaoRepository, times(2)).save(any());
        }

        @Test
        @DisplayName("Deve impedir transferência para a mesma conta")
        void deveImpedirTransferenciaMesmaConta() {
                TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                                "10001", "10001", new BigDecimal("50.00"), "Transferência");

                assertThrows(TransferenciaMesmaContaException.class, () -> transferenciaService.transferir(request));
                verify(contaRepository, never()).save(any());
        }
}
