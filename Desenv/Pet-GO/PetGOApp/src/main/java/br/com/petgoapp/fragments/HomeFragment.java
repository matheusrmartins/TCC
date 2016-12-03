package br.com.petgoapp.fragments;


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
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import br.com.petgoapp.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.petgoapp.activity.PerfilAnimalActivity;
import br.com.petgoapp.adapter.HomeAdapter;
import br.com.petgoapp.util.Erros;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private ListView listView;
    private ArrayList<ParseObject> postagens;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query;
    private boolean carregamento;
    private ParseObject parseObject;
    public ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private Location mLastLocation;
    private String cidade_usuario;
    private String estado_usuario;
    private Cursor cursor;
    private TextView textView_erro;
    private boolean acesso_gps = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        postagens = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.list_postagens_home);
        adapter = new HomeAdapter(getActivity(), postagens);
        listView.setAdapter(adapter);

        textView_erro = (TextView) view.findViewById(R.id.textView_erro);

        carregamento = false;

        progressDialog =  new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando...");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Tem certeza que deseja fechar?")
                        .setCancelable(true)
                        .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("Não",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (!carregamento)
                                    progressDialog.show();
                            }
                        });
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (!carregamento)
                            progressDialog.show();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        progressDialog.show();




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = listView.getPositionForView(view);
                parseObject = postagens.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("object_id_usuario", parseObject.getString("object_id_usuario"));
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
                bundle.putString("telefone", parseObject.getString("telefone"));
                bundle.putString("imagem", parseObject.getParseFile("imagem").getUrl());
                bundle.putString("vacinas", (parseObject.getString("vacinas").trim().equals("")) ?
                        "" : parseObject.getString("vacinas"));
                bundle.putString("objectId", parseObject.getObjectId());
                bundle.putString("usuario_nome", parseObject.getString("usuario_nome"));
                bundle.putString("usuario_email", parseObject.getString("usuario_email"));
                String data_postagem = new SimpleDateFormat("dd/MM/yyyy").format(parseObject.getCreatedAt());
                bundle.putString("data_postagem", data_postagem);

                Intent intent = new Intent(getActivity(), PerfilAnimalActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);

        } else {

            getPostagens(1);

        }

        return view;
    }



    private void getPostagens(int permissao) {

        query = ParseQuery.getQuery("Animal");
        try {

            if (permissao == 1)
                getLocation();


            SQLiteDatabase db = getContext().openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

            String user_object_id = ParseUser.getCurrentUser().getObjectId();

            cursor = db.rawQuery("SELECT * FROM filtro_usuario where object_id = \"" + user_object_id+"\"", null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();

                // filtro de genero de animal
                if (cursor.getInt(1) == 1)
                    query.whereEqualTo("lista_genero", "Fêmea");
                else if (cursor.getInt(1) == 2)
                    query.whereEqualTo("lista_genero", "Macho");

                // filtro de tipo de animal
                if (cursor.getInt(2) == 1)
                    query.whereEqualTo("lista_tipo", "Cachorro");
                else if (cursor.getInt(2) == 2)
                    query.whereEqualTo("lista_tipo", "Gato");

                //filtro de raça - enviar Todas as raças como padrão na criação de usuário.
                if (!cursor.getString(3).equals("Todas as raças"))
                    query.whereEqualTo("lista_raca", cursor.getString(3));

            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao aplicar filtros", Toast.LENGTH_LONG).show();
        }
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {

                        postagens.clear();
                        acesso_gps = true;
                        for (ParseObject parseObject : objects) {
                            if (Integer.parseInt(parseObject.get("lista_ano").toString()) >= cursor.getInt(6) &&
                                Integer.parseInt(parseObject.get("lista_ano").toString()) <= cursor.getInt(7)) {
                                try {
                                    if (cursor.getInt(4) == 1) {
                                        Location loc2 = new Location(LocationManager.GPS_PROVIDER);
                                        loc2.setLatitude(parseObject.getDouble("Latitude"));
                                        loc2.setLongitude(parseObject.getDouble("Longitude"));

                                        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
                                        loc1.setLatitude(mLastLocation.getLatitude());
                                        loc1.setLongitude(mLastLocation.getLongitude());

                                        double radius = cursor.getDouble(5) * 1000; // Raio em metros.
                                        double distance = loc1.distanceTo(loc2); // recebe distância entre os dois pontos.
                                        if (distance < radius)  // verifica se tá dentro do raio estipulado.
                                            postagens.add(parseObject);
                                    } else {
                                        postagens.add(parseObject);
                                    }
                                } catch (Exception f) {
                                    postagens.add(parseObject);
                                    acesso_gps = false;
                                }
                            }

                        }
                        adapter.notifyDataSetChanged();
                        carregamento = true;
                        progressDialog.dismiss();

                        if (!acesso_gps){
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    getContext());
                            alertDialogBuilder.setTitle("Alerta")
                                    .setMessage("Erro ao buscar animais próximos. Verifique se a sua localização está ativa.")
                                    .setCancelable(true)
                                    .setPositiveButton("Verificar",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }


                    }else{
                        progressDialog.dismiss();
                    }

                    if (listView.getCount() > 0)
                        textView_erro.setText("");
                    else
                        textView_erro.setText("Não foi encontrado nenhum animal. Verifique as configurações de interesse.");
                } else {
                    progressDialog.dismiss();
                    Erros erros = new Erros();
                    Toast.makeText(getContext(), erros.retornaMensagem("PAR-"+e.getCode()), Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    public void atualizaPostagens(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    102);

        } else {

            getPostagens(1);

        }
    }

    private void getLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<android.location.Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addresses.size() > 0) {

                    if (addresses.get(0).getCountryCode().equals("BR")) {

                        cidade_usuario = addresses.get(0).getLocality().toUpperCase();
                        estado_usuario = addresses.get(0).getAdminArea().replace("State of ", "").toUpperCase();


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
                        else
                            estado_usuario = "SP";


                        ParseUser.getCurrentUser().put("lista_cidade", cidade_usuario);
                        ParseUser.getCurrentUser().put("lista_estado", estado_usuario);
                        ParseUser.getCurrentUser().saveEventually();
                    }
                }
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Erro ao buscar localização.", Toast.LENGTH_LONG).show();
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
                    getPostagens(1);
                } else {
                    getPostagens(0);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
