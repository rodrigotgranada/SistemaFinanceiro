package com.financial.banking.contas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financial.banking.contas.api.AbrirContaRequestDTO;
import com.financial.banking.contas.api.ContaResponseDTO;
import com.financial.banking.contas.controller.ContaController;
import com.financial.banking.contas.infrastructure.service.AbrirContaService;
import com.financial.banking.contas.infrastructure.service.BuscarContaService;
import com.financial.banking.shared.exception.ContaNaoEncontradaException;
import com.financial.banking.shared.exception.GlobalExceptionHandler;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Testes de Integração WebMvc do ContaController")
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AbrirContaService abrirContaService;

    @MockBean
    private BuscarContaService buscarContaService;

    @Test
    @DisplayName("POST /api/v1/contas - Deve abrir conta e retornar 201 Created")
    void deveAbrirContaComSucesso() throws Exception {
        AbrirContaRequestDTO request = new AbrirContaRequestDTO("52998224725", "Titular Teste", "10001");
        ContaResponseDTO response = new ContaResponseDTO(
                1L, "10001", "3", "10001-3", "52998224725", "Titular Teste",
                BigDecimal.ZERO, "ATIVA", OffsetDateTime.now()
        );

        when(abrirContaService.abrirConta(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroConta").value("10001"))
                .andExpect(jsonPath("$.numeroFormatado").value("10001-3"));
    }

    @Test
    @DisplayName("GET /api/v1/contas/{numero} - Deve retornar 200 OK quando a conta existe")
    void deveBuscarContaPorNumero() throws Exception {
        ContaResponseDTO response = new ContaResponseDTO(
                1L, "10001", "3", "10001-3", "52998224725", "Titular Teste",
                BigDecimal.ZERO, "ATIVA", OffsetDateTime.now()
        );

        when(buscarContaService.buscarPorNumero("10001")).thenReturn(response);

        mockMvc.perform(get("/api/v1/contas/10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConta").value("10001"));
    }

    @Test
    @DisplayName("GET /api/v1/contas/{numero} - Deve retornar 404 Not Found quando a conta não existe")
    void deveRetornar404QuandoContaNaoExiste() throws Exception {
        when(buscarContaService.buscarPorNumero("99999")).thenThrow(new ContaNaoEncontradaException("99999"));

        mockMvc.perform(get("/api/v1/contas/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Conta Corrente Não Encontrada"));
    }
}
