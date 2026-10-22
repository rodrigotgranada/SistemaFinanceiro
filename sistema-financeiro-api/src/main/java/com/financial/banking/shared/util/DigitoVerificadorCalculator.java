package com.financial.banking.shared.util;

public final class DigitoVerificadorCalculator {

    private DigitoVerificadorCalculator() {}

    public static String calcularModulo11(String numeroConta) {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da conta não pode ser nulo ou vazio");
        }

        String limpo = numeroConta.replaceAll("\\D", "");
        int peso = 2;
        int soma = 0;

        for (int i = limpo.length() - 1; i >= 0; i--) {
            int digito = limpo.charAt(i) - '0';
            soma += digito * peso;
            peso++;
            if (peso > 9) {
                peso = 2;
            }
        }

        int resto = soma % 11;
        int dv = 11 - resto;

        if (dv == 10 || dv == 11) {
            return "0";
        }

        return String.valueOf(dv);
    }
}
