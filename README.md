# Financial Account Core Platform

Sistema corporativo para gestão de contas correntes, controle de transacoes, transferências entre contas e extrato financeiro.

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
| :--- | :--- |
| **Linguagem Backend** | Java 17 |
| **Framework Backend** | Spring Boot 3 |
| **Frontend SPA** | React 18, Vite, Tailwind |
| **Banco de Dados** | PostgreSQL 16 |
| **Mensageria** | Kafka 3 |
| **Documentação** | Swagger |
| **Containerização** | Docker |
| **Testes Automatizados** | JUnit 5 |

---

## 📁 Estrutura do Repositório (Monorepo)

```text
SistemaFinanceiro/
├── sistema-financeiro-api/               # Aplicação Core Spring Boot 3
│   ├── src/main/java/com/financial/banking/
│   │   ├── contas/                       # Módulo de Contas Correntes
│   │   │   ├── api/                      # DTOs e contratos de entrada/saída
│   │   │   ├── controller/               # Endpoints REST (/api/v1/contas)
│   │   │   └── infrastructure/
│   │   │       ├── persistence/          # Entidade JPA (Conta), Enums e Repository com Lock Pessimista
│   │   │       └── service/              # Abertura e consulta de contas
│   │   ├── movimentacoes/                # Módulo de Movimentações Financeiras
│   │   │   ├── api/                      # DTOs de transação (depósito, saque, transferência)
│   │   │   ├── controller/               # Endpoints REST de operações transacionais
│   │   │   └── infrastructure/
│   │   │       ├── persistence/          # Entidade JPA (Movimentacao), Livro-Razão e Repository
│   │   │       └── service/              # Depósitos, saques e transferências sem deadlock
│   │   ├── extratos/                     # Módulo de Extratos Bancários
│   │   │   ├── api/                      # DTO consolidado de extrato
│   │   │   ├── controller/               # Endpoint REST (/api/v1/contas/{numero}/extrato)
│   │   │   └── infrastructure/
│   │   │       └── service/              # Consulta e filtros temporais
│   │   └── shared/                       # Componentes Compartilhados
│   │       ├── config/                   # OpenAPI / Swagger e configurações Spring
│   │       ├── exception/                # Hierarquia de exceções e RFC 7807 GlobalExceptionHandler
│   │       ├── kafka/                    # Produtor, Consumidor e Eventos de Domínio
│   │       └── util/                     # Validador de CPF/CNPJ e Dígito Verificador Módulo 11
│   ├── src/main/resources/
│   │   ├── db/migration/                 # Migrações Flyway SQL (tb_conta, tb_movimentacao)
│   │   └── application.yml               # Configurações de ambiente
│   ├── Dockerfile                        # Multi-stage build JRE 17 Alpine
│   └── pom.xml
├── frontend/                             # Aplicação Single Page Application (React + Tailwind)
│   ├── src/
│   │   ├── contas/                       # Componentes e hooks de Contas Correntes
│   │   ├── movimentacoes/                # Operações de Depósito, Saque e Transferência
│   │   ├── extratos/                     # Extrato Bancário e filtros por período
│   │   └── shared/                       # UI base, MoneyInput e textos centralizados
│   ├── Dockerfile                        # Multi-stage build Node 20 + Nginx Alpine
│   ├── nginx.conf                        # Proxy reverso para /api/v1
│   └── package.json
├── collections/                          # Coleções prontas para testes (Postman/Insomnia)
│   └── financial_account_api.postman_collection.json
├── docker-compose.yml                    # Orquestrador local (Postgres, Kafka, Backend e Frontend)
└── README.md
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Docker** e **Docker Compose** instalados na máquina.

### 1. Inicialização Completa com Docker Compose
Na raiz do projeto, execute:

```bash
docker compose up --build -d
```

Este comando monta os containers:
1. **PostgreSQL** na porta `5432`
2. **Pgweb** na porta `8081`
3. **Kafka** na porta `9092`
4. **Backend API** na porta `8080`
5. **Frontend** na porta `3000`

### 2. Acesso às Aplicações
- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Banco (Pgweb)**: [http://localhost:8081](http://localhost:8081)
- **Swagger**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 🧪 Execução dos Testes Automatizados

Para executar os testes no backend:

```bash
cd sistema-financeiro-api
./mvnw test
```

*(No Windows PowerShell: `.\mvnw.cmd test`)*

---

## 📖 Guia de Endpoints da API REST

### 1. Abertura de Conta Corrente
- **POST** `/api/v1/contas`
- **Body**:
```json
{
  "cpfCnpj": "52998224725",
  "nomeTitular": "Carlos Eduardo Silva",
  "numeroConta": "10001"
}
```
- **Retorno (201 Created)**:
```json
{
  "id": 1,
  "numeroConta": "10001",
  "digitoVerificador": "3",
  "numeroFormatado": "10001-3",
  "cpfCnpj": "52998224725",
  "nomeTitular": "Carlos Eduardo Silva",
  "saldo": 0.00,
  "status": "ATIVA",
  "createdAt": "2026-10-22T11:00:00Z"
}
```

### 2. Depósito em Conta
- **POST** `/api/v1/contas/{numeroConta}/depositos`
- **Body**:
```json
{
  "valor": 1500.00
}
```

### 3. Saque em Conta
- **POST** `/api/v1/contas/{numeroConta}/saques`
- **Body**:
```json
{
  "valor": 200.00
}
```
*Se o saldo for insuficiente, retorna `HTTP 422 Unprocessable Entity` detalhando o saldo disponível.*

### 4. Transferência entre Contas Correntes
- **POST** `/api/v1/contas/transferencias`
- **Body**:
```json
{
  "contaOrigem": "10001",
  "contaDestino": "10002",
  "valor": 350.00,
  "descricao": "Pagamento de fornecedor"
}
```

### 5. Consulta de Extrato Bancário
- **GET** `/api/v1/contas/{numeroConta}/extrato`
- **Filtros Opcionais de Período**:  
  `?dataInicio=2026-01-01T00:00:00Z&dataFim=2026-12-31T23:59:59Z`
- **Retorno (200 OK)**:
```json
{
  "numeroConta": "10001",
  "digitoVerificador": "3",
  "numeroFormatado": "10001-3",
  "nomeTitular": "Carlos Eduardo Silva",
  "saldoAtual": 950.00,
  "totalMovimentacoes": 3,
  "movimentacoes": [
    {
      "id": 3,
      "tipo": "TRANSFERENCIA_ENVIO",
      "descricaoTipo": "Transferência Enviada",
      "valor": 350.00,
      "saldoResultante": 950.00,
      "descricao": "Pagamento de fornecedor",
      "contaContraparte": "10002-6",
      "dataHora": "2026-10-22T11:05:00Z"
    }
  ]
}
```
