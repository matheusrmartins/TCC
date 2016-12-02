package com.parse.starter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.starter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Matheus on 17/08/2016.
 */

public class FavoritosAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private ArrayList<ParseObject> postagens;

    public FavoritosAdapter(Context c, ArrayList<ParseObject> objects) {
        super(c, 0, objects);
        this.context = c;
        this.postagens = objects;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lista_postagem_favoritos, parent, false);
        }

        if (postagens.size() > 0){

            try {
                //Cria um objeto parse para cada postagem
                ParseObject parseObject = postagens.get(position);

                //Importa o texto com o nome do animal
                TextView objectid = (TextView) view.findViewById(R.id.text_animal);
                objectid.setText((CharSequence) parseObject.get("nome_animal").toString().toUpperCase());

                //Importa a imagem do animal
                ImageView imagemPostagem = (ImageView) view.findViewById(R.id.image_lista_favoritos);
                Picasso.with(context)
                        .load(parseObject.getParseFile("imagem").getUrl())
                        .placeholder(R.drawable.progress_animation)
                        .fit()
                        .centerCrop()
                        .into(imagemPostagem);

            }catch (Exception e){
                Toast.makeText(context, "Não foi possível carregar as imagens", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }


}
