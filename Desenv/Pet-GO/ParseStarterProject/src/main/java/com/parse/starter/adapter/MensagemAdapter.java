package com.parse.starter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.starter.R;
import com.parse.starter.model.Mensagem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matheus on 27/10/2016.
 */


public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(Context c,  ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = null;

        if (mensagens != null){

            Mensagem mensagem = mensagens.get(position);

            String usuario = ParseUser.getCurrentUser().getObjectId();

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            if (usuario.equals(mensagem.getIdUsuario())){
                view = layoutInflater.inflate(R.layout.item_mensagem_dir, parent, false);
            }else{
                view = layoutInflater.inflate(R.layout.item_mensagem_esq, parent, false);
            }

            TextView textViewMensagem  = (TextView) view.findViewById(R.id.tv_mensagem);

            textViewMensagem.setText(mensagem.getMensagem());

        }

        return view;

    }
}
