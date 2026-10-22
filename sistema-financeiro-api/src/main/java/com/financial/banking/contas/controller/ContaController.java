package com.financial.banking.contas.controller;

import com.financial.banking.contas.api.AbrirContaRequestDTO;
import com.financial.banking.contas.api.ContaResponseDTO;
import com.financial.banking.contas.infrastructure.service.AbrirContaService;
import com.financial.banking.contas.infrastructure.service.BuscarContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contas")
@CrossOrigin(origins = "*")
@Tag(name = "Contas Correntes", description = "Endpoints para gerenciamento e consulta de contas")
public class ContaController {

    private final AbrirContaService abrirContaService;
    private final BuscarContaService buscarContaService;

    public ContaController(AbrirContaService abrirContaService, BuscarContaService buscarContaService) {
        this.abrirContaService = abrirContaService;
        this.buscarContaService = buscarContaService;
    }

    @PostMapping
    @Operation(summary = "Abertura de conta corrente", description = "Cria uma nova conta com validação de CPF/CNPJ e cálculo do dígito verificador")
    public ResponseEntity<ContaResponseDTO> abrirConta(@Valid @RequestBody AbrirContaRequestDTO request) {
        ContaResponseDTO response = abrirContaService.abrirConta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas", description = "Retorna lista de todas as contas correntes cadastradas")
    public ResponseEntity<List<ContaResponseDTO>> listarContas() {
        return ResponseEntity.ok(buscarContaService.listarTodas());
    }

    @GetMapping("/{numero}")
    @Operation(summary = "Buscar conta por número", description = "Busca detalhes de uma conta informando número com ou sem dígito verificador")
    public ResponseEntity<ContaResponseDTO> buscarPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(buscarContaService.buscarPorNumero(numero));
    }
}
