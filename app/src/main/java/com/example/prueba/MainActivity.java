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
//https://developer.android.com/training/camera/photobasics?hl=es-419#java
public class MainActivity extends AppCompatActivity {
    DB miBD;
    Cursor misAmigos;
    amigos amigo;
    ArrayList<amigos> stringArrayList = new ArrayList<amigos>();
    ArrayList<amigos> copyStringArrayList = new ArrayList<amigos>();
    ListView ltsAmigos;

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
                    adaptadorImagenes adaptadorImg = new adaptadorImagenes(getApplicationContext(), stringArrayList);
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
                        misAmigos.getString(4), //email
                        misAmigos.getString(5)  //urlImg
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
        ltsAmigos = (ListView)findViewById(R.id.ltsAmigos);
        do {
            amigo = new amigos(misAmigos.getString(0),misAmigos.getString(1), misAmigos.getString(2), misAmigos.getString(3), misAmigos.getString(4), misAmigos.getString(5));
            stringArrayList.add(amigo);
        }while(misAmigos.moveToNext());
        adaptadorImagenes adaptadorImg = new adaptadorImagenes(getApplicationContext(), stringArrayList);
        ltsAmigos.setAdapter(adaptadorImg);

        copyStringArrayList.clear();//limpiamos la lista de amigos
        copyStringArrayList.addAll(stringArrayList);//creamos la copia de la lista de amigos...
        registerForContextMenu(ltsAmigos);
     }
}
class amigos{
    String id;
    String nombre;
    String telefono;
    String direccion;
    String email;
    String urlImg;

    public amigos(String id, String nombre, String telefono, String direccion, String email, String urlImg) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.urlImg = urlImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}