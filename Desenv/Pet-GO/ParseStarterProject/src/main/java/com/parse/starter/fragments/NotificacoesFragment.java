package com.parse.starter.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.activity.ChatActivity;
import com.parse.starter.activity.PerfilAnimalActivity;
import com.parse.starter.adapter.ConversaAdapter;
import com.parse.starter.model.Conversa;
import com.parse.starter.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificacoesFragment extends Fragment {

    private ListView listView_conversas;
    private ArrayAdapter<Conversa> arrayAdapter;
    private ArrayList<Conversa> conversas;
    private ParseQuery<ParseObject> query_conversas;
    private ParseQuery<ParseObject> query_conversas1;
    private ParseQuery<ParseObject> query_conversas2;
    private String usuario;

    public NotificacoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notificacoes, container, false);


        conversas = new ArrayList<>();
        listView_conversas = (ListView) view.findViewById(R.id.listView_conversas);
        arrayAdapter = new ConversaAdapter(getActivity(), conversas);
        
        listView_conversas.setAdapter(arrayAdapter);

        usuario = ParseUser.getCurrentUser().getObjectId();

        listView_conversas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("nome_usuario", conversas.get(position).getNome_usuario());
                bundle.putString("object_id_usuario", conversas.get(position).getObject_id_usuario());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        getConversas();

        return view;
    }

    public void getConversas(){
        query_conversas1 = ParseQuery.getQuery("Conversa");
        query_conversas1.whereEqualTo("usuario_1", usuario);
        query_conversas2 = ParseQuery.getQuery("Conversa");
        query_conversas2.whereEqualTo("usuario_2", usuario);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(query_conversas1);
        queries.add(query_conversas2);

        query_conversas = ParseQuery.or(queries);

        conversas.clear();

        query_conversas.orderByDescending("updatedAt");

        query_conversas.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object : objects){
                        Conversa conversa = new Conversa();
                        conversa.setObject_id_usuario(object.getString("usuario_1").equals(usuario) ? object.getString("usuario_2") : object.getString("usuario_1"));
                        conversa.setNome_usuario(object.getString("usuario_1").equals(usuario) ? object.getString("nome_2") : object.getString("nome_1"));
                        conversa.setMensagem(object.getString("ultima_mensagem"));

                        conversas.add(conversa);
                    }
                    listView_conversas.invalidateViews();
                }else{

                }
            }
        });
    }

    public void atualizaConversas(){
        getConversas();
    }

}
