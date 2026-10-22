package com.financial.banking.contas.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AbrirContaRequestDTO(
        @NotBlank(message = "CPF ou CNPJ é obrigatório")
        String cpfCnpj,

        @NotBlank(message = "Nome do titular é obrigatório")
        @Size(min = 3, max = 150, message = "Nome do titular deve ter entre 3 e 150 caracteres")
        String nomeTitular,

        String numeroConta
) {}
