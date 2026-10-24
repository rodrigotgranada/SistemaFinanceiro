package com.financial.banking.extratos.controller;

import com.financial.banking.extratos.api.ExtratoResponseDTO;
import com.financial.banking.extratos.infrastructure.service.ExtratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/contas")
@CrossOrigin(origins = "*")
@Tag(name = "Extratos", description = "Endpoints para consulta de extrato bancário")
public class ExtratoController {

    private final ExtratoService extratoService;

    public ExtratoController(ExtratoService extratoService) {
        this.extratoService = extratoService;
    }

    @GetMapping("/{numeroConta}/extrato")
    @Operation(summary = "Consultar extrato bancário", description = "Retorna histórico contábil de movimentações com filtro opcional por período")
    public ResponseEntity<ExtratoResponseDTO> consultarExtrato(
            @PathVariable String numeroConta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataFim) {
        return ResponseEntity.ok(extratoService.consultarExtrato(numeroConta, dataInicio, dataFim));
    }
}
