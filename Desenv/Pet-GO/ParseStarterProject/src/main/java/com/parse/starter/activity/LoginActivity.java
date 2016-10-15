package com.parse.starter.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.parse.starter.R;

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText texto_usuario;
    private EditText texto_senha;
    private Button botao_entrar;
    private ProgressDialog progressDialog;
    private String email;
    private String nome;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        FacebookSdk.sdkInitialize(getApplicationContext());

        verificarUsuarioLogado();

        verificaLoginFacebook();

        texto_usuario   = (EditText) findViewById(R.id.editText_user);
        texto_senha     = (EditText) findViewById(R.id.editText_password);
        botao_entrar    = (Button)   findViewById(R.id.button_entrar);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("public_profile","email"));
        callbackManager = CallbackManager.Factory.create();

        texto_usuario.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

        StringBuilder sqlClientes = new StringBuilder();
        sqlClientes.append("CREATE TABLE IF NOT EXISTS usuario(");
        sqlClientes.append("_id INTEGER PRIMARY KEY, email VARCHAR(50));");
        db.execSQL(sqlClientes.toString());

        Cursor cursor = db.rawQuery("SELECT * FROM usuario", null);

        cursor.moveToFirst();

        try {
            String nomeString = cursor.getString(cursor.getColumnIndex("email"));

            StringBuilder conversor = new StringBuilder();
            conversor.append(nomeString);
            if (conversor.toString() != null)
                texto_usuario.setText(conversor.toString());
        }
        catch (Exception e){
            Log.i("Erro ", e.getMessage());
        }

        db.close();



       /* loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetailsFromFB();
                abrirAreaPrincipal();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,  "Operação cancelada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,  "Erro ao logar com o Facebook", Toast.LENGTH_SHORT).show();
            }

        });*/

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, Arrays.asList("public_profile","email"), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Fazendo o login...");
                            progressDialog.show();
                            getUserDetailsFromFB();
                            abrirAreaPrincipal();
                            progressDialog.dismiss();
                        } else {
                            progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Fazendo o login...");
                            progressDialog.show();
                            getUserDetailsFromParse();
                            abrirAreaPrincipal();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });


        botao_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Fazendo o login...");
                progressDialog.show();
                verificarLogin(texto_usuario.getText().toString().toLowerCase(), texto_senha.getText().toString());
            }
        });
    }


    private void verificarLogin(String usuario, String senha){
        ParseUser.logInInBackground(usuario, senha, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null){
                    SQLiteDatabase db = openOrCreateDatabase("usuariologin.db", Context.MODE_PRIVATE, null);

                    StringBuilder sqlClientes = new StringBuilder();
                    sqlClientes.append("DELETE FROM usuario;");
                    db.execSQL(sqlClientes.toString());

                    ContentValues ctv = new ContentValues();
                    ctv.put("email", texto_usuario.getText().toString());

                    db.insert("usuario","id",ctv);

                    abrirAreaPrincipal();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,  e.getMessage()+" Codigo: PAR-"+ e.getCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarUsuarioLogado(){
        if(ParseUser.getCurrentUser() != null){
            abrirAreaPrincipal();
        }
    }

    public void onBackPressed() {
        finish();
    }

    private void abrirAreaPrincipal(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void getUserDetailsFromFB() {
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
         /* handle the result */
                        try {
                            email = response.getJSONObject().getString("email");
                            nome = response.getJSONObject().getString("name");
                            ParseUser parseUser = ParseUser.getCurrentUser();
                            parseUser.setUsername(email);
                            parseUser.setEmail(email);
                            parseUser.put("nome", nome);
                            parseUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(LoginActivity.this, "New user:" + nome + " Signed up", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.i("Nome: ", nome);
                            Log.i("Email: ", email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void getUserDetailsFromParse() {
        ParseUser parseUser = ParseUser.getCurrentUser();

        Toast.makeText(LoginActivity.this, "Welcome back " + parseUser.getString("nome"), Toast.LENGTH_SHORT).show();
    }



    public void abrirCadastroUsuario(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void verificaLoginFacebook(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null){
            abrirAreaPrincipal();
        }
    }
}
