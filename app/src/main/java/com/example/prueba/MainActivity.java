package com.example.prueba;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    SensorManager sensorManager;
    Sensor sensor;
    SensorEventListener sensorEventListener;

    @Override
    protected void onPause() {
        detener();
        super.onPause();
    }
    @Override
       protected void onResume() {
        iniciar();
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null){
            finish();
        }
           final TextView lblSensorAcelerometro = (TextView)findViewById(R.id.lblSensorAcelerometro);
           sensorEventListener = new SensorEventListener() {
               @Override
               public void onSensorChanged(SensorEvent sensorEvent) {
                   double acelerometro = sensorEvent.values[0];
                   if( acelerometro>=-9 && acelerometro<=0 ) {
                       getWindow().getDecorView().setBackgroundColor(Color.RED);
                   } else if( acelerometro>0 && acelerometro<=5){
                       getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                   } else if(acelerometro>5 && acelerometro<=10){
                       getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                   }
                   lblSensorAcelerometro.setText("VALOR: " + acelerometro);
               }
               @Override
               public void onAccuracyChanged(Sensor sensor, int i) {

               }
           };
           iniciar();
    }
    void iniciar(){
           sensorManager.registerListener(sensorEventListener, sensor, 2000*1000);
    }
    void detener(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}