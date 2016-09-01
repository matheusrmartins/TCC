package com.parse.starter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.parse.starter.R;
import com.squareup.picasso.Picasso;

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
    private CheckBox castrado;
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
        castrado = (CheckBox) findViewById(R.id.castrado);
        lista_genero = (Spinner) findViewById(R.id.spinner_lista_Genero);
        lista_cidade = (Spinner) findViewById(R.id.spinner_lista_cidade);
        lista_estado = (Spinner) findViewById(R.id.spinner_lista_estado);
        lista_ano = (Spinner) findViewById(R.id.spinner_lista_ano);
        descricao = (EditText) findViewById(R.id.editText_descricao);

        lista_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alteraSpinnerCidade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lista_tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alteraSpinnerRaca();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Atribuição de valores do parseObject para os objetos da tela
        nome_animal.setText(getIntent().getExtras().getString("nome_animal"));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.tipo_animal));
        lista_tipo.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_tipo")));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.genero_animal));
        lista_genero.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_genero")));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_cachorro));
        lista_raca.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_raca")));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_ano));
        lista_ano.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_ano")));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_mes));
        lista_mes.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_mes")));
        if (getIntent().getExtras().getString("castrado_checked").equals("S"))
            castrado.setChecked(true);
        else
            castrado.setChecked(false);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_estado));
        lista_estado.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_estado")));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, alteraSpinnerCidade());
        lista_cidade.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_cidade")));
        descricao.setText(getIntent().getExtras().getString("descricao"));
        ImageView imagemEditar = (ImageView) findViewById(R.id.image_editar);
        Picasso.with(this)
                .load(getIntent().getExtras().getString("imagem"))
                .fit()
                .centerInside()
                .into(imagemEditar);

    }

    private void alteraSpinnerRaca(){
        if (lista_tipo.getSelectedItem().toString().equals("Cachorro")) {
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_cachorro));
        }
        else if (lista_tipo.getSelectedItem().toString().equals("Gato")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_gato));
        }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista_raca.setAdapter(spinnerArrayAdapter);
    }

    private int alteraSpinnerCidade(){
        int array_cidade = 0;
        if (lista_estado.getSelectedItem().toString().equals("AC")) {
            array_cidade = R.array.lista_cidade_AC;
        }
        else if (lista_estado.getSelectedItem().toString().equals("AL")){
            array_cidade = R.array.lista_cidade_AL;
        }
        else if (lista_estado.getSelectedItem().toString().equals("AP")){
            array_cidade = R.array.lista_cidade_AP;
        }
        else if (lista_estado.getSelectedItem().toString().equals("AM")){
            array_cidade = R.array.lista_cidade_AM;
        }
        else if (lista_estado.getSelectedItem().toString().equals("BA")){
            array_cidade = R.array.lista_cidade_BA;
        }
        else if (lista_estado.getSelectedItem().toString().equals("CE")){
            array_cidade = R.array.lista_cidade_CE;
        }
        else if (lista_estado.getSelectedItem().toString().equals("DF")){
            array_cidade = R.array.lista_cidade_DF;
        }
        else if (lista_estado.getSelectedItem().toString().equals("ES")){
            array_cidade = R.array.lista_cidade_ES;
        }
        else if (lista_estado.getSelectedItem().toString().equals("GO")){
            array_cidade = R.array.lista_cidade_GO;
        }
        else if (lista_estado.getSelectedItem().toString().equals("MA")){
            array_cidade = R.array.lista_cidade_MA;
        }
        else if (lista_estado.getSelectedItem().toString().equals("MT")){
            array_cidade = R.array.lista_cidade_MT;
        }
        else if (lista_estado.getSelectedItem().toString().equals("MS")){
            array_cidade = R.array.lista_cidade_MS;
        }
        else if (lista_estado.getSelectedItem().toString().equals("MG")){
            array_cidade = R.array.lista_cidade_MG;
        }
        else if (lista_estado.getSelectedItem().toString().equals("PA")){
            array_cidade = R.array.lista_cidade_PA;
        }
        else if (lista_estado.getSelectedItem().toString().equals("PB")){
            array_cidade = R.array.lista_cidade_PB;
        }
        else if (lista_estado.getSelectedItem().toString().equals("PR")){
            array_cidade = R.array.lista_cidade_PR;
        }
        else if (lista_estado.getSelectedItem().toString().equals("PE")){
            array_cidade = R.array.lista_cidade_PE;
        }
        else if (lista_estado.getSelectedItem().toString().equals("PI")){
            array_cidade = R.array.lista_cidade_PI;
        }
        else if (lista_estado.getSelectedItem().toString().equals("RJ")){
            array_cidade = R.array.lista_cidade_RJ;
        }
        else if (lista_estado.getSelectedItem().toString().equals("RN")){
            array_cidade = R.array.lista_cidade_RN;
        }
        else if (lista_estado.getSelectedItem().toString().equals("RS")){
            array_cidade = R.array.lista_cidade_RS;
        }
        else if (lista_estado.getSelectedItem().toString().equals("RO")){
            array_cidade = R.array.lista_cidade_RO;
        }
        else if (lista_estado.getSelectedItem().toString().equals("RR")){
            array_cidade = R.array.lista_cidade_RR;
        }
        else if (lista_estado.getSelectedItem().toString().equals("SC")){
            array_cidade = R.array.lista_cidade_SC;
        }
        else if (lista_estado.getSelectedItem().toString().equals("SP")){
            array_cidade = R.array.lista_cidade_SP;
        }
        else if (lista_estado.getSelectedItem().toString().equals("SE")){
            array_cidade = R.array.lista_cidade_SE;
        }
        else if (lista_estado.getSelectedItem().toString().equals("TO")){
            array_cidade = R.array.lista_cidade_TO;
        }

        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(array_cidade));


        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista_cidade.setAdapter(spinnerArrayAdapter);

        return array_cidade;
    }
}
