package com.parse.starter.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.activity.EditarActivity;
import com.parse.starter.activity.MainActivity;
import com.parse.starter.activity.PerfilAnimalActivity;
import com.parse.starter.fragments.HomeFragment;
import com.parse.starter.util.ImageViewTarget;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by Matheus on 17/08/2016.
 */

public class HomeAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private ArrayList<ParseObject> postagens;
    private ParseObject parseObject;
    //private ParseQuery<ParseObject> query;
    private boolean curtiu = false;
    private String usuario_like = "";
    private ParseUser parseUser;



    public HomeAdapter(Context c, ArrayList<ParseObject> objects) {
        super(c, 0, objects);
        this.context = c;
        this.postagens = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View view = convertView;


        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lista_postagem, parent, false);
        }

        if (postagens.size() > 0)
            try {

                final ImageView imagemPostagem = (ImageView) view.findViewById(R.id.image_lista_postagem);



                //Cria um objeto parse para cada postagem
            parseObject = postagens.get(position);

            //Importa a imagem do animal

            Picasso.with(context)
                    .load(parseObject.getParseFile("imagem").getUrl())
                    .fit()
                    .centerInside()
                    .into(imagemPostagem);

            final ImageButton botao_like = (ImageButton) view.findViewById(R.id.botao_like);
            botao_like.setTag(position);

            final ImageButton botao_favoritar = (ImageButton) view.findViewById(R.id.botao_favoritar);
            botao_favoritar.setTag(position);

            parseUser = ParseUser.getCurrentUser();

            //Importa o texto com o nome do animal
            TextView nome = (TextView) view.findViewById(R.id.text_usuario);
            nome.setText((CharSequence) parseObject.get("nome_animal").toString().toUpperCase());

            TextView detalhes = (TextView) view.findViewById(R.id.text_detalhes);
            detalhes.setText((CharSequence) parseObject.get("lista_raca").toString() +
                    ", ");

            if (!parseObject.get("lista_ano").equals("00")) {
                detalhes.append((parseObject.get("lista_ano").equals("01")) ? parseObject.get("lista_ano").toString() + " ano"
                        : parseObject.get("lista_ano").toString() + " anos");
            }

            if ((!parseObject.get("lista_ano").equals("00")) && (!parseObject.get("lista_mes").equals("00")))
                detalhes.append(" e ");

            if (!parseObject.get("lista_mes").equals("00")) {
                detalhes.append(parseObject.get("lista_mes").toString());
                detalhes.append((parseObject.get("lista_mes").equals("01")) ? " mês" : " meses");
            }




            nome.setText((CharSequence) parseObject.get("nome_animal").toString().toUpperCase());

            TextView txt_likes = (TextView) view.findViewById(R.id.txt_likes);
            txt_likes.setText(parseObject.get("Likes") + " pessoas curtiram");

            botao_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    SQLiteDatabase db = getContext().openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

                    StringBuilder sqlClientes = new StringBuilder();
                    sqlClientes.append("CREATE TABLE IF NOT EXISTS like_pressed(");
                    sqlClientes.append("_id INTEGER PRIMARY KEY, like INTEGER);");
                    db.execSQL(sqlClientes.toString());

                    Cursor cursor = db.rawQuery("SELECT * FROM like_pressed", null);

                    if (cursor.getCount() > 0) {

                        final int posicao = (Integer) botao_like.getTag();
                        parseObject = postagens.get(posicao);

                        ParseQuery<ParseObject> query;
                        query = ParseQuery.getQuery("Animal");
                        query.getInBackground(parseObject.getObjectId(), new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    // object will be your game score
                                    usuario_like = object.getString("usuario_like");
                                    if (usuario_like == null) {
                                        usuario_like = "";
                                    }
                                    curtiu = usuario_like.contains(parseUser.getObjectId().toString());
                                    if (!curtiu) {
                                        parseObject.put("Likes", parseObject.getInt("Likes") + 1);
                                        parseObject.put("usuario_like", parseObject.get("usuario_like") + parseUser.getObjectId() + ";");
                                        parseObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Você já curtiu " + parseObject.get("nome_animal"), Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    // something went wrong
                                    Toast.makeText(context, "Erro inesperado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        ContentValues ctv = new ContentValues();
                        ctv.put("like", 0);

                        db.insert("like_pressed","id",ctv);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getContext());
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Ao curtir um pet, você estará contribuindo para que ele apareça para mais pessoas!")
                                .setCancelable(true)
                                .setNeutralButton("OK",null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                }
            });


            botao_favoritar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SQLiteDatabase db = getContext().openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

                    StringBuilder sqlClientes = new StringBuilder();
                    sqlClientes.append("CREATE TABLE IF NOT EXISTS favorit_pressed(");
                    sqlClientes.append("_id INTEGER PRIMARY KEY, fav INTEGER);");
                    db.execSQL(sqlClientes.toString());

                    Cursor cursor = db.rawQuery("SELECT * FROM favorit_pressed", null);

                    if (cursor.getCount() > 0) {
                        final int posicao = (Integer) botao_favoritar.getTag();
                        parseObject = postagens.get(posicao);

                        ParseQuery<ParseObject> query;
                        query = ParseQuery.getQuery("Favoritos");
                        query.whereEqualTo("user_id", parseUser.getObjectId());
                        query.whereEqualTo("animal_id", parseObject.getObjectId());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    // object will be your game score
                                    if (objects.isEmpty()) {
                                        ParseObject parseObject_favoritos = new ParseObject("Favoritos");
                                        parseObject_favoritos.put("data_favorito", new Date());
                                        parseObject_favoritos.put("user_id", parseUser.getObjectId());
                                        parseObject_favoritos.put("animal_id", parseObject.getObjectId());
                                        parseObject_favoritos.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    notifyDataSetChanged();
                                                    Toast.makeText(context, parseObject.getString("nome_animal") + " adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    // something went wrong
                                    Toast.makeText(context, "Erro inesperado", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        ContentValues ctv = new ContentValues();
                        ctv.put("fav", 0);

                        db.insert("favorit_pressed","id",ctv);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                getContext());
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Ao favoritar um pet, ele fica salvo no menu \"Favoritos\" para acessa-lo sempre quando quiser!")
                                .setCancelable(true)
                                .setNeutralButton("OK",null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            });


        } catch (Exception e) {
            Toast.makeText(context, "Não foi possível carregar as imagens", Toast.LENGTH_SHORT).show();
        }


        return view;

    }

}
