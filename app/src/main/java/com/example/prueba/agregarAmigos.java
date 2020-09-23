package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class agregarAmigos extends AppCompatActivity {
    DB miDB;
    String accion = "nuevo";
    String idAmigo = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);

        Button btnAmigos = (Button)findViewById(R.id.btnGuardarAmigos);
        btnAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tempVal = (TextView)findViewById(R.id.txtNombreAmigo);
                String nombre = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtTelefonoAmigo);
                String tel = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtDireccionAmigo);
                String direccion = tempVal.getText().toString();

                tempVal = (TextView)findViewById(R.id.txtEmailAmigo);
                String email = tempVal.getText().toString();

                String[] data = {idAmigo,nombre,tel,direccion,email};

                miDB = new DB(getApplicationContext(),"", null, 1);
                miDB.mantenimientoAmigos(accion, data);

                Toast.makeText(getApplicationContext(),"Registro de amigo insertado con exito", Toast.LENGTH_LONG).show();
                mostrarListaAmigos();
            }
        });
        btnAmigos = (Button)findViewById(R.id.btnMostrarAmigos);
        btnAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarListaAmigos();
            }
        });
        mostrarDatosAmigo();
    }
    void mostrarListaAmigos(){
        Intent mostrarAmigos = new Intent(agregarAmigos.this, MainActivity.class);
        startActivity(mostrarAmigos);
    }
    void mostrarDatosAmigo(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                String[] dataAmigo = recibirParametros.getStringArray("dataAmigo");

                idAmigo = dataAmigo[0];

                TextView tempVal = (TextView)findViewById(R.id.txtNombreAmigo);
                tempVal.setText(dataAmigo[1]);

                tempVal = (TextView)findViewById(R.id.txtTelefonoAmigo);
                tempVal.setText(dataAmigo[2]);

                tempVal = (TextView)findViewById(R.id.txtDireccionAmigo);
                tempVal.setText(dataAmigo[3]);

                tempVal = (TextView)findViewById(R.id.txtEmailAmigo);
                tempVal.setText(dataAmigo[4]);
            }
        }catch (Exception ex){
            ///
        }
    }
}