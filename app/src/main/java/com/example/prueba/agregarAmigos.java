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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);

        Button btnGuardarAmigos = (Button)findViewById(R.id.btnGuardarAmigos);
        btnGuardarAmigos.setOnClickListener(new View.OnClickListener() {
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

                String[] data = {"",nombre,tel,direccion,email};

                miDB = new DB(getApplicationContext(),"", null, 1);
                miDB.mantenimientoAmigos(accion, data);

                Toast.makeText(getApplicationContext(),"Registro de amigo insertado con exito", Toast.LENGTH_LONG).show();
                Intent mostrarAmigos = new Intent(agregarAmigos.this, MainActivity.class);
                startActivity(mostrarAmigos);
            }
        });
    }
}