package com.financial.banking.movimentacoes.controller;

import com.financial.banking.movimentacoes.api.DepositoRequestDTO;
import com.financial.banking.movimentacoes.api.SaqueRequestDTO;
import com.financial.banking.movimentacoes.api.TransacaoSucessoResponseDTO;
import com.financial.banking.movimentacoes.api.TransferenciaRequestDTO;
import com.financial.banking.movimentacoes.infrastructure.service.DepositoService;
import com.financial.banking.movimentacoes.infrastructure.service.SaqueService;
import com.financial.banking.movimentacoes.infrastructure.service.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contas")
@CrossOrigin(origins = "*")
@Tag(name = "Movimentações Financeiras", description = "Endpoints de depósitos, saques e transferências")
public class MovimentacoesController {

    private final DepositoService depositoService;
    private final SaqueService saqueService;
    private final TransferenciaService transferenciaService;

    public MovimentacoesController(DepositoService depositoService,
            SaqueService saqueService,
            TransferenciaService transferenciaService) {
        this.depositoService = depositoService;
        this.saqueService = saqueService;
        this.transferenciaService = transferenciaService;
    }

    @PostMapping("/{numeroConta}/depositos")
    @Operation(summary = "Realizar depósito", description = "Credita valor na conta com registro no livro-razão e evento Kafka")
    public ResponseEntity<TransacaoSucessoResponseDTO> depositar(
            @PathVariable String numeroConta,
            @Valid @RequestBody DepositoRequestDTO request) {
        return ResponseEntity.ok(depositoService.depositar(numeroConta, request));
    }

    @PostMapping("/{numeroConta}/saques")
    @Operation(summary = "Realizar saque", description = "Debita valor da conta após validação de saldo suficiente com lock pessimista")
    public ResponseEntity<TransacaoSucessoResponseDTO> sacar(
            @PathVariable String numeroConta,
            @Valid @RequestBody SaqueRequestDTO request) {
        return ResponseEntity.ok(saqueService.sacar(numeroConta, request));
    }

    @PostMapping("/transferencias")
    @Operation(summary = "Realizar transferência", description = "Transferência entre contas com ordenação de locks para prevenção de deadlocks")
    public ResponseEntity<TransacaoSucessoResponseDTO> transferir(
            @Valid @RequestBody TransferenciaRequestDTO request) {
        return ResponseEntity.ok(transferenciaService.transferir(request));
    }
}
