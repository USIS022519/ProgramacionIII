package com.example.prueba;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.wifi.aware.PublishConfig;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.patria_querida);

            final Button btnReproducir = (Button) findViewById(R.id.btnReproducir);
            btnReproducir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mediaPlayer.isPlaying()) {
                        iniciar();
                        btnReproducir.setBackgroundResource(R.drawable.pause);
                    } else {
                        parar();
                        btnReproducir.setBackgroundResource(R.drawable.play);
                    }
                }
            });
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    void iniciar(){
        mediaPlayer.start();
    }
    void parar(){
        mediaPlayer.pause();
    }
}