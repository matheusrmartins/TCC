package com.parse.starter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.adapter.TabsAdapter;
import com.parse.starter.fragments.HomeFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CadastroPetActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_cadastro_pet);


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

        //lista_estado.setSelection(2);
        //lista_estado.getSelectedItemPosition();

        toolbar = (Toolbar) findViewById(R.id.toolbar_cadastro_pet);
        toolbar.setTitle("Publicar Pet");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);
        setSupportActionBar(toolbar);


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

        botao_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilhar();
            }
        });


        botao_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                castrado_checked=((CheckBox)findViewById(R.id.castrado)).isChecked();

                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("nome_animal", nome_animal.getText().toString());
                    bundle.putString("lista_genero", lista_genero.getSelectedItem().toString());
                    bundle.putString("lista_tipo", lista_tipo.getSelectedItem().toString());
                    bundle.putString("lista_raca", lista_raca.getSelectedItem().toString());
                    bundle.putString("lista_ano", lista_ano.getSelectedItem().toString());
                    bundle.putString("lista_mes", lista_mes.getSelectedItem().toString());
                    bundle.putBoolean("castrado_checked", castrado_checked);
                    bundle.putString("lista_estado", lista_estado.getSelectedItem().toString());
                    bundle.putString("lista_cidade", lista_cidade.getSelectedItem().toString());
                    bundle.putString("descricao", descricao.getText().toString());
                    bundle.putByteArray("imagem", imagem_byteArray);
                    Intent intent = new Intent(CadastroPetActivity.this, VacinaActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }catch(Exception e){
                    Toast.makeText(CadastroPetActivity.this,  e.getMessage() + " Codigo: AND-"+ e.hashCode(), Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void compartilhar(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            progressDialog = new ProgressDialog(CadastroPetActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Carregando imagem...");
            progressDialog.show();
            Uri localImagem = data.getData(); //Recupera local da imagem
            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem); //Importando imagem
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagem = createScaledBitmapKeepingAspectRatio(imagem,400);
                imagem.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imagem_byteArray = stream.toByteArray();
                botao_foto.setText("Mudar foto");
                botao_foto.setBackgroundColor(R.drawable.fundo_botao_vermelho);
                progressDialog.dismiss();
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(CadastroPetActivity.this,  e.getMessage() + " Codigo: AND-"+ e.hashCode(), Toast.LENGTH_SHORT).show();
            }


        }
    }


}
