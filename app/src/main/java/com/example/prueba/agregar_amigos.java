package com.example.prueba;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class agregar_amigos extends AppCompatActivity {
    String accion, uniqueID, id="", rev="";
    utilidadesComunes uc = new utilidadesComunes();
    detectarInternet di;
    BDSQLite miBD;
    ImageView imgFotoAmigo;
    Intent takePictureIntent;
    String urlCompletaImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);

        miBD = new BDSQLite(getApplicationContext(),"", null, 1);
        imgFotoAmigo = findViewById(R.id.imgFotoAmigo);

       try {
           FloatingActionButton btnMostrarAmigos = findViewById(R.id.btnMostrarAmigos);
           btnMostrarAmigos.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mostrarAmigos();
               }
           });
           Button btnGuardarAmigo = findViewById(R.id.btnGuardarAmigos);
           btnGuardarAmigo.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   guardarAmigo();
               }
           });
           mostrarDatosAmigo();
           tomarFotoAmigo();
       }catch (Exception ex){
           Toast.makeText(getApplicationContext(), "Error al agregar amigos: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
       }
    }
    void mostrarDatosAmigo(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                JSONObject dataAmigo = new JSONObject(recibirParametros.getString("dataAmigo")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtNombreAmigo);
                tempVal.setText(dataAmigo.getString("nombre"));

                tempVal = (TextView)findViewById(R.id.txtDireccionAmigo);
                tempVal.setText(dataAmigo.getString("direccion"));

                tempVal = (TextView)findViewById(R.id.txtTelefonoAmigo);
                tempVal.setText(dataAmigo.getString("telefono"));

                tempVal = (TextView)findViewById(R.id.txtEmailAmigo);
                tempVal.setText(dataAmigo.getString("email"));

                id = dataAmigo.getString("_id");
                rev = dataAmigo.getString("_rev");
                uniqueID = dataAmigo.getString("uniqueID");
                urlCompletaImg = dataAmigo.getString("url");
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgFotoAmigo.setImageBitmap(imageBitmap);
            } else {
                uniqueID = uc.generateUniqueId();
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Error al mostrar datos: "+ ex.getMessage() , Toast.LENGTH_LONG).show();
        }
    }
    private void mostrarAmigos(){
        Intent mostrarAmigos = new Intent(agregar_amigos.this, MainActivity.class);
        startActivity(mostrarAmigos);
    }
    private void guardarAmigo(){
        TextView tempVal = findViewById(R.id.txtNombreAmigo);
        String nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtDireccionAmigo);
        String direccion = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtTelefonoAmigo);
        String telefono = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtEmailAmigo);
        String email = tempVal.getText().toString();

        try {
            JSONObject datosAmigo = new JSONObject();
            if (accion.equals("modificar") && id.length()>1 && rev.length()>1){
                datosAmigo.put("_id",id);
                datosAmigo.put("_rev",rev);
            }
            datosAmigo.put("uniqueID", uniqueID);
            datosAmigo.put("nombre", nombre);
            datosAmigo.put("direccion", direccion);
            datosAmigo.put("telefono", telefono);
            datosAmigo.put("email", email);
            datosAmigo.put("url", urlCompletaImg);
            datosAmigo.put("actualizado", "si");

            di = new detectarInternet(getApplicationContext());
            if( di.hayConexionInternet() ) {
                enviarDatosAmigo objGuardarAmigo = new enviarDatosAmigo(getApplicationContext());
                String resp = objGuardarAmigo.execute(datosAmigo.toString()).get();
                try{
                    JSONObject respJSON = new JSONObject(resp);
                    if(respJSON.getBoolean("ok")){
                        datosAmigo.put("_id",respJSON.getString("id"));
                        datosAmigo.put("_rev",respJSON.getString("rev"));
                        miBD.mantenimientoAmigos(accion, datosAmigo);
                        Toast.makeText(getApplicationContext(), "Datos de amigo guardado con exito ", Toast.LENGTH_SHORT).show();
                        mostrarAmigos();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al intentar guardar datos de amigo", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error al guardar amigo: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                datosAmigo.put("_id",id);
                datosAmigo.put("_rev",rev);
                datosAmigo.put("actualizado", "no");
                miBD.mantenimientoAmigos(accion, datosAmigo);
            }
            if(!di.hayConexionInternet()){
                mostrarAmigos();
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                            Uri photoURI = FileProvider.getUriForFile(agregar_amigos.this, "com.example.prueba.fileprovider", photoFile);
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
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }
}