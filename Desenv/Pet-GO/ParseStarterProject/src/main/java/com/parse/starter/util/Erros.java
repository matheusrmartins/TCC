package com.parse.starter.util;

/**
 * Created by Matheus on 16/08/2016.
 */

public class Erros {
    public String retornaMensagem(String codigo) {
        switch (codigo){
            case "APP-100":
                return "O usuário deve conter no mínimo 5 caracteres.";
            case "APP-101":
                return "A senha deve conter no mínimo 6 caracteres";
            case "APP-102":
                return "O e-mail é obrigatório";
            case "APP-103":
                return "O CNPJ é obrigatório";
            case "APP-104":
                return "A cidade informada é inválida";
            case "PAR-202":
                return "Esse usuário já está cadastrado.";
            case "PAR-203":
                return "Esse e-mail já está cadastrado.";
            case "PAR-125":
                return "Digite um e-mail válido.";
            default:
                return "Ocorreu um erro inesperado";
        }
    }
}
