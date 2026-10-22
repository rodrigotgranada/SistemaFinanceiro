package com.financial.banking.shared.util;

public final class CpfCnpjValidator {

    private CpfCnpjValidator() {}

    public static String normalizar(String documento) {
        if (documento == null) return null;
        return documento.replaceAll("\\D", "");
    }

    public static boolean isValid(String documento) {
        String limpo = normalizar(documento);
        if (limpo == null) return false;

        if (limpo.length() == 11) {
            return isValidCpf(limpo);
        } else if (limpo.length() == 14) {
            return isValidCnpj(limpo);
        }
        return false;
    }

    private static boolean isValidCpf(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;
            if (digito1 != (cpf.charAt(9) - '0')) return false;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            return digito2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidCnpj(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        try {
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += (cnpj.charAt(i) - '0') * peso1[i];
            }
            int digito1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);
            if (digito1 != (cnpj.charAt(12) - '0')) return false;

            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += (cnpj.charAt(i) - '0') * peso2[i];
            }
            int digito2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

            return digito2 == (cnpj.charAt(13) - '0');
        } catch (Exception e) {
            return false;
        }
    }
}
