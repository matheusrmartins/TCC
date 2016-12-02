package com.parse.starter.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.parse.DeleteCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacinaActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private List<String> vacinas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacina);

        vacinas.clear();

        final LinearLayout attractedTo = (LinearLayout) findViewById(R.id.activity_vacina);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_vacinas);

        toolbar.setTitle("Selecione as vacinas");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String tipo_animal = getIntent().getExtras().getString("lista_tipo");

        if (tipo_animal.equals("Cachorro"))
        {
            criaCheckBox("V8", 0);
            criaCheckBox("V10", 1);
            criaCheckBox("Anti-rábica", 2);
            criaCheckBox("Giardiase", 3);
            criaCheckBox("Gripe Canina", 4);
        }
        else if (tipo_animal.equals("Gato"))
        {
            criaCheckBox("Anti-rábica", 0);
            criaCheckBox("Quíntupla", 1);
        }

        Button botao_cadastrar = new Button(VacinaActivity.this);
        botao_cadastrar.setText("Cadastrar");
        botao_cadastrar.setBackgroundResource(R.drawable.fundo_botao);
        attractedTo.addView(botao_cadastrar);

        botao_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(VacinaActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Publicando animal...");
                progressDialog.show();

                String lista_vacinas = "";

                final String nome_animal = getIntent().getExtras().getString("nome_animal");
                String lista_genero = getIntent().getExtras().getString("lista_genero");
                String lista_raca = getIntent().getExtras().getString("lista_raca");
                String lista_ano = getIntent().getExtras().getString("lista_ano");
                String lista_mes = getIntent().getExtras().getString("lista_mes");
                String lista_estado = getIntent().getExtras().getString("lista_estado");
                String lista_cidade = getIntent().getExtras().getString("lista_cidade");

                Geocoder gc = new Geocoder(VacinaActivity.this, new Locale("pt", "BR"));
                List<Address> addresses= null;
                Double latitude = 0.0;
                Double longitude = 0.0;
                try {
                    addresses = gc.getFromLocationName(lista_cidade.toUpperCase()+" "+lista_estado, 1);
                     latitude = addresses.get(0).getLatitude();
                     longitude = addresses.get(0).getLongitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String telefone_contato = getIntent().getExtras().getString("telefone_contato");
                String lista_tipo = getIntent().getExtras().getString("lista_tipo");
                final String descricao = getIntent().getExtras().getString("descricao");
                Boolean castrado_checked = getIntent().getExtras().getBoolean("castrado_checked");
                byte[] imagem = getIntent().getExtras().getByteArray("imagem");

                char castrado = 'N';

                if (castrado_checked)
                {
                    castrado = 'S';
                }

                //Define o nome da imagem a ser gravada no banco
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
                String nomeimagem = dateFormat.format( new Date());

                //Cria a imagem em um objeto ParseFile para ser enviado para o banco
                ParseFile parseFile = new ParseFile(ParseUser.getCurrentUser().getObjectId() + nomeimagem + ".png", imagem);

                //monta String de vacinas
                try {
                    for (String vacina : vacinas) {
                        if (vacina != null) {
                            lista_vacinas += vacina + ";";
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(VacinaActivity.this,  e.getMessage()+" Codigo: AND-"+ e.hashCode(), Toast.LENGTH_SHORT).show();
                }


                if (!telefone_contato.trim().equals("")){
                    ParseUser.getCurrentUser().put("telefone", telefone_contato);
                    ParseUser.getCurrentUser().saveEventually();
                }

                //Envia os objetos parse para o banco
                final ParseObject parseObject = new ParseObject("Animal");

                parseObject.put("object_id_usuario", ParseUser.getCurrentUser().getObjectId().toString());
                parseObject.put("nome_animal", nome_animal);
                parseObject.put("lista_genero", lista_genero);
                parseObject.put("lista_raca", lista_raca);
                parseObject.put("lista_ano", lista_ano);
                parseObject.put("lista_mes", lista_mes);
                parseObject.put("lista_estado", lista_estado);
                parseObject.put("lista_cidade", lista_cidade);
                parseObject.put("Latitude", latitude);
                parseObject.put("Longitude", longitude);
                parseObject.put("telefone", telefone_contato);
                parseObject.put("descricao", descricao);
                parseObject.put("imagem", parseFile);
                parseObject.put("vacinas", lista_vacinas);
                parseObject.put("castrado", ""+castrado);
                parseObject.put("lista_tipo", lista_tipo);
                parseObject.put("Likes", 0);
                parseObject.put("usuario_nome", ParseUser.getCurrentUser().get("nome"));
                parseObject.put("usuario_email", ParseUser.getCurrentUser().getEmail());


                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){

                            progressDialog.dismiss();

                            Toast.makeText(VacinaActivity.this,  "Sucesso ao publicar animal" , Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(VacinaActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(VacinaActivity.this,  e.getMessage()+" Codigo: PAR-"+ e.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void onBackPressed() {
        finish();
    }


    private void criaCheckBox(String texto, int id){
        final LinearLayout attractedTo = (LinearLayout) findViewById(R.id.activity_vacina);
        final CheckBox checkBox = new CheckBox(VacinaActivity.this);
        checkBox.setId(id);
        checkBox.setText(texto);
        attractedTo.addView(checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkBox.isChecked()) {
                        vacinas.add(vacinas.size(), checkBox.getText().toString());
                    } else {
                        vacinas.remove(checkBox.getText().toString());
                    }
                } catch (Exception e) {
                    Toast.makeText(VacinaActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
