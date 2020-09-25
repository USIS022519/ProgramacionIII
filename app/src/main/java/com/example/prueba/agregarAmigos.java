package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class agregarAmigos extends AppCompatActivity {
    DB miDB;
    String accion = "nuevo";
    String idAmigo = "0";
    ImageView imgFotoAmigo;
    String urlCompletaImg;
    Button btnAmigos;
    Intent takePictureIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);

        imgFotoAmigo = findViewById(R.id.imgFotoAmigo);

        btnAmigos = findViewById(R.id.btnMostrarAmigos);
        btnAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarListaAmigos();
            }
        });
        guardarDatosAmigo();
        mostrarDatosAmigo();
        tomarFotoAmigo();
    }
    void tomarFotoAmigo(){
        imgFotoAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    //guardando la imagen
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    }catch (Exception ex){}
                    if (photoFile != null) {
                        try {
                            Uri photoURI = FileProvider.getUriForFile(agregarAmigos.this, "com.example.prueba.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 1);
                        }catch (Exception ex){
                            Toast.makeText(getApplicationContext(), "Error Toma Foto: "+ ex.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFotoAmigo.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagen_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if( storageDir.exists()==false ){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }
    void guardarDatosAmigo(){
        btnAmigos = findViewById(R.id.btnGuardarAmigos);
        btnAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tempVal = findViewById(R.id.txtNombreAmigo);
                String nombre = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtTelefonoAmigo);
                String tel = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtDireccionAmigo);
                String direccion = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtEmailAmigo);
                String email = tempVal.getText().toString();

                String[] data = {idAmigo,nombre,tel,direccion,email,urlCompletaImg};

                miDB = new DB(getApplicationContext(),"", null, 1);
                miDB.mantenimientoAmigos(accion, data);

                Toast.makeText(getApplicationContext(),"Registro de amigo insertado con exito", Toast.LENGTH_LONG).show();
                mostrarListaAmigos();
            }
        });
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

                urlCompletaImg = dataAmigo[5];
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFotoAmigo.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            ///
        }
    }
}