package com.example.prueba;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    //direcciones direccion = new direcciones();
    conversores miConversor = new conversores();
    Spinner spnDe, spnA;

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spnTipo = (Spinner)findViewById(R.id.spnTipo);
        spnDe = (Spinner)findViewById(R.id.spnDe);
        spnA = (Spinner)findViewById(R.id.spna);

        spnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                spnDe.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,miConversor.obtenerConversor(position) ));
                spnA.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,miConversor.obtenerConversor(position) ));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button btnConvertir = (Button)findViewById(R.id.btnConvertir);
        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tempVal = (TextView)findViewById(R.id.txtcantidad);
                double cantidad = Double.parseDouble(tempVal.getText().toString());
                int d = spnDe.getSelectedItemPosition();
                int a = spnA.getSelectedItemPosition();
                int tipo = spnTipo.getSelectedItemPosition();

                Double respuesta = miConversor.convertir(tipo,d,a,cantidad);
                tempVal = (TextView)findViewById(R.id.lblrespuesta);
                tempVal.setText("Respuesta: "+ respuesta);
            }
        });
    }
}