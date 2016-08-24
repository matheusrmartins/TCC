package com.parse.starter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;
import com.parse.starter.util.Erros;

public class CadastroActivity extends AppCompatActivity {

    private EditText texto_usuario;
    private EditText texto_email;
    private EditText texto_senha;
    private Button botao_cadastrar;
    private int cod_erro = 0;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        texto_usuario   = (EditText) findViewById(R.id.editText_nome_animal);
        texto_email     = (EditText) findViewById(R.id.editText_raca_animal);
        texto_senha     = (EditText) findViewById(R.id.editText_password);
        botao_cadastrar = (Button)   findViewById(R.id.button_cadastrar);

        botao_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog =  new ProgressDialog(CadastroActivity.this);
                progressDialog.setMessage("Realizando o cadastro...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                cadastrarUsuario();
            }
        });
    }

    private int verificaErro(String usuario, String email, String senha){
        if(usuario.length() < 5)
            return 100;
        if(senha.length() < 6)
            return 101;
        else
            return 0;
    }

    private void cadastrarUsuario(){

        cod_erro = verificaErro(texto_usuario.getText().toString().toLowerCase(),
                                texto_email.getText().toString().toLowerCase(),
                                texto_senha.getText().toString());

        if (cod_erro == 0) {
            ParseUser usuario = new ParseUser();
            usuario.setUsername(texto_usuario.getText().toString().toLowerCase());
            usuario.setEmail(texto_email.getText().toString().toLowerCase());
            usuario.setPassword(texto_senha.getText().toString());

            usuario.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        abrirLoginUsuario();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CadastroActivity.this, e.getMessage()+" Codigo : PAR-"+ e.getCode(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Erros erros = new Erros();
            progressDialog.dismiss();
            Toast.makeText(CadastroActivity.this, erros.retornaMensagem("APP-"+cod_erro), Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
