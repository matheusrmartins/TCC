package com.parse.starter.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.starter.R;
import com.squareup.picasso.Picasso;

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


    }


}
