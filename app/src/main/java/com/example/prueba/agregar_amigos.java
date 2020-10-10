package com.example.prueba;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar_amigos extends AppCompatActivity {
    String resp, accion, id, rev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);

       try {
           FloatingActionButton btnMostrarAmigos = findViewById(R.id.btnMostrarAmigos);
           btnMostrarAmigos.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mostrarAmigos();
               }
           });
           Button btnGuardarAmigo = findViewById(R.id.btnGuardarAmigos);
           btnGuardarAmigo.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   guardarAmigo();
               }
           });
           mostrarDatosAmigo();
       }catch (Exception ex){
           Toast.makeText(getApplicationContext(), "Error al agregar amigos: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
       }
    }
    void mostrarDatosAmigo(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                JSONObject dataAmigo = new JSONObject(recibirParametros.getString("dataAmigo")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtCodigoAmigo);
                tempVal.setText(dataAmigo.getString("codigo"));

                tempVal = (TextView)findViewById(R.id.txtNombreAmigo);
                tempVal.setText(dataAmigo.getString("nombre"));

                tempVal = (TextView)findViewById(R.id.txtDireccionAmigo);
                tempVal.setText(dataAmigo.getString("direccion"));

                tempVal = (TextView)findViewById(R.id.txtTelefonoAmigo);
                tempVal.setText(dataAmigo.getString("telefono"));

                tempVal = (TextView)findViewById(R.id.txtDuiAmigo);
                tempVal.setText(dataAmigo.getString("dui"));

                id = dataAmigo.getString("_id");
                rev = dataAmigo.getString("_rev");
            }
        }catch (Exception ex){
            ///
        }
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
            if (accion.equals("modificar")){
                datosAmigo.put("_id",id);
                datosAmigo.put("_rev",rev);
            }
            datosAmigo.put("codigo", codigo);
            datosAmigo.put("nombre", nombre);
            datosAmigo.put("direccion", direccion);
            datosAmigo.put("telefono", telefono);
            datosAmigo.put("dui", dui);

            enviarDatosAmigo objGuardarAmigo = new enviarDatosAmigo();
            objGuardarAmigo.execute(datosAmigo.toString());
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
                URL url = new URL("http://192.168.1.7:5984/db_agenda/");
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
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();

                String inputLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=reader.readLine())!= null){
                    stringBuffer.append(inputLine+"\n");
                }
                if(stringBuffer.length()==0){
                    return null;
                }
                jsonResponse = stringBuffer.toString();
                return jsonResponse;
            }catch (Exception ex){
                //
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("ok")){
                    Toast.makeText(getApplicationContext(), "Datos de amigo guardado con exito", Toast.LENGTH_SHORT).show();
                    mostrarAmigos();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al intentar guardar datos de amigo", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al guardar amigo: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}