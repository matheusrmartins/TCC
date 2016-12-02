package com.parse.starter.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.parse.starter.R;

import java.util.Random;

public class MarcaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marca);

        RelativeLayout layout =(RelativeLayout)findViewById(R.id.activity_marca);

        Random gerador = new Random();

        int numero = gerador.nextInt(6);

        switch (numero) {
            case 0:
                layout.setBackgroundResource(R.drawable.fundo_marca_1);
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.fundo_marca_2);
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.fundo_marca_3);
                break;
            case 3:
                layout.setBackgroundResource(R.drawable.fundo_marca_4);
                break;
            case 4:
                layout.setBackgroundResource(R.drawable.fundo_marca_5);
                break;
            case 5:
                layout.setBackgroundResource(R.drawable.fundo_marca_6);
                break;
        }

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                // Esse método será executado sempre que o timer acabar
                // E inicia a activity principal
                Intent i = new Intent(MarcaActivity.this, LoginActivity.class);
                startActivity(i);

                // Fecha esta activity
                finish();
            }
        }, 3000);
    }


}
