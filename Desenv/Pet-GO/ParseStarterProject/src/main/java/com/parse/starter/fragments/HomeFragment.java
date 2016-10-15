package com.parse.starter.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.print.PrintHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;
import com.parse.starter.activity.EditarActivity;
import com.parse.starter.activity.EditarPetActivity;
import com.parse.starter.activity.FavoritosActivity;
import com.parse.starter.activity.MainActivity;
import com.parse.starter.activity.PerfilAnimalActivity;
import com.parse.starter.adapter.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ListView listView;
    private ArrayList<ParseObject> postagens;
    private ArrayAdapter<ParseObject> adapter;
    private ParseQuery<ParseObject> query;
    private boolean carregamento;
    private ParseObject parseObject;
    public ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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


        postagens = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.list_postagens_home);
        adapter = new HomeAdapter(getActivity(), postagens);
        listView.setAdapter(adapter);




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
                bundle.putString("imagem", parseObject.getParseFile("imagem").getUrl());
                bundle.putString("vacinas", (parseObject.getString("vacinas").trim().equals("")) ?
                                    "" : parseObject.getString("vacinas"));
                bundle.putString("objectId", parseObject.getObjectId());

                Intent intent = new Intent(getActivity(), PerfilAnimalActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });



        getPostagens();

        return view;
    }



    private void getPostagens(){
        query = ParseQuery.getQuery("Animal");
        query.orderByDescending("createdAt");

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
                        carregamento = true;
                        progressDialog.dismiss();

                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage() + e.getCode(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void atualizaPostagens(){
        getPostagens();
    }

}
