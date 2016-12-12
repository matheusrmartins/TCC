package br.com.petgoapp.util;

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
                return "As senhas não correspondem";
            case "APP-104":
                return "A cidade informada é inválida";
            case "APP-105":
                return "O nome é obrigatório";
            case "APP-106":
                return "O nome deve conter no máximo 30 caracteres";
            case "APP-107":
                return "O nome do animal é obrigatório";
            case "APP-108":
                return "A imagem do animal é obrigatória";
            case "APP-109":
                return "A idade é obrigatória";
            case "APP-110":
                return "O animal deve ter menos que 21 anos";
            case "APP-111":
                return "Digite um mês válido";
            case "APP-112":
                return "Digite um número de telefone válido";
            case "APP-113":
                return "Selecione uma raça";
            case "APP-114":
                return "Selecione o estado";
            case "APP-115":
                return "O nome do anunciante é obrigatório";
            case "PAR-101":
                return "E-mail/Senha incorretos.";
            case "PAR-200":
                return "O e-mail é obrigatório.";
            case "PAR-201":
                return "A senha é obrigatória.";
            case "PAR-202":
                return "Esse e-mail já está cadastrado.";
            case "PAR-203":
                return "Esse e-mail já está cadastrado.";
            case "PAR-125":
                return "Digite um e-mail válido.";
            case "PAR-100":
                return "Verifique sua conexão com a internet.";
            case "APP-300":
                return "Erro. Verifique sua conexão com a internet";
            default:
                return "Ocorreu um erro inesperado";

        }
    }
}
