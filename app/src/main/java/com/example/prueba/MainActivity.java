package com.example.prueba;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DB miBD;
    Cursor misAmigos;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayList<String> copyStringArrayList = new ArrayList<String>();
    ArrayAdapter<String> stringArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton btnAgregarAmigos = (FloatingActionButton)findViewById(R.id.btnAgregarAmigos);
        btnAgregarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAmigo("nuevo", new String[]{});
            }
        });
        obtenerDatosAmigos();
        buscarAmigos();
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
    void buscarAmigos(){
        final TextView tempVal = (TextView)findViewById(R.id.txtBuscarAmigos);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stringArrayList.clear();
                if( tempVal.getText().toString().trim().length()<1 ){//no hay texto para buscar
                    stringArrayList.addAll(copyStringArrayList);
                } else{//hacemos la busqueda
                    for (String amigo : copyStringArrayList){
                        if(amigo.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())){
                            stringArrayList.add(amigo);
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
                AlertDialog eliminarFriend =  eliminarAmigo();
                eliminarFriend.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    AlertDialog eliminarAmigo(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
        confirmacion.setTitle(misAmigos.getString(1));
        confirmacion.setMessage("Esta seguro de eliminar el registro?");
        confirmacion.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                miBD.mantenimientoAmigos("eliminar",new String[]{misAmigos.getString(0)});
                obtenerDatosAmigos();
                Toast.makeText(getApplicationContext(), "Amigo eliminado con exito.",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        confirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Eliminacion cancelada por el usuario.",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        return confirmacion.create();
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
        stringArrayList.clear();
        ListView ltsAmigos = (ListView)findViewById(R.id.ltsAmigos);
        stringArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, stringArrayList);
        ltsAmigos.setAdapter(stringArrayAdapter);
        do {
            stringArrayList.add(misAmigos.getString(1));
        }while(misAmigos.moveToNext());

        copyStringArrayList.clear();//limpiamos la lista de amigos
        copyStringArrayList.addAll(stringArrayList);//creamos la copia de la lista de amigos...

        stringArrayAdapter.notifyDataSetChanged();
        registerForContextMenu(ltsAmigos);
     }
}