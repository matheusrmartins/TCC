package br.com.petgoapp.model;

/**
 * Created by Matheus on 27/10/2016.
 */

public class Conversa {

    String object_id_usuario;
    String nome_usuario;
    String mensagem;

    public Conversa() {
    }

    public String getObject_id_usuario() {
        return object_id_usuario;
    }

    public void setObject_id_usuario(String object_id_usuario) {
        this.object_id_usuario = object_id_usuario;
    }

    public String getNome_usuario() {
        return nome_usuario;
    }

    public void setNome_usuario(String nome_usuario) {
        this.nome_usuario = nome_usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
