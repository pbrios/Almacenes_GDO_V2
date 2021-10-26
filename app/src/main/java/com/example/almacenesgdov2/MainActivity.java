package com.example.almacenesgdov2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.almacenesgdov2.objetos.Almacenes;
import com.example.almacenesgdov2.objetos.Persona;
import com.example.almacenesgdov2.recursos.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText edtUser, edtPassword;
    private Button btnLogin;
    private String servidor;
    private Gson gson = new Gson();
    private Persona[] persona;
    private Persona datosPersona;
    private Almacenes[] responseAlmacenes;
    private ArrayList<Almacenes> almacenesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializa();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setEnabled(false);
                validaUsuario(servidor+"loginApps.php");
            }
        });
    }

    private void inicializa(){
        edtUser = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        servidor = getString(R.string.servername);
        almacenesArrayList = new ArrayList<Almacenes>();
        listaAlmacenes();
    }

    private void validaUsuario(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    persona = gson.fromJson(response, Persona[].class);
                    datosPersona = persona[0];
                    Intent intent = new Intent(getApplicationContext(), Menu.class);
                    intent.putExtra("usuario",datosPersona);
                    intent.putExtra("almacenes", almacenesArrayList);
                    startActivity(intent);
                    btnLogin.setEnabled(true);
                }else{
                    Toast.makeText(getApplicationContext(), "Error en Usuario o Contraseña", Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                btnLogin.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", edtUser.getText().toString()+"@gdo.com");
                parametros.put("password", edtPassword.getText().toString());
                return parametros;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void listaAlmacenes(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("http://distribuciongdo.site/conexiones/listaAlmacenes.php?almacenU="+"2", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                responseAlmacenes = null;
                responseAlmacenes = gson.fromJson(response.toString(), Almacenes[].class);
                almacenesArrayList.addAll(Arrays.asList(responseAlmacenes));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error \n" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
}