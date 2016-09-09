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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private byte[] imagem_byteArray;
    private ImageView imagemEditar;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private int count_listener_estado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pet);

        toolbar = (Toolbar) findViewById(R.id.toolbar_editar_pet);
        toolbar.setTitle("Editar Pet");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        setSupportActionBar(toolbar);

        count_listener_estado = 0;

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

        botao_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilhar();
            }
        });


        lista_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                count_listener_estado++;
                if (count_listener_estado > 1)
                alteraSpinnerCidade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        botao_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Animal");
                query.whereEqualTo("lista_cidade", getIntent().getExtras().getString("lista_cidade"));
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {
                            progressDialog = new ProgressDialog(EditarPetActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Atualizando animal...");
                            progressDialog.show();

                            ParseObject object = objects.get(0);
                            //Define o nome da imagem a ser gravada no banco
                            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
                            String nomeimagem = dateFormat.format( new Date());
                            ParseFile parseFile = new ParseFile(ParseUser.getCurrentUser().getObjectId() + nomeimagem + ".png", imagem_byteArray);

                            char animal_castrado = 'N';

                            if (castrado.isChecked())
                            {
                                animal_castrado = 'S';
                            }

                            object.put("imagem", parseFile);
                            object.put("lista_cidade", lista_cidade.getSelectedItem().toString());
                            object.put("lista_estado",lista_estado.getSelectedItem().toString());
                            object.put("lista_raca",lista_raca.getSelectedItem().toString());
                            object.put("lista_ano",lista_ano.getSelectedItem().toString());
                            object.put("lista_mes",lista_mes.getSelectedItem().toString());
                            object.put("descricao",descricao.getText().toString());
                            object.put("lista_genero",lista_genero.getSelectedItem().toString());
                            object.put("lista_tipo",lista_tipo.getSelectedItem().toString());
                            object.put("castrado", ""+animal_castrado);
                            object.put("nome_animal", nome_animal.getText().toString());
                            object.saveInBackground();

                            finish();
                            Intent activity = new Intent(EditarPetActivity.this, EditarActivity.class);
                            startActivity(activity);
                            progressDialog.dismiss();
                        }
                    }

                });
            }});


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

        if (getIntent().getExtras().getString("lista_estado").equals("AC")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AC));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("AL")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AL));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("AP")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AP));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("AM")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AM));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("BA")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_BA));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("CE")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_CE));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("DF")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_DF));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("ES")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_ES));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("GO")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_GO));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("MA")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MA));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("MT")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MT));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("MS")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MS));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("MG")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MG));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("PA")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PA));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("PB")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PB));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("PR")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PR));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("PE")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PE));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("PI")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PI));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("RJ")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RJ));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("RN")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RN));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("RS")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RS));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("RO")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RO));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("RR")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RR));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("SC")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_SC));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("SP")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_SP));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("SE")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_SE));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }
        else if (getIntent().getExtras().getString("lista_estado").equals("TO")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_TO));
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lista_cidade.setAdapter(spinnerArrayAdapter);
        }

        lista_cidade.setSelection(spinnerArrayAdapter.getPosition(getIntent().getExtras().getString("lista_cidade")));
        descricao.setText(getIntent().getExtras().getString("descricao"));
        imagemEditar = (ImageView) findViewById(R.id.image_editar);
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

    private void alteraSpinnerCidade(){
        if (lista_estado.getSelectedItem().toString().equals("AC")) {
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AC));
        }
        else if (lista_estado.getSelectedItem().toString().equals("AL")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AL));
        }
        else if (lista_estado.getSelectedItem().toString().equals("AP")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AP));
        }
        else if (lista_estado.getSelectedItem().toString().equals("AM")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_AM));
        }
        else if (lista_estado.getSelectedItem().toString().equals("BA")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_BA));
        }
        else if (lista_estado.getSelectedItem().toString().equals("CE")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_CE));
        }
        else if (lista_estado.getSelectedItem().toString().equals("DF")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_DF));
        }
        else if (lista_estado.getSelectedItem().toString().equals("ES")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_ES));
        }
        else if (lista_estado.getSelectedItem().toString().equals("GO")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_GO));
        }
        else if (lista_estado.getSelectedItem().toString().equals("MA")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MA));
        }
        else if (lista_estado.getSelectedItem().toString().equals("MT")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MT));
        }
        else if (lista_estado.getSelectedItem().toString().equals("MS")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MS));
        }
        else if (lista_estado.getSelectedItem().toString().equals("MG")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_MG));
        }
        else if (lista_estado.getSelectedItem().toString().equals("PA")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PA));
        }
        else if (lista_estado.getSelectedItem().toString().equals("PB")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PB));
        }
        else if (lista_estado.getSelectedItem().toString().equals("PR")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PR));
        }
        else if (lista_estado.getSelectedItem().toString().equals("PE")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PE));
        }
        else if (lista_estado.getSelectedItem().toString().equals("PI")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_PI));
        }
        else if (lista_estado.getSelectedItem().toString().equals("RJ")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RJ));
        }
        else if (lista_estado.getSelectedItem().toString().equals("RN")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RN));
        }
        else if (lista_estado.getSelectedItem().toString().equals("RS")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RS));
        }
        else if (lista_estado.getSelectedItem().toString().equals("RO")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RO));
        }
        else if (lista_estado.getSelectedItem().toString().equals("RR")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_RR));
        }
        else if (lista_estado.getSelectedItem().toString().equals("SC")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_SC));
        }
        else if (lista_estado.getSelectedItem().toString().equals("SP")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_SP));
        }
        else if (lista_estado.getSelectedItem().toString().equals("SE")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_SE));
        }
        else if (lista_estado.getSelectedItem().toString().equals("TO")){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_cidade_TO));
        }

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista_cidade.setAdapter(spinnerArrayAdapter);
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
