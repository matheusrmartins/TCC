package com.parse.starter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText_mensagem;
    private Button botao_enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        try {
            toolbar.setTitle(getIntent().getExtras().getString("nome_usuario"));
        }
        catch (Exception e){
            toolbar.setTitle("Chat");
        }

        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        editText_mensagem = (EditText) findViewById(R.id.editText_mensagem);
        botao_enviar = (Button) findViewById(R.id.button_mensagem);



        setSupportActionBar(toolbar);


    }

}
