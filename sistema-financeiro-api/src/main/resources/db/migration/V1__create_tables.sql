CREATE TABLE IF NOT EXISTS tb_conta (
    id BIGSERIAL PRIMARY KEY,
    numero_conta VARCHAR(20) NOT NULL,
    digito_verificador VARCHAR(2) NOT NULL,
    cpf_cnpj VARCHAR(18) NOT NULL,
    nome_titular VARCHAR(150) NOT NULL,
    saldo NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_conta_numero_dv UNIQUE (numero_conta, digito_verificador)
);

CREATE INDEX IF NOT EXISTS idx_conta_numero ON tb_conta(numero_conta);
CREATE INDEX IF NOT EXISTS idx_conta_cpf_cnpj ON tb_conta(cpf_cnpj);

CREATE TABLE IF NOT EXISTS tb_movimentacao (
    id BIGSERIAL PRIMARY KEY,
    conta_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    valor NUMERIC(19, 4) NOT NULL,
    saldo_resultante NUMERIC(19, 4) NOT NULL,
    descricao VARCHAR(255),
    conta_contraparte VARCHAR(30),
    data_hora TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimentacao_conta FOREIGN KEY (conta_id) REFERENCES tb_conta(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_movimentacao_conta_data ON tb_movimentacao(conta_id, data_hora DESC);
