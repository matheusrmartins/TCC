package com.parse.starter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.util.Erros;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditarPetActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Button botao_atualizar;
    private Button botao_foto;
    private EditText nome_animal;
    private Spinner lista_raca;
    private EditText lista_ano;
    private EditText lista_mes;
    private RadioGroup lista_tipo;
    private RadioGroup lista_genero;
    private String tipo;
    private RadioButton cachorro;
    private RadioButton gato;
    private RadioButton macho;
    private RadioButton femea;
    private AutoCompleteTextView lista_cidade;
    private Spinner lista_estado;
    private EditText descricao;
    private CheckBox castrado;
    private byte[] imagem_byteArray;
    private ImageView imagemEditar;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private int estado_count_listener;
    private String[] cidades;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pet);

        toolbar = (Toolbar) findViewById(R.id.toolbar_editar_pet);
        toolbar.setTitle("Editar Pet");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        setSupportActionBar(toolbar);

        estado_count_listener = 0;

        botao_atualizar = (Button) findViewById(R.id.botao_atualizar);
        botao_foto = (Button) findViewById(R.id.botao_foto);
        botao_foto.setText("Mudar foto");
        botao_foto.setBackgroundColor(R.drawable.fundo_botao_vermelho);
        nome_animal = (EditText) findViewById(R.id.editText_nome_animal);
        lista_tipo = (RadioGroup) findViewById(R.id.radio_tipo);
        lista_genero = (RadioGroup) findViewById(R.id.radio_genero);
        lista_raca = (Spinner) findViewById(R.id.spinner_lista_raca);
        lista_mes = (EditText) findViewById(R.id.lista_mes);
        castrado = (CheckBox) findViewById(R.id.castrado);
        lista_cidade = (AutoCompleteTextView) findViewById(R.id.lista_cidade);
        lista_estado = (Spinner) findViewById(R.id.spinner_lista_estado);
        lista_ano = (EditText) findViewById(R.id.lista_ano);
        descricao = (EditText) findViewById(R.id.editText_descricao);
        macho = (RadioButton) findViewById(R.id.radio_macho);
        cachorro = (RadioButton) findViewById(R.id.radio_cachorro);
        femea = (RadioButton) findViewById(R.id.radio_femea);
        gato = (RadioButton) findViewById(R.id.radio_gato);

        botao_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilhar();
            }
        });


        lista_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estado_count_listener++;
                if (estado_count_listener != 1)
                    alteraSpinnerCidade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lista_tipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tipo = cachorro.isChecked() ? "Cachorro":"Gato";
                alteraSpinnerRaca();
            }
        });

        botao_atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressDialog = new ProgressDialog(EditarPetActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Atualizando animal...");
                progressDialog.show();

                int cod_erro;
                String lista_ano_int, lista_mes_int;

                lista_ano_int = lista_ano.getText().toString();
                lista_mes_int = lista_mes.getText().toString();

                if(lista_ano_int.length() == 0)
                    lista_ano_int = "00";
                else if(lista_ano_int.length() == 1)
                    lista_ano_int = "0" + lista_ano_int;

                if(lista_mes_int.length() == 0)
                    lista_mes_int = "00";
                else if(lista_mes_int.length() == 1)
                    lista_mes_int = "0" + lista_mes_int;

                cod_erro = verificaErro(adapter.getPosition(lista_cidade.getText().toString().toUpperCase().trim()),
                        nome_animal.getText().toString().trim(),
                        lista_ano_int+lista_mes_int);

                if(Integer.valueOf(lista_ano_int) > 21)
                    cod_erro = 110;
                else if(Integer.valueOf(lista_mes_int) > 11)
                    cod_erro = 111;

                if (cod_erro == 0) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Animal");
                    query.whereEqualTo("objectId", getIntent().getExtras().getString("objectId"));
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, com.parse.ParseException e) {
                            if (e == null) {
                                ParseObject object = objects.get(0);

                                if (imagem_byteArray != null) {
                                    //Define o nome da imagem a ser gravada no banco
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
                                    String nomeimagem = dateFormat.format(new Date());
                                    ParseFile parseFile = new ParseFile(ParseUser.getCurrentUser().getObjectId() + nomeimagem + ".png", imagem_byteArray);
                                    object.put("imagem", parseFile);
                                }
                                char animal_castrado = 'N';

                                if (castrado.isChecked()) {
                                    animal_castrado = 'S';
                                }

                                object.put("lista_cidade", lista_cidade.getText().toString().toUpperCase().trim());
                                object.put("lista_estado", lista_estado.getSelectedItem().toString());
                                object.put("lista_raca", lista_raca.getSelectedItem().toString());
                                object.put("lista_ano", lista_ano.getText().toString());
                                object.put("lista_mes", lista_mes.getText().toString());
                                object.put("descricao", descricao.getText().toString());
                                object.put("lista_genero", macho.isChecked() ? "Macho":"Fêmea");
                                object.put("lista_tipo", cachorro.isChecked() ? "Cachorro":"Gato");
                                object.put("castrado", "" + animal_castrado);
                                object.put("nome_animal", nome_animal.getText().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(com.parse.ParseException e) {
                                        if (e == null) {
                                            finish();
                                        } else {
                                            Erros erros = new Erros();
                                            Toast.makeText(EditarPetActivity.this,  erros.retornaMensagem("PAR-" + e.getCode()), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        }


                    });
                }
                else{
                    progressDialog.dismiss();
                    Erros erros = new Erros();
                    Toast.makeText(EditarPetActivity.this, erros.retornaMensagem("APP-"+cod_erro), Toast.LENGTH_SHORT).show();
                }
            }});


        // Atribuição de valores do parseObject para os objetos da tela
        nome_animal.setText(getIntent().getExtras().getString("nome_animal"));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.tipo_animal));
        if (getIntent().getExtras().getString("lista_tipo").equals("Cachorro"))
            cachorro.setChecked(true);
        else
            gato.setChecked(true);
        if (getIntent().getExtras().getString("lista_genero").equals("Macho"))
            macho.setChecked(true);
        else
            femea.setChecked(true);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_cachorro));
        lista_raca.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_raca")));
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_ano));
        lista_ano.setText(getIntent().getExtras().getString("lista_ano"));
        lista_mes.setText(getIntent().getExtras().getString("lista_mes"));
        if (getIntent().getExtras().getString("castrado_checked").equals("S"))
            castrado.setChecked(true);
        else
            castrado.setChecked(false);
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_estado));
        lista_estado.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_estado")));
        alteraSpinnerCidade();
        lista_cidade.setText(getIntent().getExtras().getString("lista_cidade"));
        descricao.setText(getIntent().getExtras().getString("descricao"));
        imagemEditar = (ImageView) findViewById(R.id.image_editar);
        Picasso.with(this)
                .load(getIntent().getExtras().getString("imagem"))
                .fit()
                .centerInside()
                .into(imagemEditar);




    }

    private void alteraSpinnerRaca(){
        if (tipo == "Cachorro") {
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_cachorro));
        }
        else if (tipo == "Gato"){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_gato));
        }
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista_raca.setAdapter(spinnerArrayAdapter);
    }

    private void alteraSpinnerCidade(){
        if (lista_estado.getSelectedItem().toString().equals("AC")) {
            cidades = getResources().getStringArray(R.array.lista_cidade_AC);
        }
        else if (lista_estado.getSelectedItem().toString().equals("AL")){
            cidades = getResources().getStringArray(R.array.lista_cidade_AL);
        }
        else if (lista_estado.getSelectedItem().toString().equals("AP")){
            cidades = getResources().getStringArray(R.array.lista_cidade_AP);
        }
        else if (lista_estado.getSelectedItem().toString().equals("AM")){
            cidades = getResources().getStringArray(R.array.lista_cidade_AM);
        }
        else if (lista_estado.getSelectedItem().toString().equals("BA")){
            cidades = getResources().getStringArray(R.array.lista_cidade_BA);
        }
        else if (lista_estado.getSelectedItem().toString().equals("CE")){
            cidades = getResources().getStringArray(R.array.lista_cidade_CE);
        }
        else if (lista_estado.getSelectedItem().toString().equals("DF")){
            cidades = getResources().getStringArray(R.array.lista_cidade_DF);
        }
        else if (lista_estado.getSelectedItem().toString().equals("ES")){
            cidades = getResources().getStringArray(R.array.lista_cidade_ES);
        }
        else if (lista_estado.getSelectedItem().toString().equals("GO")){
            cidades = getResources().getStringArray(R.array.lista_cidade_GO);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MA")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MA);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MT")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MT);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MS")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MS);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MG")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MG);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PA")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PA);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PB")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PB);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PR")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PR);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PE")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PE);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PI")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PI);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RJ")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RJ);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RN")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RN);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RS")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RS);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RO")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RO);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RR")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RR);
        }
        else if (lista_estado.getSelectedItem().toString().equals("SC")){
            cidades = getResources().getStringArray(R.array.lista_cidade_SC);
        }
        else if (lista_estado.getSelectedItem().toString().equals("SP")){
            cidades = getResources().getStringArray(R.array.lista_cidade_SP);
        }
        else if (lista_estado.getSelectedItem().toString().equals("SE")){
            cidades = getResources().getStringArray(R.array.lista_cidade_SE);
        }
        else if (lista_estado.getSelectedItem().toString().equals("TO")){
            cidades = getResources().getStringArray(R.array.lista_cidade_TO);
        }

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cidades);
        lista_cidade.setAdapter(adapter);
        lista_cidade.setThreshold(1);
        lista_cidade.setText(null);
    }

    private final int verificaErro(int posicao_cidade, String nome_animal, String idade){

        if(posicao_cidade == -1)
            return 104;
        else if(nome_animal.equals(""))
            return 107;
        else if(idade.equals("0000"))
            return 109;
        else
            return 0;
    }

    private void compartilhar(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide) {
        int orgHeight = bitmap.getHeight();
        int orgWidth = bitmap.getWidth();
        Log.d("Mine", "orgHeight="+orgHeight + " orgWidth="+orgWidth);

        int scaledWidth = (orgWidth >= orgHeight) ? maxSide : (int) ((float) maxSide * ((float) orgWidth / (float) orgHeight));
        int scaledHeight = (orgHeight >= orgWidth) ? maxSide : (int) ((float) maxSide * ((float) orgHeight / (float) orgWidth));
        Log.d("Mine", "scaledHeight="+scaledHeight + " scaledWidth="+scaledWidth);

        // create the scaled bitmap
        Bitmap scaledGalleryPic = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        return scaledGalleryPic;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            progressDialog = new ProgressDialog(EditarPetActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Carregando imagem...");
            progressDialog.show();
            Uri localImagem = data.getData(); //Recupera local da imagem
            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem); //Importando imagem
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagem = createScaledBitmapKeepingAspectRatio(imagem, 400);
                imagem.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imagem_byteArray = stream.toByteArray();
                imagemEditar.setImageBitmap(imagem);
                botao_foto.setText("Mudar foto");
                botao_foto.setBackgroundColor(R.drawable.fundo_botao_vermelho);
                progressDialog.dismiss();
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(EditarPetActivity.this, e.getMessage() + " Codigo: AND-" + e.hashCode(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
