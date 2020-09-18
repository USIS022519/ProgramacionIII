package com.example.prueba;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MainActivity extends Activity {
    DB miBD;
    Cursor misAmigos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        obtenerDatosAmigos();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_amigos, menu);

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        misAmigos.moveToPosition(adapterContextMenuInfo.position);
        menu.setHeaderTitle(misAmigos.getString(1));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnxAgregar:
                agregarAmigo("nuevo", new String[]{});
                return true;

            case R.id.mnxModificar:
                String[] dataAmigo = {
                        misAmigos.getString(0),//idAmigo
                        misAmigos.getString(1),//nombre
                        misAmigos.getString(2),//telefono
                        misAmigos.getString(3),//direccion
                        misAmigos.getString(4) //email
                };
                agregarAmigo("modificar",dataAmigo);
                return true;

            case R.id.mnxEliminar:
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    void obtenerDatosAmigos(){
        miBD = new DB(getApplicationContext(), "", null, 1);
        misAmigos = miBD.mantenimientoAmigos("consultar", null);
        if( misAmigos.moveToFirst() ){ //hay registro en la BD que mostrar
            mostrarDatosAmigos();
        } else{ //No tengo registro que mostrar.
            Toast.makeText(getApplicationContext(), "No hay registros de amigos que mostrar",Toast.LENGTH_LONG).show();
            agregarAmigo("nuevo", new String[]{});
        }
    }
    void agregarAmigo(String accion, String[] dataAmigo){
        Bundle enviarParametros = new Bundle();
        enviarParametros.putString("accion",accion);
        enviarParametros.putStringArray("dataAmigo",dataAmigo);
        Intent agregarAmigos = new Intent(MainActivity.this, agregarAmigos.class);
        agregarAmigos.putExtras(enviarParametros);
        startActivity(agregarAmigos);
    }
    void mostrarDatosAmigos(){
        ListView ltsAmigos = (ListView)findViewById(R.id.ltsAmigos);
        ArrayList<String> stringArrayList = new ArrayList<String>();
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, stringArrayList);
        ltsAmigos.setAdapter(stringArrayAdapter);
        do {
            stringArrayList.add(misAmigos.getString(1));
        }while(misAmigos.moveToNext());
        stringArrayAdapter.notifyDataSetChanged();
        registerForContextMenu(ltsAmigos);
     }
}