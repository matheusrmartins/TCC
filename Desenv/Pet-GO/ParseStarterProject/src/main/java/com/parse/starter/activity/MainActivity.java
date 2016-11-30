/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.adapter.TabsAdapter;
import com.parse.starter.fragments.HomeFragment;
import com.parse.starter.fragments.NotificacoesFragment;
import com.parse.starter.util.SlidingTabLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  {

    private Toolbar toolbarPrincipal;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarPrincipal = (Toolbar) findViewById(R.id.toolbar_principal);
        toolbarPrincipal.setLogo(R.drawable.logo_escrita);
        toolbarPrincipal.setTitle("");
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager_main);

        setSupportActionBar(toolbarPrincipal);

        SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

        StringBuilder sqlClientes = new StringBuilder();
        sqlClientes.append("CREATE TABLE IF NOT EXISTS usuario(");
        sqlClientes.append("_id INTEGER PRIMARY KEY, email VARCHAR(50));");
        db.execSQL("CREATE TABLE IF NOT EXISTS usuario(_id INTEGER PRIMARY KEY, object_id VARCHAR(50));");

        db.execSQL("CREATE TABLE IF NOT EXISTS filtro_usuario(object_id VARCHAR(20) PRIMARY KEY, genero INTEGER, tipo INTEGER, " +
                "raca VARCHAR(30), localizacao INTEGER, raio DOUBLE);");

        try {
            String user_object_id = ParseUser.getCurrentUser().getObjectId();

            Cursor cursor = db.rawQuery("SELECT * FROM filtro_usuario where object_id = \"" + user_object_id+"\"", null);

            if (cursor.getCount() < 1){
                db.execSQL("insert into filtro_usuario (object_id, genero, tipo, raca, localizacao, raio) values (\""+user_object_id+"\", 0, 0, \"Todos\", 1, 150.0);");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.list_postagens_home);

        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(tabsAdapter);
        slidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.text_item_tab);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.CorPrincipal));
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_sair:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Tem certeza que deseja sair?")
                        .setCancelable(true)
                        .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                progressDialog =  new ProgressDialog(MainActivity.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Saindo...");
                                progressDialog.show();
                                deslogarUsuario();
                            }
                        })
                        .setNegativeButton("Não",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            case R.id.action_configuracoes:
                Intent intent_filtro = new Intent(MainActivity.this, FiltroActivity.class);
                startActivity(intent_filtro);
                return true;
            case R.id.action_compartilhar:
                Intent intent_cadastro_pet = new  Intent(MainActivity.this, CadastroPetActivity.class);
                startActivity(intent_cadastro_pet);
                return true;
            case R.id.action_favoritos:
                Intent intent_favoritos = new  Intent(MainActivity.this, FavoritosActivity.class);
                startActivity(intent_favoritos);
                return true;
            case R.id.action_atualizar:
                try {
                    TabsAdapter adapterNovo = (TabsAdapter) viewPager.getAdapter();
                    HomeFragment homeFragment = (HomeFragment) adapterNovo.getFragment(0);
                    homeFragment.atualizaPostagens();
                    NotificacoesFragment notificacoesFragment = (NotificacoesFragment) adapterNovo.getFragment(1);
                    notificacoesFragment.atualizaConversas();
                }
                catch(Exception e){
                    Log.i("ERRO ", e.getMessage());
                }
                return true;
            case R.id.action_editar:
                Intent intent_editar = new  Intent(MainActivity.this, EditarActivity.class);
                startActivity(intent_editar);
                return true;
            default:
                return true;
        }

    }


    private void deslogarUsuario(){
        ParseUser.logOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
