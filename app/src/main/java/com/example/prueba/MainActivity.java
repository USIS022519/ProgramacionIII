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
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if(sensor==null){
            finish();
        }
           final TextView lblSensorProximidad = (TextView)findViewById(R.id.lblSensorProximidad);
           sensorEventListener = new SensorEventListener() {
               @Override
               public void onSensorChanged(SensorEvent sensorEvent) {
                    if( sensorEvent.values[0]>=0 && sensorEvent.values[0]<=4 ){
                        getWindow().getDecorView().setBackgroundColor(Color.RED);
                        lblSensorProximidad.setText("LEJOS: "+ sensorEvent.values[0]);
                    } else if(sensorEvent.values[0]>4 && sensorEvent.values[0]<=8 ){
                        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                        lblSensorProximidad.setText("INTERMEDIO: "+ sensorEvent.values[0]);
                    } else{
                        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                        lblSensorProximidad.setText("CERCA: "+ sensorEvent.values[0]);
                    }
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