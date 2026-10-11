package com.vendas.util;

public final class CpfValidator {

    private CpfValidator() {

    }

    public static String apenasNumeros(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }

    public static boolean isValido(String cpf) {
        String numeros = apenasNumeros(cpf);

        if (numeros.length() != 11) {
            return false;
        }
        if (numeros.chars().distinct().count() == 1) {

            return false;
        }

        int[] digitos = numeros.chars().map(c -> c - '0').toArray();

        int primeiroDigito = calcularDigitoVerificador(digitos, 9);
        if (primeiroDigito != digitos[9]) {
            return false;
        }

        int segundoDigito = calcularDigitoVerificador(digitos, 10);
        return segundoDigito == digitos[10];
    }

    private static int calcularDigitoVerificador(int[] digitos, int quantidade) {
        int soma = 0;
        int peso = quantidade + 1;
        for (int i = 0; i < quantidade; i++) {
            soma += digitos[i] * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}
