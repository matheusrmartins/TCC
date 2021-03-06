package br.com.petgoapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseUser;
import br.com.petgoapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.petgoapp.util.Erros;

public class CadastroPetActivity extends AppCompatActivity {

    private int estado_count_listener;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Button botao_continuar;
    private Button botao_foto;
    private EditText nome_animal;
    private RadioGroup lista_tipo;
    private String tipo;
    private RadioButton cachorro;
    private Spinner lista_raca;
    private EditText lista_ano;
    private EditText lista_mes;
    private RadioButton macho;
    private AutoCompleteTextView lista_cidade;
    private Spinner lista_estado;
    private EditText descricao;
    private boolean castrado_checked;
    byte[] imagem_byteArray;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String[] cidades;
    private ArrayAdapter<String> adapter;
    private EditText telefone_contato;
    private EditText nome_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pet);


        botao_continuar = (Button) findViewById(R.id.botao_continuar);
        botao_foto = (Button) findViewById(R.id.botao_foto);
        nome_animal = (EditText) findViewById(R.id.editText_nome_animal);
        lista_tipo = (RadioGroup) findViewById(R.id.radio_tipo);
        lista_raca = (Spinner) findViewById(R.id.spinner_lista_raca);
        lista_mes = (EditText) findViewById(R.id.lista_mes);
        lista_cidade = (AutoCompleteTextView) findViewById(R.id.lista_cidade);
        lista_estado = (Spinner) findViewById(R.id.spinner_lista_estado);
        lista_ano = (EditText) findViewById(R.id.lista_ano);
        descricao = (EditText) findViewById(R.id.editText_descricao);
        macho = (RadioButton) findViewById(R.id.radio_macho);
        cachorro = (RadioButton) findViewById(R.id.radio_cachorro);
        telefone_contato = (EditText) findViewById(R.id.editText_telefone);
        nome_usuario = (EditText) findViewById(R.id.editText_nome_usuario);

        try {
            nome_usuario.setText(ParseUser.getCurrentUser().get("nome").toString());
        } catch (Exception e){
            nome_usuario.setEnabled(true);
            nome_usuario.setVisibility(View.VISIBLE);
        }

        if (ParseUser.getCurrentUser().get("nome").toString().equals("Não Identificado")){
            nome_usuario.setText("");
            nome_usuario.setEnabled(true);
            nome_usuario.setVisibility(View.VISIBLE);
        }

        MaskEditTextChangedListener maskTELEFONE = new MaskEditTextChangedListener("(##) #####-####", telefone_contato);
        telefone_contato.addTextChangedListener(maskTELEFONE);


        toolbar = (Toolbar) findViewById(R.id.toolbar_cadastro_pet);
        toolbar.setTitle("Publicar Pet");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        setSupportActionBar(toolbar);

        ParseUser usuario = ParseUser.getCurrentUser();
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_estado));
        lista_estado.setSelection(spinnerArrayAdapter.getPosition(usuario.getString("lista_estado")));
        lista_cidade.setText(usuario.getString("lista_cidade"));

        telefone_contato.setText(usuario.getString("telefone"));

        estado_count_listener = 0;

        lista_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

        botao_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilhar();
            }
        });

        botao_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                            lista_ano_int+lista_mes_int,
                                            lista_raca.getSelectedItemPosition(),
                                            lista_estado.getSelectedItemPosition(),
                                            nome_usuario.getText().toString().trim());

                if (imagem_byteArray == null)
                    cod_erro = 108;

                if(Integer.valueOf(lista_ano_int) > 21)
                    cod_erro = 110;
                else if(Integer.valueOf(lista_mes_int) > 11)
                    cod_erro = 111;

                if ((telefone_contato.length() < 14) && (telefone_contato.length() > 0))
                    cod_erro = 112;

                if (cod_erro == 0) {
                    castrado_checked = ((CheckBox) findViewById(R.id.castrado)).isChecked();

                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("nome_usuario", nome_usuario.getText().toString().trim());
                        bundle.putString("nome_animal", nome_animal.getText().toString().trim());
                        bundle.putString("lista_genero", macho.isChecked() ? "Macho":"Fêmea");
                        bundle.putString("lista_tipo", cachorro.isChecked() ? "Cachorro":"Gato");
                        bundle.putString("lista_raca", lista_raca.getSelectedItem().toString());
                        bundle.putString("lista_ano", lista_ano_int);
                        bundle.putString("lista_mes", lista_mes_int);
                        bundle.putBoolean("castrado_checked", castrado_checked);
                        bundle.putString("lista_estado", lista_estado.getSelectedItem().toString());
                        bundle.putString("lista_cidade", lista_cidade.getText().toString().toUpperCase().trim());
                        bundle.putString("telefone_contato", telefone_contato.getText().toString());
                        bundle.putString("descricao", descricao.getText().toString().trim());
                        bundle.putByteArray("imagem", imagem_byteArray);
                        Intent intent = new Intent(CadastroPetActivity.this, VacinaActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(CadastroPetActivity.this, e.getMessage() + " Codigo: AND-" + e.hashCode(), Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Erros erros = new Erros();
                    Toast.makeText(CadastroPetActivity.this, erros.retornaMensagem("APP-"+cod_erro), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void compartilhar(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
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
        estado_count_listener++;
        if (lista_estado.getSelectedItem().toString().equals("AC")) {
            cidades = getResources().getStringArray(R.array.lista_cidade_AC);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("AL")){
            cidades = getResources().getStringArray(R.array.lista_cidade_AL);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("AP")){
            cidades = getResources().getStringArray(R.array.lista_cidade_AP);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("AM")){
            cidades = getResources().getStringArray(R.array.lista_cidade_AM);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("BA")){
            cidades = getResources().getStringArray(R.array.lista_cidade_BA);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("CE")){
            cidades = getResources().getStringArray(R.array.lista_cidade_CE);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("DF")){
            cidades = getResources().getStringArray(R.array.lista_cidade_DF);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("ES")){
            cidades = getResources().getStringArray(R.array.lista_cidade_ES);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("GO")){
            cidades = getResources().getStringArray(R.array.lista_cidade_GO);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MA")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MA);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MT")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MT);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MS")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MS);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("MG")){
            cidades = getResources().getStringArray(R.array.lista_cidade_MG);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PA")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PA);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PB")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PB);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PR")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PR);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PE")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PE);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("PI")){
            cidades = getResources().getStringArray(R.array.lista_cidade_PI);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RJ")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RJ);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RN")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RN);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RS")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RS);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RO")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RO);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("RR")){
            cidades = getResources().getStringArray(R.array.lista_cidade_RR);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("SC")){
            cidades = getResources().getStringArray(R.array.lista_cidade_SC);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("SP")){
            cidades = getResources().getStringArray(R.array.lista_cidade_SP);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("SE")){
            cidades = getResources().getStringArray(R.array.lista_cidade_SE);
            lista_cidade.setEnabled(true);
        }
        else if (lista_estado.getSelectedItem().toString().equals("TO")){
            cidades = getResources().getStringArray(R.array.lista_cidade_TO);
            lista_cidade.setEnabled(true);
        }
        else{
            lista_cidade.setText("");
            lista_cidade.setClickable(false);
            cidades = null;
        }


        if (lista_estado.getSelectedItemPosition() > 0) {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cidades);
            lista_cidade.setAdapter(adapter);
            lista_cidade.setThreshold(1);
        }else{
            lista_cidade.setAdapter(null);
        }


        if (estado_count_listener != 1)
            lista_cidade.setText(null);
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

    private final int verificaErro(int posicao_cidade,
                                   String nome_animal,
                                   String idade,
                                   int raca_position,
                                   int posicao_estado,
                                   String nome_usuario){

        if(nome_usuario.equals(""))
            return 115;
        else if(nome_animal.equals(""))
            return 107;
        else if (posicao_estado == 0)
            return 114;
        else if(posicao_cidade == -1)
            return 104;
        else if(idade.equals("0000"))
            return 109;
        else if (raca_position == 0)
            return 113;
        else
            return 0;
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
