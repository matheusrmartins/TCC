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


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private Toolbar toolbarPrincipal;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    private ListView listView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private Location mLastLocation;
    private String cidade_usuario;
    private String estado_usuario;

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


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);

        } else {

            getLocation();

        }

        SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

        StringBuilder sqlClientes = new StringBuilder();
        sqlClientes.append("CREATE TABLE IF NOT EXISTS usuario(");
        sqlClientes.append("_id INTEGER PRIMARY KEY, email VARCHAR(50));");
        db.execSQL("CREATE TABLE IF NOT EXISTS usuario(_id INTEGER PRIMARY KEY, object_id VARCHAR(50));");

        db.execSQL("CREATE TABLE IF NOT EXISTS filtro_usuario(object_id VARCHAR(20) PRIMARY KEY, genero INTEGER, tipo INTEGER, " +
                "raca VARCHAR(30));");

        try {
            String user_object_id = ParseUser.getCurrentUser().getObjectId();

            Cursor cursor = db.rawQuery("SELECT * FROM filtro_usuario where object_id = \"" + user_object_id+"\"", null);

            if (cursor.getCount() < 1){
                db.execSQL("insert into filtro_usuario (object_id, genero, tipo, raca) values (\""+user_object_id+"\", 0, 0, \"Todos\");");
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

    private void getLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(Bundle arg0) {

        fusedLocationProviderApi.requestLocationUpdates( mGoogleApiClient,  locationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addresses.size() > 0) {
                    cidade_usuario = addresses.get(0).getLocality().toUpperCase();
                    estado_usuario = addresses.get(0).getAdminArea().replace("State of ","").toUpperCase();

                    if (estado_usuario.equals("ACRE"))
                        estado_usuario = "AC";
                    else if (estado_usuario.equals("ALAGOAS"))
                        estado_usuario = "AL";
                    else if (estado_usuario.equals("AMAPÁ"))
                        estado_usuario = "AP";
                    else if (estado_usuario.equals("AMAZONAS"))
                        estado_usuario = "AM";
                    else if (estado_usuario.equals("BAHIA"))
                        estado_usuario = "BA";
                    else if (estado_usuario.equals("CEARÁ"))
                        estado_usuario = "CE";
                    else if (estado_usuario.equals("DISTRITO FEDERAL"))
                        estado_usuario = "DF";
                    else if (estado_usuario.equals("ESPÍRITO SANTO"))
                        estado_usuario = "ES";
                    else if (estado_usuario.equals("GOIÁS"))
                        estado_usuario = "GO";
                    else if (estado_usuario.equals("MARANHÃO"))
                        estado_usuario = "MA";
                    else if (estado_usuario.equals("MATO GROSSO"))
                        estado_usuario = "MT";
                    else if (estado_usuario.equals("MATO GROSSO DO SUL"))
                        estado_usuario = "MS";
                    else if (estado_usuario.equals("MINAS GERAIS"))
                        estado_usuario = "MG";
                    else if (estado_usuario.equals("PARÁ"))
                        estado_usuario = "PA";
                    else if (estado_usuario.equals("PARAÍBA"))
                        estado_usuario = "PB";
                    else if (estado_usuario.equals("PARANÁ"))
                        estado_usuario = "PR";
                    else if (estado_usuario.equals("PERNAMBUCO"))
                        estado_usuario = "PE";
                    else if (estado_usuario.equals("PIAUÍ"))
                        estado_usuario = "PI";
                    else if (estado_usuario.equals("RIO DE JANEIRO"))
                        estado_usuario = "RJ";
                    else if (estado_usuario.equals("RIO GRANDE DO NORTE"))
                        estado_usuario = "RN";
                    else if (estado_usuario.equals("RIO GRANDE DO SUL"))
                        estado_usuario = "RS";
                    else if (estado_usuario.equals("RONDÔNIA"))
                        estado_usuario = "RO";
                    else if (estado_usuario.equals("RORAIMA"))
                        estado_usuario = "RR";
                    else if (estado_usuario.equals("SANTA CATARINA"))
                        estado_usuario = "SC";
                    else if (estado_usuario.equals("SÃO PAULO"))
                        estado_usuario = "SP";
                    else if (estado_usuario.equals("SERGIPE"))
                        estado_usuario = "SE";
                    else if (estado_usuario.equals("TOCANTINS"))
                        estado_usuario = "TO";


                    ParseUser.getCurrentUser().put("lista_cidade", cidade_usuario);
                    ParseUser.getCurrentUser().put("lista_estado", estado_usuario);
                    ParseUser.getCurrentUser().saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                //Toast.makeText(MainActivity.this, "Localização atualizada para", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Erro ao buscar localização.", Toast.LENGTH_LONG).show();
            }

        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 102:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    getLocation();
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
