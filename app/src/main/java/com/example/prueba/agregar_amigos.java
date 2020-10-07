package com.example.prueba;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar_amigos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);

        FloatingActionButton btnMostrarAmigos = findViewById(R.id.btnMostrarAmigos);
        btnMostrarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAmigos();
            }
        });
        FloatingActionButton btnGuardarAmigo = findViewById(R.id.btnGuardarAmigos);
        btnGuardarAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAmigo();
            }
        });
    }
    private void mostrarAmigos(){
        Intent mostrarAmigos = new Intent(agregar_amigos.this, MainActivity.class);
        startActivity(mostrarAmigos);
    }
    private void guardarAmigo(){
        TextView tempVal = findViewById(R.id.txtCodigoAmigo);
        String codigo = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtNombreAmigo);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDireccionAmigo);
        String direccion = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtTelefonoAmigo);
        String telefono = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDuiAmigo);
        String dui = tempVal.getText().toString();

        try {
            JSONObject datosAmigo = new JSONObject();
            datosAmigo.put("codigo", codigo);
            datosAmigo.put("nombre", nombre);
            datosAmigo.put("direccion", direccion);
            datosAmigo.put("telefono", telefono);
            datosAmigo.put("dui", dui);

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private class enviarDatosAmigo extends AsyncTask<String,String, String>{
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL("http://192.168.1.15:5984/db_agenda/");
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonDatos);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
            }catch (Exception ex){
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}