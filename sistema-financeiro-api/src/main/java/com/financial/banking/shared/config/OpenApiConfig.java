package com.financial.banking.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Core Banking API - Gerenciamento de Contas e Movimentação Financeira")
                        .version("1.0.0")
                        .description("API REST para abertura de conta corrente, depósitos, saques, transferências atômicas e extrato com filtros temporais.")
                        .contact(new Contact()
                                .name("Equipe de Engenharia Financeira")
                                .email("engineering@financial.com"))
                        .license(new License().name("Apache 2.0")));
    }
}
