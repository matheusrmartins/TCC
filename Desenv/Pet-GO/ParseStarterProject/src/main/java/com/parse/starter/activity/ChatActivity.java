package com.parse.starter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.adapter.MensagemAdapter;
import com.parse.starter.model.Conversa;
import com.parse.starter.model.Mensagem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText_mensagem;
    private ImageButton botao_enviar;
    private String usuarioDestinatario;
    private String usuarioRementente;
    private ListView listView_mensagens;
    private ArrayAdapter<Mensagem> arrayAdapter;
    private ArrayList<Mensagem> array_mensagens;
    private ParseQuery<ParseObject> query_mensagens;
    private ParseQuery<ParseObject> query_mensagens1;
    private ParseQuery<ParseObject> query_mensagens2;

    private ParseQuery<ParseObject> query_conversas;
    private ParseQuery<ParseObject> query_conversas1;
    private ParseQuery<ParseObject> query_conversas2;
    private boolean retorno;
    private Conversa conversa;
    private String nome_usuario;
    private String textoMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Destinatario
        usuarioDestinatario = getIntent().getExtras().getString("object_id_usuario");
        nome_usuario = getIntent().getExtras().getString("nome_usuario");

        //Remetente
        usuarioRementente = ParseUser.getCurrentUser().getObjectId();

        toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        try {
            toolbar.setTitle(nome_usuario == null ? "Chat" : nome_usuario);
        }
        catch (Exception e){
            toolbar.setTitle("Chat");
        }

        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        editText_mensagem = (EditText) findViewById(R.id.editText_mensagem);
        botao_enviar = (ImageButton) findViewById(R.id.botao_enviar);
        listView_mensagens = (ListView) findViewById(R.id.listView_mensagens);

        array_mensagens = new ArrayList<>();

        arrayAdapter =  new MensagemAdapter(ChatActivity.this, array_mensagens);
        listView_mensagens.setAdapter(arrayAdapter);

        array_mensagens.clear();

        query_mensagens1 = ParseQuery.getQuery("Mensagem");
        query_mensagens1.whereEqualTo("usuario_remetente", usuarioRementente);
        query_mensagens1.whereEqualTo("usuario_destinatario", usuarioDestinatario);
        query_mensagens2 = ParseQuery.getQuery("Mensagem");
        query_mensagens2.whereEqualTo("usuario_remetente", usuarioDestinatario);
        query_mensagens2.whereEqualTo("usuario_destinatario", usuarioRementente);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query_mensagens1);
        queries.add(query_mensagens2);

        query_mensagens = ParseQuery.or(queries);

        query_mensagens.orderByAscending("createdAt");
        query_mensagens.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object : objects){
                        Mensagem mensagem = new Mensagem();
                        mensagem.setMensagem(object.getString("mensagem"));
                        mensagem.setIdUsuario(object.getString("usuario_remetente"));
                        array_mensagens.add(mensagem);
                    }
                    listView_mensagens.invalidateViews();
                }else{

                }
            }
        });

        botao_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textoMensagem = editText_mensagem.getText().toString();

                //apaga mensagem do edittext
                editText_mensagem.setText("");

                if (!textoMensagem.isEmpty()){

                    Mensagem mensagem = new Mensagem();
                    mensagem.setMensagem(textoMensagem);
                    mensagem.setIdUsuario(usuarioRementente);
                    array_mensagens.add(mensagem);
                    if(!salvarMensagemParse (usuarioRementente, usuarioDestinatario, textoMensagem));
                    /*if (!result_mensagem){

                    }else{
                        try {
                            query_mensagens1 = ParseQuery.getQuery("Mensagem");
                            query_mensagens1.whereEqualTo("usuario_remetente", usuarioRementente);
                            query_mensagens1.whereEqualTo("usuario_destinatario", usuarioDestinatario);
                            query_mensagens2 = ParseQuery.getQuery("Mensagem");
                            query_mensagens2.whereEqualTo("usuario_remetente", usuarioDestinatario);
                            query_mensagens2.whereEqualTo("usuario_destinatario", usuarioRementente);

                            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                            queries.add(query_mensagens1);
                            queries.add(query_mensagens2);

                            query_mensagens = ParseQuery.or(queries);

                            query_mensagens.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e==  null) {
                                        if (objects.size() < 2) {
                                            ParseObject parseObject = new ParseObject("Conversa");
                                            parseObject.put("usuario_1", usuarioRementente);
                                            parseObject.put("usuario_2", usuarioDestinatario);
                                            parseObject.put("ultima_mensagem", textoMensagem);
                                            parseObject.saveInBackground();
                                        } else {
                                            query_mensagens1 = ParseQuery.getQuery("Conversa");
                                            query_mensagens1.whereEqualTo("usuario_1", usuarioRementente);
                                            query_mensagens1.whereEqualTo("usuario_2", usuarioDestinatario);
                                            query_mensagens2 = ParseQuery.getQuery("Conversa");
                                            query_mensagens2.whereEqualTo("usuario_1", usuarioDestinatario);
                                            query_mensagens2.whereEqualTo("usuario_2", usuarioRementente);

                                            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                                            queries.add(query_mensagens1);
                                            queries.add(query_mensagens2);

                                            query_mensagens = ParseQuery.or(queries);

                                            query_mensagens.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    if (e == null) {
                                                        for (ParseObject object : objects) {
                                                            object.put("ultima_mensagem", textoMensagem);
                                                            object.saveInBackground();
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }
                            });
                            if (query_mensagens.count() < 1){

                            }else{

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }*/

                }

            }
        });

        setSupportActionBar(toolbar);


    }

    private boolean salvarMensagemParse(final String usuarioRementente, final String usuarioDestinatario, final String textoMensagem) {

        try{

            retorno = false;

            ParseObject parseObject = new ParseObject("Mensagem");
            parseObject.put("usuario_remetente", usuarioRementente);
            parseObject.put("usuario_destinatario", usuarioDestinatario);
            parseObject.put("mensagem", textoMensagem);

            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){

                        retorno = true;

                    }
                    else{

                        retorno = false;

                    }
                }
            });

            query_conversas1 = ParseQuery.getQuery("Conversa");
            query_conversas1.whereEqualTo("usuario_1", usuarioRementente);
            query_conversas1.whereEqualTo("usuario_2", usuarioDestinatario);
            query_conversas2 = ParseQuery.getQuery("Conversa");
            query_conversas2.whereEqualTo("usuario_1", usuarioDestinatario);
            query_conversas2.whereEqualTo("usuario_2", usuarioRementente);

            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(query_conversas1);
            queries.add(query_conversas2);

            query_conversas = ParseQuery.or(queries);

            query_conversas.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e==  null) {
                        if (objects.size() < 1) {
                            ParseObject parseObject_conversa = new ParseObject("Conversa");
                            parseObject_conversa.put("usuario_1", usuarioRementente);
                            parseObject_conversa.put("usuario_2", usuarioDestinatario);
                            parseObject_conversa.put("nome_1", ParseUser.getCurrentUser().get("nome"));
                            parseObject_conversa.put("nome_2", nome_usuario);
                            parseObject_conversa.put("ultima_mensagem", textoMensagem);

                            parseObject_conversa.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null){

                                        retorno = true;

                                    }
                                    else{

                                        retorno = false;

                                    }
                                }
                            });
                        } else {
                            for (ParseObject object : objects) {
                              object.put("ultima_mensagem", textoMensagem);
                              object.saveInBackground();
                            }
                        }
                    }
                }
            });

            return retorno;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

}
