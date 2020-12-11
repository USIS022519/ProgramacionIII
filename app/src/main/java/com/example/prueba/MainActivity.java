package com.example.prueba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {
    BDSQLite miBD;
    Cursor misAmigos;
    amigos amigo;
    JSONArray datosJSON;
    JSONObject jsonObject = new JSONObject();
    Integer posicion;

    ArrayList<amigos> stringArrayList = new ArrayList<amigos>();
    ArrayList<amigos> copyStringArrayList = new ArrayList<amigos>();
    ListView ltsAmigos;

    utilidadesComunes uc;
    detectarInternet di;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miBD = new BDSQLite(getApplicationContext(), "", null, 1);

        di = new detectarInternet(getApplicationContext());
        if( di.hayConexionInternet() ) {
            try {
                conexionServidor objObtenerAmigos = new conexionServidor();
                String resp = objObtenerAmigos.execute(uc.url_consulta, "GET").get();

                JSONObject respJSON = new JSONObject(resp);
                datosJSON = respJSON.getJSONArray("rows");
                mostrarDatosAmigos();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "Error al obtener datos de amigos del servidor... "+ ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            sincronizar();
        } else {
            obtenerDatosSQLiteAmigos();
        }

        FloatingActionButton btnAgregarNuevoAmigos = findViewById(R.id.btnAgregarAmigos);
        btnAgregarNuevoAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    agregarNuevosAmigos("nuevo", jsonObject);
            }
        });
        buscarAmigos();
    }
    void sincronizar(){
        misAmigos = miBD.pendienteSincronizar();
        if (misAmigos.moveToFirst()) {
            jsonObject = new JSONObject();
            try {
                Toast.makeText(getApplicationContext(), "Sincronizando...", Toast.LENGTH_SHORT).show();
                do {
                    if( misAmigos.getString(2).length()>1 && misAmigos.getString(3).length()>1 ) {
                        jsonObject.put("_id", misAmigos.getString(2));
                        jsonObject.put("_rev", misAmigos.getString(3));
                    }
                    jsonObject.put("uniqueID", misAmigos.getString(1));
                    if( misAmigos.getString(9).equals("no") ) {
                        jsonObject.put("nombre", misAmigos.getString(4));
                        jsonObject.put("direccion", misAmigos.getString(5));
                        jsonObject.put("telefono", misAmigos.getString(6));
                        jsonObject.put("email", misAmigos.getString(7));
                        jsonObject.put("url", misAmigos.getString(8));
                        jsonObject.put("actualizado", "si");

                        enviarDatosAmigo objGuardarAmigo = new enviarDatosAmigo(getApplicationContext());
                        String resp = objGuardarAmigo.execute(jsonObject.toString()).get();
                        if (resp != null) {
                            JSONObject respJSON = new JSONObject(resp);
                            if (respJSON.getBoolean("ok")) {
                                jsonObject.put("_id", respJSON.getString("id"));
                                jsonObject.put("_rev", respJSON.getString("rev"));

                                miBD.mantenimientoAmigos("modificar", jsonObject);
                                Toast.makeText(getApplicationContext(), "Sincronizacion con exito", Toast.LENGTH_SHORT).show();

                                conexionServidor objObtenerAmigos = new conexionServidor();
                                resp = objObtenerAmigos.execute(uc.url_consulta, "GET").get();

                                respJSON = new JSONObject(resp);
                                datosJSON = respJSON.getJSONArray("rows");
                                mostrarDatosAmigos();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al sincronizar", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo sincronizar", Toast.LENGTH_SHORT).show();
                        }
                    } else if(misAmigos.getString(9).equals("eliminado")){
                        conexionServidor objEliminarAmigo = new conexionServidor();
                        String resp = objEliminarAmigo.execute(uc.url_mto + misAmigos.getString(2) + "?rev=" + misAmigos.getString(3), "DELETE").get();
                        JSONObject respJSON = new JSONObject(resp);
                        if(respJSON.getBoolean("ok")) {
                            miBD.mantenimientoAmigos("eliminar", jsonObject);
                            Toast.makeText(getApplicationContext(), "Sincronizacion con exito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo sincronizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }while (misAmigos.moveToNext());
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Error al intentar sincronizar: "+ ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    void buscarAmigos(){
        final TextView tempVal = (TextView)findViewById(R.id.txtBuscarAmigo);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    stringArrayList.clear();
                    if (tempVal.getText().toString().trim().length() < 1) {//no hay texto para buscar
                        stringArrayList.addAll(copyStringArrayList);
                    } else {//hacemos la busqueda
                        for (amigos am : copyStringArrayList) {
                            String nombre = am.getNombre();
                            if (nombre.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())) {
                                stringArrayList.add(am);
                            }
                        }
                    }
                    adaptadorImagen adaptadorImg = new adaptadorImagen(getApplicationContext(), stringArrayList);
                    ltsAmigos.setAdapter(adaptadorImg);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
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
        try {
            if(datosJSON.length()>0) {
                stringArrayList.clear();
                ltsAmigos = findViewById(R.id.ltsAgendaAmigosCouchDB);
                JSONObject datosAmigo;
                for (int i = 0; i < datosJSON.length(); i++) {
                    datosAmigo = datosJSON.getJSONObject(i).getJSONObject("value");
                    amigo = new amigos(
                            datosAmigo.getString("uniqueID"),
                            datosAmigo.getString("_id"),
                            datosAmigo.getString("_rev"),
                            datosAmigo.getString("nombre"),
                            datosAmigo.getString("direccion"),
                            datosAmigo.getString("telefono"),
                            datosAmigo.getString("email"),
                            datosAmigo.getString("url"),
                            datosAmigo.getString("actualizado")
                    );
                    stringArrayList.add(amigo);
                }
                adaptadorImagen adaptadorImg = new adaptadorImagen(getApplicationContext(), stringArrayList);
                ltsAmigos.setAdapter(adaptadorImg);

                copyStringArrayList.clear();//limpiamos la lista de amigos
                copyStringArrayList.addAll(stringArrayList);//creamos la copia de la lista de amigos...
                registerForContextMenu(ltsAmigos);
            }else {
                agregarNuevosAmigos("nuevo", jsonObject);
            }
        }catch (Exception ex){
            Toast.makeText(MainActivity.this, "Error al mostrar los datos: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void obtenerDatosSQLiteAmigos(){
        try {

            misAmigos = miBD.mantenimientoAmigos("consultar", null);
            if (misAmigos.moveToFirst()) { //hay registro en la BD que mostrar
                datosJSON =  new JSONArray();
                JSONObject valueJSON;
                do {
                    jsonObject = new JSONObject();
                    valueJSON = new JSONObject();

                    jsonObject.put("uniqueID", misAmigos.getString(1));
                    jsonObject.put("_id", misAmigos.getString(2));
                    jsonObject.put("_rev", misAmigos.getString(3));
                    jsonObject.put("nombre", misAmigos.getString(4));
                    jsonObject.put("direccion", misAmigos.getString(5));
                    jsonObject.put("telefono", misAmigos.getString(6));
                    jsonObject.put("email", misAmigos.getString(7));
                    jsonObject.put("url", misAmigos.getString(8));
                    jsonObject.put("actualizado", misAmigos.getString(9));

                    valueJSON.put("value",jsonObject);
                    datosJSON.put(valueJSON);

                } while (misAmigos.moveToNext());
                mostrarDatosAmigos();
            } else { //No tengo registro que mostrar.
                Toast.makeText(getApplicationContext(), "No hay registros de amigos que mostrar", Toast.LENGTH_LONG).show();
                agregarNuevosAmigos("nuevo", jsonObject);
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
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
                        JSONObject datosAmigo = new JSONObject();

                        if( di.hayConexionInternet() ) {
                            datosAmigo.put("uniqueID", datosJSON.getJSONObject(posicion).getJSONObject("value").getString("uniqueID"));
                            miBD.mantenimientoAmigos("eliminar", datosAmigo);

                            conexionServidor objEliminarAmigo = new conexionServidor();
                            String resp = objEliminarAmigo.execute(uc.url_mto +
                                    datosJSON.getJSONObject(posicion).getJSONObject("value").getString("_id") + "?rev=" +
                                    datosJSON.getJSONObject(posicion).getJSONObject("value").getString("_rev"), "DELETE").get();

                            JSONObject respJSON = new JSONObject(resp);
                            if(respJSON.getBoolean("ok")) {
                                datosJSON.remove(posicion);
                                mostrarDatosAmigos();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al intentar eliminar el amigo" , Toast.LENGTH_LONG).show();
                            }
                        } else {
                            datosJSON.getJSONObject(posicion).getJSONObject("value").put("actualizado","eliminado");
                            miBD.mantenimientoAmigos("modificar", datosJSON.getJSONObject(posicion).getJSONObject("value"));

                            datosJSON.remove(posicion);
                            mostrarDatosAmigos();
                        }
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
        }
    }
}