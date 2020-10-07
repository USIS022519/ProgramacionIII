package com.example.prueba;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.aware.PublishConfig;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    JSONArray datosJSON;
    JSONObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerDatosAmigos objObtenerAmigos = new obtenerDatosAmigos();
        objObtenerAmigos.execute();

        FloatingActionButton btnAgregarNuevoAmigos = findViewById(R.id.btnAgregarAmigos);
        btnAgregarNuevoAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarNuevosAmigos();
            }
        });
    }
    private class obtenerDatosAmigos extends AsyncTask<Void,Void, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("Http://192.168.1.15:5984/db_agenda/_design/agenda/_view/mi-agenda");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    result.append(linea);
                }
            } catch (Exception ex) {
                //
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                jsonObject = new JSONObject(s);
                datosJSON = jsonObject.getJSONArray("rows");
                mostrarDatosAmigos();
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "Error la parsear los datos: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void mostrarDatosAmigos(){
        ListView ltsAmigos = findViewById(R.id.ltsAgendaAmigosCouchDB);
        try {
            final ArrayList<String> arrayList = new ArrayList<>();
            final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
            ltsAmigos.setAdapter(stringArrayAdapter);

            for (int i = 0; i < datosJSON.length(); i++) {
                stringArrayAdapter.add(datosJSON.getJSONObject(i).getJSONObject("value").getString("nombre"));
            }
            stringArrayAdapter.notifyDataSetChanged();
            registerForContextMenu(ltsAmigos);
        }catch (Exception ex){
            Toast.makeText(MainActivity.this, "Error al mostrar los datos: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void agregarNuevosAmigos(){
        Intent nuevoAmigo = new Intent(MainActivity.this, agregar_amigos.class);
        startActivity(nuevoAmigo);
    }
}