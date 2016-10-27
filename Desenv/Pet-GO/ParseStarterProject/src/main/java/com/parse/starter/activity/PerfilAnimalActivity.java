package com.parse.starter.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PerfilAnimalActivity  extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imagem;
    private TextView descricao;
    private TextView tipo_genero;
    private TextView raca;
    private TextView idade;
    private TextView castrado;
    private TextView cidade_estado;
    private TextView text_vacinas;
    private String[] vacinas;
    private Button botao_chat;
    private Button botao_telefone;
    private String object_id_usuario;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_animal);

        toolbar = (Toolbar) findViewById(R.id.toolbar_perfil_animal);
        toolbar.setTitle(getIntent().getExtras().getString("nome_animal"));
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        setSupportActionBar(toolbar);


        imagem = (ImageView) findViewById(R.id.imagem_perfil);
        Picasso.with(this)
                .load(getIntent().getExtras().getString("imagem"))
                .fit()
                .centerInside()
                .into(imagem);

        descricao = (TextView) findViewById(R.id.textView_descricao);
        descricao.setText(getIntent().getExtras().getString("descricao"));

        tipo_genero = (TextView) findViewById(R.id.textView_tipo_genero);
        tipo_genero.setText(getIntent().getExtras().getString("lista_tipo") + " " +
                            getIntent().getExtras().getString("lista_genero"));

        raca = (TextView) findViewById(R.id.textView_raca);
        raca.setText(getIntent().getExtras().getString("lista_raca"));

        idade = (TextView) findViewById(R.id.textView_idade);
        idade.setText("");

        if (!getIntent().getExtras().getString("lista_ano").equals("00")) {
            idade.append((getIntent().getExtras().getString("lista_ano").equals("01")) ? getIntent().getExtras().getString("lista_ano").toString() + " ano"
                    : getIntent().getExtras().getString("lista_ano").toString() + " anos");
        }

        if ((!getIntent().getExtras().getString("lista_ano").equals("00")) && (!getIntent().getExtras().getString("lista_mes").equals("00")))
            idade.append(" e ");

        if (!getIntent().getExtras().getString("lista_mes").equals("00")) {
            idade.append(getIntent().getExtras().getString("lista_mes").toString());
            idade.append((getIntent().getExtras().getString("lista_mes").equals("01")) ? " mês" : " meses");
        }

        castrado = (TextView) findViewById(R.id.textView_castrado);
        castrado.setText((getIntent().getExtras().getString("castrado_checked").equals("N"))?
                          "Não castrado":"Castrado");

        cidade_estado = (TextView) findViewById(R.id.textView_cidade_estado);
        cidade_estado.setText("De " + getIntent().getExtras().getString("lista_cidade") + ", "+
                              getIntent().getExtras().getString("lista_estado"));

        vacinas = getIntent().getExtras().getString("vacinas").split(";");

        text_vacinas = (TextView) findViewById(R.id.textView_vacinas);

        text_vacinas.setText("");

        if (vacinas.length > 0) {
            for (String vacina : vacinas) {
                text_vacinas.append(vacina+"\n");
            }
        }

        botao_chat = (Button) findViewById(R.id.button_mensagem);
        botao_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object_id_usuario = getIntent().getExtras().getString("object_id_usuario");
                progressDialog =  new ProgressDialog(PerfilAnimalActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Abrindo chat...");
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                progressDialog.show();
                Intent intent = new Intent(PerfilAnimalActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_usuario", getIntent().getExtras().getString("usuario_nome"));
                bundle.putString("email_usuario", getIntent().getExtras().getString("usuario_email"));
                bundle.putString("object_id_usuario", getIntent().getExtras().getString("object_id_usuario"));
                intent.putExtras(bundle);
                startActivity(intent);
                progressDialog.dismiss();

                    }
                });



        botao_telefone = (Button) findViewById(R.id.button_telefone);
        botao_telefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PerfilAnimalActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                              ActivityCompat.requestPermissions(PerfilAnimalActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                101);

                } else {

                    chamarLigacao("13991076260");

                }
            }
        });


    }

    private void chamarLigacao(String telefone){
        Intent intentLigar = new Intent(Intent.ACTION_DIAL);
        intentLigar.setData(Uri.parse("tel:"+telefone));
        startActivity(intentLigar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    chamarLigacao("13991076260");
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
