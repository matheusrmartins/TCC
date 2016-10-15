package com.parse.starter.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.R;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        toolbar.setTitle("Chat");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        setSupportActionBar(toolbar);


    }

}
