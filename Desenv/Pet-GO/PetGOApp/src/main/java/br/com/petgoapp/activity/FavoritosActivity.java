/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package br.com.petgoapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import br.com.petgoapp.R;

import java.util.ArrayList;
import java.util.List;

import br.com.petgoapp.adapter.FavoritosAdapter;


public class FavoritosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private ViewPager viewPager;
    private ListView listView;
    private ArrayList<ParseObject> postagens;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query_aux;
    private ParseObject parseObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Favoritos");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        viewPager = (ViewPager) findViewById(R.id.view_pager_favoritos);

        setSupportActionBar(toolbar);

        progressDialog =  new ProgressDialog(FavoritosActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando favoritos...");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        progressDialog.show();

        postagens = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_postagens_favoritos);
        adapter = new FavoritosAdapter(FavoritosActivity.this, postagens);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = listView.getPositionForView(view);
                parseObject = postagens.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("nome_animal", parseObject.getString("nome_animal"));
                bundle.putString("lista_genero", parseObject.getString("lista_genero"));
                bundle.putString("lista_tipo", parseObject.getString("lista_tipo"));
                bundle.putString("lista_raca", parseObject.getString("lista_raca"));
                bundle.putString("lista_ano", parseObject.getString("lista_ano"));
                bundle.putString("lista_mes", parseObject.getString("lista_mes"));
                bundle.putString("castrado_checked", parseObject.getString("castrado"));
                bundle.putString("lista_estado", parseObject.getString("lista_estado"));
                bundle.putString("lista_cidade", parseObject.getString("lista_cidade"));
                bundle.putString("descricao", parseObject.getString("descricao"));
                bundle.putString("imagem", parseObject.getParseFile("imagem").getUrl());
                bundle.putString("vacinas", (parseObject.getString("vacinas").trim().equals("")) ?
                        "" : parseObject.getString("vacinas"));
                bundle.putString("objectId", parseObject.getObjectId());

                adapter.notifyDataSetChanged();

                Intent intent = new Intent(FavoritosActivity.this, PerfilAnimalActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        getPostagens();
    }


    public void removerFavoritos(View v){

        ListView listview = (ListView) findViewById(R.id.list_postagens_favoritos);

        final int position = listview.getPositionForView((View) v.getParent());

        parseObject = postagens.get(position);

        progressDialog =  new ProgressDialog(FavoritosActivity.this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                FavoritosActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Tem certeza que deseja remover esse animal dos favoritos?")
                .setCancelable(false)
                .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Removendo animal...");
                        progressDialog.show();
                            try {
                                ParseUser parseUser = new ParseUser();
                                parseUser = ParseUser.getCurrentUser();
                                ParseQuery<ParseObject> query;
                                query = ParseQuery.getQuery("Favoritos");
                                query.whereEqualTo("user_id", parseUser.getObjectId().toString());
                                query.whereEqualTo("animal_id", parseObject.getObjectId().toString());
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            // object will be your game score
                                            if (!objects.isEmpty()) {
                                                for (ParseObject object : objects) {

                                                    object.deleteInBackground(new DeleteCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                postagens.remove(position);
                                                                adapter.notifyDataSetChanged();
                                                                progressDialog.dismiss();
                                                                Toast.makeText(FavoritosActivity.this, "Animal removido com sucesso", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(FavoritosActivity.this, "Erro ao remover animal", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }
                                            }
                                        } else {
                                            // something went wrong
                                            Toast.makeText(FavoritosActivity.this, "Erro inesperado", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                Toast.makeText(FavoritosActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                    }
                })
                .setNegativeButton("Não",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }




    private void getPostagens(){
        ParseUser parseUser = ParseUser.getCurrentUser();
        query_aux = ParseQuery.getQuery("Favoritos");
        query_aux.whereEqualTo("user_id", parseUser.getObjectId());
        //query_aux.orderByAscending("data_favorito");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Animal");
        query.whereMatchesKeyInQuery("objectId","animal_id",query_aux);
        query.orderByAscending("nome_animal");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objectList, ParseException e) {
                if (e == null) {
                    if (objectList.size() > 0){
                        postagens.clear();
                        for (ParseObject parseObject : objectList) {
                            postagens.add(parseObject);
                        }
                        adapter.notifyDataSetChanged();
                    }else{
                        progressDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                FavoritosActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Você não marcou nenhum animal como favorito")
                                .setCancelable(true)
                                .setNeutralButton("OK",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        finish();
                                        Intent intent = new Intent(FavoritosActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }

                progressDialog.dismiss();
            }
        });
    }

    public void onBackPressed() {
        finish();
    }

}
