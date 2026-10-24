package com.financial.banking.movimentacoes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.banking.movimentacoes.api.DepositoRequestDTO;
import com.financial.banking.movimentacoes.api.SaqueRequestDTO;
import com.financial.banking.movimentacoes.api.TransacaoSucessoResponseDTO;
import com.financial.banking.movimentacoes.api.TransferenciaRequestDTO;
import com.financial.banking.movimentacoes.controller.MovimentacoesController;
import com.financial.banking.movimentacoes.infrastructure.service.DepositoService;
import com.financial.banking.movimentacoes.infrastructure.service.SaqueService;
import com.financial.banking.movimentacoes.infrastructure.service.TransferenciaService;
import com.financial.banking.shared.exception.GlobalExceptionHandler;
import com.financial.banking.shared.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovimentacoesController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Testes de Integração WebMvc do MovimentacoesController")
class MovimentacoesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepositoService depositoService;

    @MockBean
    private SaqueService saqueService;

    @MockBean
    private TransferenciaService transferenciaService;

    @Test
    @DisplayName("POST /api/v1/contas/{numeroConta}/depositos - Deve realizar depósito e retornar 200 OK")
    void deveDepositarComSucesso() throws Exception {
        DepositoRequestDTO request = new DepositoRequestDTO(new BigDecimal("100.00"));
        TransacaoSucessoResponseDTO response = new TransacaoSucessoResponseDTO(
                true, "DEPOSITO", "Depósito realizado com sucesso", "proto-123", "10001-3",
                new BigDecimal("100.00"), new BigDecimal("100.00"), OffsetDateTime.now()
        );

        when(depositoService.depositar(eq("10001"), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/contas/10001/depositos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.protocolo").value("proto-123"));
    }

    @Test
    @DisplayName("POST /api/v1/contas/{numeroConta}/saques - Deve retornar 422 quando saldo insuficiente")
    void deveRetornar422QuandoSaldoInsuficiente() throws Exception {
        SaqueRequestDTO request = new SaqueRequestDTO(new BigDecimal("500.00"));

        when(saqueService.sacar(eq("10001"), any()))
                .thenThrow(new SaldoInsuficienteException(BigDecimal.ZERO, new BigDecimal("500.00")));

        mockMvc.perform(post("/api/v1/contas/10001/saques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Saldo Insuficiente"));
    }

    @Test
    @DisplayName("POST /api/v1/contas/transferencias - Deve realizar transferência e retornar 200 OK")
    void deveTransferirComSucesso() throws Exception {
        TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                "10001", "10002", new BigDecimal("50.00"), "Pix teste"
        );
        TransacaoSucessoResponseDTO response = new TransacaoSucessoResponseDTO(
                true, "TRANSFERENCIA", "Transferência realizada com sucesso", "proto-456", "10001-3",
                new BigDecimal("50.00"), new BigDecimal("950.00"), OffsetDateTime.now()
        );

        when(transferenciaService.transferir(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/contas/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.protocolo").value("proto-456"));
    }
}
