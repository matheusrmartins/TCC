/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.adapter.EditarAdapter;
import com.parse.starter.adapter.HomeAdapter;
import com.parse.starter.adapter.TabsAdapter;
import com.parse.starter.fragments.HomeFragment;
import com.parse.starter.util.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;


public class EditarActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private ViewPager viewPager;
    private ListView listView;
    private ArrayList<ParseObject> postagens;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query;
    private ParseObject parseObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Meus pets");
        toolbar.setTitleTextColor(R.color.Preto);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left);

        viewPager = (ViewPager) findViewById(R.id.view_pager_editar);

        setSupportActionBar(toolbar);

        postagens = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_postagens_editar);
        adapter = new EditarAdapter(EditarActivity.this, postagens);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(EditarActivity.this, "Teste", Toast.LENGTH_SHORT).show();
            }
        });

        getPostagens();
    }

    public void editarAnimal(View v){

        ListView listview = (ListView) findViewById(R.id.list_postagens_editar);

        final int position = listview.getPositionForView((View) v.getParent());

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

        adapter.notifyDataSetChanged();

        Intent intent = new Intent(EditarActivity.this, EditarPetActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void excluirAnimal(View v){

        ListView listview = (ListView) findViewById(R.id.list_postagens_editar);

        final int position = listview.getPositionForView((View) v.getParent());

        parseObject = postagens.get(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                EditarActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Tem certeza que deseja remover esse animal?")
                .setCancelable(false)
                .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        progressDialog =  new ProgressDialog(EditarActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Removendo "+parseObject.get("nome_animal").toString()+"...");
                        progressDialog.show();
                        try {
                            parseObject.delete();
                            postagens.remove(position);
                            adapter.notifyDataSetChanged();
                        }catch (ParseException e){
                            Toast.makeText(EditarActivity.this, e.getMessage()+e.getCode(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
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
        query = ParseQuery.getQuery("Animal");
        query.orderByDescending("createdAt");
        query.whereEqualTo("object_id_usuario", ParseUser.getCurrentUser().getObjectId());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){

                    if(objects.size() > 0){

                        postagens.clear();
                        for (ParseObject parseObject : objects){

                            postagens.add(parseObject);

                        }
                        adapter.notifyDataSetChanged();

                    }
                }else{

                }
            }
        });
    }

    public void onBackPressed() {
        finish();
    }

}
