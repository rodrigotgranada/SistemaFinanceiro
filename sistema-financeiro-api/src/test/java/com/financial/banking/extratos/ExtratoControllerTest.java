package com.financial.banking.extratos;

import com.financial.banking.extratos.api.ExtratoResponseDTO;
import com.financial.banking.extratos.controller.ExtratoController;
import com.financial.banking.extratos.infrastructure.service.ExtratoService;
import com.financial.banking.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExtratoController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Testes de Integração WebMvc do ExtratoController")
class ExtratoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExtratoService extratoService;

    @Test
    @DisplayName("GET /api/v1/contas/{numeroConta}/extrato - Deve retornar extrato e 200 OK")
    void deveConsultarExtratoComSucesso() throws Exception {
        ExtratoResponseDTO response = new ExtratoResponseDTO(
                "10001", "3", "10001-3", "Cliente Teste",
                new BigDecimal("500.00"), 0, Collections.emptyList()
        );

        when(extratoService.consultarExtrato(eq("10001"), any(), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/contas/10001/extrato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroConta").value("10001"))
                .andExpect(jsonPath("$.saldoAtual").value(500.00));
    }
}
