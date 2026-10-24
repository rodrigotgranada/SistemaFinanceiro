package com.financial.banking.extratos.infrastructure.service;

import com.financial.banking.contas.infrastructure.persistence.Conta;
import com.financial.banking.contas.infrastructure.persistence.ContaRepository;
import com.financial.banking.extratos.api.ExtratoResponseDTO;
import com.financial.banking.movimentacoes.api.MovimentacaoResponseDTO;
import com.financial.banking.movimentacoes.infrastructure.persistence.Movimentacao;
import com.financial.banking.movimentacoes.infrastructure.persistence.MovimentacaoRepository;
import com.financial.banking.shared.exception.ContaNaoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ExtratoService {

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ExtratoService(ContaRepository contaRepository,
                          MovimentacaoRepository movimentacaoRepository) {
        this.contaRepository = contaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional(readOnly = true)
    public ExtratoResponseDTO consultarExtrato(String numeroConta, OffsetDateTime dataInicio, OffsetDateTime dataFim) {
        String cleanNumero = extrairNumeroConta(numeroConta);
        Conta conta = contaRepository.findByNumeroConta(cleanNumero)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));

        List<Movimentacao> movimentacoes;
        if (dataInicio != null && dataFim != null) {
            movimentacoes = movimentacaoRepository.findByContaIdAndPeriodo(conta.getId(), dataInicio, dataFim);
        } else {
            movimentacoes = movimentacaoRepository.findByContaIdOrderByDataHoraDesc(conta.getId());
        }

        List<MovimentacaoResponseDTO> dtos = movimentacoes.stream()
                .map(MovimentacaoResponseDTO::fromEntity)
                .toList();

        return new ExtratoResponseDTO(
                conta.getNumeroConta(),
                conta.getDigitoVerificador(),
                conta.getNumeroFormatado(),
                conta.getNomeTitular(),
                conta.getSaldo().setScale(2, RoundingMode.HALF_EVEN),
                dtos.size(),
                dtos
        );
    }

    private String extrairNumeroConta(String raw) {
        if (raw == null) return "";
        String[] partes = raw.trim().split("-");
        return partes[0].replaceAll("\\D", "");
    }
}
