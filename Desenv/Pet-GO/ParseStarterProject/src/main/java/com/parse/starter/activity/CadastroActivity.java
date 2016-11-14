package com.parse.starter.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;
import com.parse.starter.util.Erros;
import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;

public class CadastroActivity extends AppCompatActivity {

    private EditText texto_email;
    private EditText texto_senha;
    private EditText texto_senha_redig;
    private Button botao_cadastrar;
    private int cod_erro = 0;
    private EditText nome;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        texto_email       = (EditText) findViewById(R.id.editText_email);
        texto_senha       = (EditText) findViewById(R.id.editText_password);
        botao_cadastrar   = (Button)   findViewById(R.id.button_cadastrar);
        texto_senha_redig = (EditText) findViewById(R.id.editText_password_retype);
        nome              = (EditText) findViewById(R.id.editText_nome);


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

    private int verificaErro(String email, String senha, String nome, String senha_redig){

        if((email.trim().equals(null)) || (email.trim().equals("")))
            return 102;
        else if(senha.length() < 6)
            return 101;
        else  if (!senha_redig.equals(senha))
            return 103;
        else if(nome.trim().length() < 1)
            return 105;
        else if(nome.trim().length() > 30)
            return 106;

        else
            return 0;
    }


    private void cadastrarUsuario(){

        cod_erro = verificaErro(texto_email.getText().toString().toLowerCase(),
                                texto_senha.getText().toString(),
                                nome.getText().toString(),
                                texto_senha_redig.getText().toString());

        if (cod_erro == 0) {
            ParseUser usuario = new ParseUser();
            usuario.setUsername(texto_email.getText().toString().toLowerCase());
            usuario.setEmail(texto_email.getText().toString().toLowerCase());
            usuario.setPassword(texto_senha.getText().toString());
            usuario.put("nome", nome.getText().toString());

            usuario.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

                        StringBuilder sqlClientes = new StringBuilder();
                        sqlClientes.append("DELETE FROM usuario;");
                        db.execSQL(sqlClientes.toString());

                        ContentValues ctv = new ContentValues();
                        ctv.put("email", texto_email.getText().toString());

                        db.insert("usuario","id",ctv);
                        abrirLoginUsuario();
                    } else {
                        progressDialog.dismiss();
                        Erros erros = new Erros();
                        Toast.makeText(CadastroActivity.this, erros.retornaMensagem("PAR-"+ e.getCode()), Toast.LENGTH_SHORT).show();
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
