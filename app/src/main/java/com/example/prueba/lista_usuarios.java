package com.example.prueba;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class lista_usuarios extends AppCompatActivity {
    DatabaseReference mDatabaseReference;
    ListView ltsUsuarios;
    JSONArray datosJSONArray = new JSONArray();
    JSONObject datosJSONObject;
    MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        mostrarListadoUsuarios();
    }
    private void mostrarListadoUsuarios(){
        ltsUsuarios = findViewById(R.id.ltsUsuarios);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        mDatabaseReference.orderByChild("token").equalTo(myFirebaseInstanceIdService.miToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    if( snapshot.getChildrenCount()<=0 ){
                        registrarUsuario();
                        finish();
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error al saber si estoy registrado: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                    registrarUsuario();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    ArrayList<usuarios> stringArrayList = new ArrayList<usuarios>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        usuarios user = dataSnapshot.getValue(usuarios.class);
                        stringArrayList.add(user);
                    }
                    adaptadorImagenes adaptadorImg = new adaptadorImagenes(getApplicationContext(), stringArrayList);
                    ltsUsuarios.setAdapter(adaptadorImg);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error al recuperar los amigos: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void registrarUsuario(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}