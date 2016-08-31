package com.parse.starter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.starter.R;

public class EditarPetActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Button botao_continuar;
    private Button botao_foto;
    private EditText nome_animal;
    private Spinner lista_tipo;
    private Spinner lista_genero;
    private Spinner lista_raca;
    private Spinner lista_ano;
    private Spinner lista_mes;
    private Spinner lista_cidade;
    private Spinner lista_estado;
    private EditText descricao;
    private boolean castrado_checked;
    byte[] imagem_byteArray;
    private ArrayAdapter<String> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pet);

        toolbar = (Toolbar) findViewById(R.id.toolbar_editar_pet);
        toolbar.setTitle("Editar Pet");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        setSupportActionBar(toolbar);


        botao_continuar = (Button) findViewById(R.id.botao_continuar);
        botao_foto = (Button) findViewById(R.id.botao_foto);
        nome_animal = (EditText) findViewById(R.id.editText_nome_animal);
        lista_tipo = (Spinner) findViewById(R.id.spinner_lista_Tipo);
        lista_raca = (Spinner) findViewById(R.id.spinner_lista_raca);
        lista_mes = (Spinner) findViewById(R.id.spinner_lista_mes);
        lista_genero = (Spinner) findViewById(R.id.spinner_lista_Genero);
        lista_cidade = (Spinner) findViewById(R.id.spinner_lista_cidade);
        lista_estado = (Spinner) findViewById(R.id.spinner_lista_estado);
        lista_ano = (Spinner) findViewById(R.id.spinner_lista_ano);
        descricao = (EditText) findViewById(R.id.editText_descricao);

        nome_animal.setText(getIntent().getExtras().getString("nome_animal"));


    }
}
