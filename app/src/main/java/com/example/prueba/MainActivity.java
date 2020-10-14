package com.example.prueba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
    JSONArray datosJSON;
    JSONObject jsonObject;
    Integer posicion;
    ArrayList<String> arrayList =new ArrayList<String>();
    ArrayList<String> copyStringArrayList = new ArrayList<String>();
    ArrayAdapter<String> stringArrayAdapter;
    utilidadesComunes uc;
    detectarInternet di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        di = new detectarInternet(getApplicationContext());
        if( di.hayConexionInternet() ) {
            conexionServidor objObtenerAmigos = new conexionServidor();
            objObtenerAmigos.execute(uc.url_consulta, "GET");
        } else {
            Toast.makeText(getApplicationContext(), "No hay conexion a internet.", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton btnAgregarNuevoAmigos = findViewById(R.id.btnAgregarAmigos);
        btnAgregarNuevoAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(di.hayConexionInternet()) {
                    agregarNuevosAmigos("nuevo", jsonObject);
                } else {
                    Toast.makeText(getApplicationContext(), "No hay conexion a internet.", Toast.LENGTH_LONG).show();
                }
            }
        });
        buscarAmigos();
    }
    void buscarAmigos(){
        final TextView tempVal = (TextView)findViewById(R.id.txtBuscarAmigo);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayList.clear();
                if( tempVal.getText().toString().trim().length()<1 ){//no hay texto para buscar
                    arrayList.addAll(copyStringArrayList);
                } else{//hacemos la busqueda
                    for (String amigo : copyStringArrayList){
                        if(amigo.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())){
                            arrayList.add(amigo);
                        }
                    }
                }
                stringArrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal, menu);
        try {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = adapterContextMenuInfo.position;
            menu.setHeaderTitle(datosJSON.getJSONObject(posicion).getString("nombre"));
        }catch (Exception ex){

        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnxAgregarAmigo:
                agregarNuevosAmigos("nuevo", jsonObject);
                return true;

            case R.id.mnxModificarAmigo:
                try {
                    agregarNuevosAmigos("modificar", datosJSON.getJSONObject(posicion));
                }catch (Exception ex){}
                return true;

            case R.id.mnxEliminarAmigo:
                AlertDialog eliminarFriend =  eliminarAmigo();
                eliminarFriend.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    private void mostrarDatosAmigos(){
        ListView ltsAmigos = findViewById(R.id.ltsAgendaAmigosCouchDB);
        try {
            arrayList.clear();
            stringArrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
            ltsAmigos.setAdapter(stringArrayAdapter);

            for (int i = 0; i < datosJSON.length(); i++) {
                stringArrayAdapter.add(datosJSON.getJSONObject(i).getJSONObject("value").getString("nombre"));
            }
            copyStringArrayList.clear();
            copyStringArrayList.addAll(arrayList);

            stringArrayAdapter.notifyDataSetChanged();
            registerForContextMenu(ltsAmigos);
        }catch (Exception ex){
            Toast.makeText(MainActivity.this, "Error al mostrar los datos: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void agregarNuevosAmigos(String accion, JSONObject jsonObject){
        try {
            Bundle enviarParametros = new Bundle();
            enviarParametros.putString("accion",accion);
            enviarParametros.putString("dataAmigo",jsonObject.toString());

            Intent agregarAmigo = new Intent(MainActivity.this, agregar_amigos.class);
            agregarAmigo.putExtras(enviarParametros);
            startActivity(agregarAmigo);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al llamar agregar amigos: "+ e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    AlertDialog eliminarAmigo(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
        try {
            confirmacion.setTitle(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("nombre"));
            confirmacion.setMessage("Esta seguro de eliminar el registro?");
            confirmacion.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        conexionServidor objEliminarAmigo = new conexionServidor();
                        objEliminarAmigo.execute(uc.url_mto +
                                datosJSON.getJSONObject(posicion).getJSONObject("value").getString("_id") + "?rev=" +
                                datosJSON.getJSONObject(posicion).getJSONObject("value").getString("_rev"), "DELETE");

                    }catch (Exception ex){
                        Toast.makeText(getApplicationContext(), "Error al intentar eliminar el amigo: "+ ex.getMessage() , Toast.LENGTH_LONG).show();
                    }
                    dialogInterface.dismiss();
                }
            });
            confirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "Eliminacion cancelada por el usuario.", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            });
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error al mostrar la confoirmacion: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return confirmacion.create();
    }
    private class conexionServidor extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder result = new StringBuilder();
            try {
                String uri = parametros[0];
                String metodo = parametros[1];

                URL url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(metodo);

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
                if(jsonObject.isNull("rows")) {
                    if(jsonObject.getBoolean("ok")){
                        Toast.makeText(MainActivity.this, "Amigo eliminado con exito", Toast.LENGTH_SHORT).show();
                        datosJSON.remove(posicion);
                    } else{
                        Toast.makeText(MainActivity.this, "Error no se pudo eliminar el registro de amigo", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    datosJSON = jsonObject.getJSONArray("rows");
                }
                mostrarDatosAmigos();
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, "Error la parsear los datos: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}