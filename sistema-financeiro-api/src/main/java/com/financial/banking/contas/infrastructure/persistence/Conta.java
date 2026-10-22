package com.financial.banking.contas.infrastructure.persistence;

import com.financial.banking.shared.exception.ContaInativaException;
import com.financial.banking.shared.exception.SaldoInsuficienteException;
import com.financial.banking.shared.exception.ValorInvalidoException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "tb_conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta", nullable = false, length = 20, unique = true)
    private String numeroConta;

    @Column(name = "digito_verificador", nullable = false, length = 2)
    private String digitoVerificador;

    @Column(name = "cpf_cnpj", nullable = false, length = 18)
    private String cpfCnpj;

    @Column(name = "nome_titular", nullable = false, length = 150)
    private String nomeTitular;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 4)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusConta status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Conta() {
        this.saldo = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN);
        this.status = StatusConta.ATIVA;
    }

    public Conta(Long id, String numeroConta, String digitoVerificador, String cpfCnpj,
                 String nomeTitular, BigDecimal saldo, StatusConta status,
                 OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.digitoVerificador = digitoVerificador;
        this.cpfCnpj = cpfCnpj;
        this.nomeTitular = nomeTitular;
        this.saldo = saldo != null ? saldo.setScale(4, RoundingMode.HALF_EVEN) : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN);
        this.status = status != null ? status : StatusConta.ATIVA;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void validarContaAtiva() {
        if (this.status != StatusConta.ATIVA) {
            throw new ContaInativaException(this.getNumeroFormatado(), this.status.name());
        }
    }

    public void depositar(BigDecimal valor) {
        validarContaAtiva();
        validarValorOperacao(valor);

        this.saldo = this.saldo.add(valor.setScale(4, RoundingMode.HALF_EVEN));
        this.updatedAt = OffsetDateTime.now();
    }

    public void sacar(BigDecimal valor) {
        validarContaAtiva();
        validarValorOperacao(valor);

        BigDecimal valorAjustado = valor.setScale(4, RoundingMode.HALF_EVEN);
        if (this.saldo.compareTo(valorAjustado) < 0) {
            throw new SaldoInsuficienteException(this.saldo.setScale(2, RoundingMode.HALF_EVEN), valor.setScale(2, RoundingMode.HALF_EVEN));
        }

        this.saldo = this.saldo.subtract(valorAjustado);
        this.updatedAt = OffsetDateTime.now();
    }

    private void validarValorOperacao(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw ValorInvalidoException.valorNaoPositivo(valor != null ? valor : BigDecimal.ZERO);
        }
    }

    public String getNumeroFormatado() {
        return String.format("%s-%s", this.numeroConta, this.digitoVerificador);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public StatusConta getStatus() {
        return status;
    }

    public void setStatus(StatusConta status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id) || Objects.equals(numeroConta, conta.numeroConta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroConta);
    }
}
