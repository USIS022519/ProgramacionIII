package com.example.prueba;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class enviarDatosAmigo extends AsyncTask<String,String, String> {
    Context context;
    utilidadesComunes uc = new utilidadesComunes();
    String resp;

    public enviarDatosAmigo(Context context) {
        this.context = context;
    }

    HttpURLConnection urlConnection;
    @Override
    protected String doInBackground(String... parametros) {
        String jsonResponse = null;
        String jsonDatos = parametros[0];
        BufferedReader reader;
        try {
            URL url = new URL(uc.url_mto);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Accept","application/json");

            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(jsonDatos);
            writer.close();

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream==null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            resp = reader.toString();

            String inputLine;
            StringBuffer stringBuffer = new StringBuffer();
            while ((inputLine=reader.readLine())!= null){
                stringBuffer.append(inputLine+"\n");
            }
            if(stringBuffer.length()==0){
                return null;
            }
            jsonResponse = stringBuffer.toString();
            return jsonResponse;
        }catch (Exception ex){
            //
        }
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
