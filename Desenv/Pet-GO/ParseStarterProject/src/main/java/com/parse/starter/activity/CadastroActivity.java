package com.parse.starter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;
import com.parse.starter.util.Erros;

import java.util.List;

public class CadastroActivity extends AppCompatActivity {

    private EditText texto_email;
    private EditText texto_senha;
    private Spinner tipo_usuario;
    private Button botao_cadastrar;
    private int cod_erro = 0;
    private EditText cnpj;
    private EditText nome;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        texto_email     = (EditText) findViewById(R.id.editText_email);
        texto_senha     = (EditText) findViewById(R.id.editText_password);
        botao_cadastrar = (Button)   findViewById(R.id.button_cadastrar);
        tipo_usuario    = (Spinner)  findViewById(R.id.spinner_tipo_usuario);
        cnpj            = (EditText) findViewById(R.id.editText_cnpj);
        nome            = (EditText) findViewById(R.id.editText_nome);



        tipo_usuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tipo_usuario.getSelectedItem().toString().equals("Pessoa Física")) {
                    cnpj.setVisibility(View.INVISIBLE);
                    /*Mudar layout*/
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    p.addRule(RelativeLayout.BELOW, R.id.spinner_tipo_usuario);

                    texto_email.setLayoutParams(p);
                }
                else {
                    cnpj.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    p.addRule(RelativeLayout.BELOW, R.id.editText_cnpj);

                    texto_email.setLayoutParams(p);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    private int verificaErro(String email, String senha){

        if((email.trim().equals(null)) || (email.trim().equals(""))){
            return 102;
        }
        if(senha.length() < 6)
            return 101;
        if((tipo_usuario.getSelectedItem().toString().equals("Pessoa Jurídica")) && (cnpj.getText().toString().trim().length() == 0))
            return 103;
        else
            return 0;
    }


    private void cadastrarUsuario(){

        cod_erro = verificaErro(texto_email.getText().toString().toLowerCase(),
                                texto_senha.getText().toString());

        if (cod_erro == 0) {
            ParseUser usuario = new ParseUser();
            usuario.setUsername(texto_email.getText().toString().toLowerCase());
            usuario.setEmail(texto_email.getText().toString().toLowerCase());
            usuario.setPassword(texto_senha.getText().toString());
            usuario.put("nome", nome.getText().toString());
            usuario.put("tipo_usuario", tipo_usuario.getSelectedItem().toString());
            usuario.put("cnpj", cnpj.getText().toString());



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
