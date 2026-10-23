package com.financial.banking.movimentacoes.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findByContaIdOrderByDataHoraDesc(Long contaId);

    @Query("SELECT m FROM Movimentacao m WHERE m.contaId = :contaId AND m.dataHora >= :dataInicio AND m.dataHora <= :dataFim ORDER BY m.dataHora DESC")
    List<Movimentacao> findByContaIdAndPeriodo(
            @Param("contaId") Long contaId,
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim
    );
}
