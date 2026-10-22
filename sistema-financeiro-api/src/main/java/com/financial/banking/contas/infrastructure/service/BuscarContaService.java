package com.financial.banking.contas.infrastructure.service;

import com.financial.banking.contas.api.ContaResponseDTO;
import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.shared.exception.ContaNaoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuscarContaService {

    private final ContaRepository contaRepository;

    public BuscarContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Transactional(readOnly = true)
    public ContaResponseDTO buscarPorNumero(String rawNumero) {
        String numero = extrairNumero(rawNumero);
        Conta conta = contaRepository.findByNumeroConta(numero)
                .orElseThrow(() -> new ContaNaoEncontradaException(rawNumero));
        return ContaResponseDTO.fromEntity(conta);
    }

    @Transactional(readOnly = true)
    public List<ContaResponseDTO> listarTodas() {
        return contaRepository.findAll().stream()
                .map(ContaResponseDTO::fromEntity)
                .toList();
    }

    private String extrairNumero(String raw) {
        if (raw == null) return "";
        return raw.split("-")[0].replaceAll("\\D", "");
    }
}
