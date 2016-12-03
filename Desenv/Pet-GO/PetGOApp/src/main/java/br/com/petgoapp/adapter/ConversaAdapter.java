package br.com.petgoapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.petgoapp.R;

import java.util.ArrayList;

import br.com.petgoapp.model.Conversa;

/**
 * Created by Matheus on 27/10/2016.
 */

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;
    private Conversa conversa;

    public ConversaAdapter(Context c,  ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversas = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        if(conversas != null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.lista_conversas,parent,false);

            TextView nome = (TextView) view.findViewById(R.id.text_nome_usuario_conversa);
            TextView ult_msg = (TextView) view.findViewById(R.id.text_ultima_mensagem_conversa);

            conversa = conversas.get(position);
            nome.setText(conversa.getNome_usuario());
            ult_msg.setText(conversa.getMensagem());
        }

        return view;
    }
}
