package com.financial.banking.movimentacoes.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_movimentacao")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoMovimentacao tipo;

    @Column(name = "valor", nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;

    @Column(name = "saldo_resultante", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldoResultante;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "conta_contraparte", length = 30)
    private String contaContraparte;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    public Movimentacao() {}

    public Movimentacao(Long id, Long contaId, TipoMovimentacao tipo, BigDecimal valor,
                        BigDecimal saldoResultante, String descricao,
                        String contaContraparte, OffsetDateTime dataHora) {
        this.id = id;
        this.contaId = contaId;
        this.tipo = tipo;
        this.valor = valor;
        this.saldoResultante = saldoResultante;
        this.descricao = descricao;
        this.contaContraparte = contaContraparte;
        this.dataHora = dataHora;
    }

    public static Movimentacao criar(Long contaId, TipoMovimentacao tipo, BigDecimal valor,
                                     BigDecimal saldoResultante, String descricao, String contaContraparte) {
        return new Movimentacao(
                null,
                contaId,
                tipo,
                valor,
                saldoResultante,
                descricao,
                contaContraparte,
                OffsetDateTime.now()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldoResultante() {
        return saldoResultante;
    }

    public void setSaldoResultante(BigDecimal saldoResultante) {
        this.saldoResultante = saldoResultante;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getContaContraparte() {
        return contaContraparte;
    }

    public void setContaContraparte(String contaContraparte) {
        this.contaContraparte = contaContraparte;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(OffsetDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
