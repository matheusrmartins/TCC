package com.parse.starter.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openDatabase;

public class FiltroActivity extends AppCompatActivity {

    private Button botao_salvar;
    private RadioButton radio_macho;
    private RadioButton radio_femea;
    private RadioButton radio_cachorro;
    private RadioButton radio_gato;
    private int filtro_genero = 0;
    private int filtro_tipo = 0;
    private Spinner spinner_raca;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private String tipo;
    private String raca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        botao_salvar = (Button) findViewById(R.id.botao_salvar);
        radio_macho  = (RadioButton) findViewById(R.id.radioButton_macho);
        radio_femea  = (RadioButton) findViewById(R.id.radioButton_femea);
        radio_cachorro = (RadioButton) findViewById(R.id.radioButton_cachorro);
        radio_gato  = (RadioButton) findViewById(R.id.radioButton_gato);
        spinner_raca = (Spinner) findViewById(R.id.spinner_lista_raca_filtro);

        spinner_raca.setEnabled(false);

        SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

        String user_object_id = ParseUser.getCurrentUser().getObjectId();

        Cursor cursor = db.rawQuery("SELECT * FROM filtro_usuario where object_id = \"" + user_object_id+"\"", null);

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            // filtro de genero de animal
            if (cursor.getInt(1) == 1)
                radio_femea.setChecked(true);

            else if (cursor.getInt(1) == 2)
                radio_macho.setChecked(true);

            // filtro de tipo de animal
            if (cursor.getInt(2) == 1){
                radio_cachorro.setChecked(true);
                tipo = "Cachorro";
            }
            else if (cursor.getInt(2) == 2) {
                radio_gato.setChecked(true);
                tipo = "Gato";
            }

            alteraSpinnerRaca(cursor.getString(3));


        radio_cachorro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (radio_cachorro.isChecked())
                    tipo = "Cachorro";
                else if (radio_gato.isChecked())
                    tipo = "Gato";
                else
                    tipo =  "Ambos";
                alteraSpinnerRaca("Todos");
            }
        });

        radio_gato.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (radio_cachorro.isChecked())
                    tipo = "Cachorro";
                else if (radio_gato.isChecked())
                    tipo = "Gato";
                else
                    tipo =  "Ambos";
                alteraSpinnerRaca("Todos");
            }
        });

        botao_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radio_femea.isChecked())
                    filtro_genero = 1;
                else if (radio_macho.isChecked())
                    filtro_genero = 2;
                else
                    filtro_genero = 0;

                if (radio_cachorro.isChecked())
                    filtro_tipo = 1;
                else if (radio_gato.isChecked())
                    filtro_tipo = 2;
                else
                    filtro_tipo = 0;

                try {

                SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

                String user_object_id = ParseUser.getCurrentUser().getObjectId();

                Cursor cursor = db.rawQuery("SELECT * FROM filtro_usuario where object_id = \"" + user_object_id+"\"", null);


                    if (cursor.getCount() > 0) {
                        db.execSQL("update filtro_usuario set genero = " + filtro_genero + "," +
                                " tipo = " + filtro_tipo + ", raca = \"" + spinner_raca.getSelectedItem().toString() + "\" " +
                                " where object_id =  \""+ user_object_id + "\";");
                        Toast.makeText(FiltroActivity.this, "Salvo com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(FiltroActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e) {
                    Toast.makeText(FiltroActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
      }
    }

    private void alteraSpinnerRaca(String flag){
        if (tipo == "Cachorro") {
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_cachorro_filtro));
            spinner_raca.setEnabled(true);
        }
        else if (tipo == "Gato"){
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_gato_filtro));
            spinner_raca.setEnabled(true);
        }
        else {
            spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.lista_raca_ambos_filtro));
            spinner_raca.setEnabled(false);
        }

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_raca.setAdapter(spinnerArrayAdapter);

        spinner_raca.setSelection(spinnerArrayAdapter.getPosition(flag));
    }
}
