package com.financial.banking.contas;

import com.financial.banking.contas.api.AbrirContaRequestDTO;
import com.financial.banking.contas.api.ContaResponseDTO;
import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.contas.infrastructure.service.AbrirContaService;
import com.financial.banking.shared.exception.DocumentoInvalidoException;
import com.financial.banking.shared.kafka.KafkaTransactionProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Abertura de Conta")
class AbrirContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private KafkaTransactionProducer kafkaProducer;

    @InjectMocks
    private AbrirContaService abrirContaService;

    @Test
    @DisplayName("Deve abrir conta com documento válido e saldo zero")
    void deveAbrirContaComDocumentoValido() {
        AbrirContaRequestDTO request = new AbrirContaRequestDTO("52998224725", "João Silva", "10001");

        when(contaRepository.existsByNumeroConta("10001")).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContaResponseDTO response = abrirContaService.abrirConta(request);

        assertNotNull(response);
        assertEquals("10001", response.numeroConta());
        assertEquals(BigDecimal.ZERO.setScale(2), response.saldo());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve falhar abertura de conta com CPF inválido")
    void deveFalharAberturaComCpfInvalido() {
        AbrirContaRequestDTO request = new AbrirContaRequestDTO("11111111111", "João Silva", "10001");
        assertThrows(DocumentoInvalidoException.class, () -> abrirContaService.abrirConta(request));
        verify(contaRepository, never()).save(any());
    }
}
