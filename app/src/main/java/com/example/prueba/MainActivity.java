package com.example.prueba;

import android.app.Activity;
import android.net.wifi.aware.PublishConfig;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public Button btnCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCalcular = (Button)findViewById(R.id.btnCalcular);
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                procesar(view);
            }
        });
    }
    public void procesar(View vista){
        try {
            RadioGroup optOperaciones = (RadioGroup) findViewById(R.id.optOperaciones);
            Spinner cboOperaciones = (Spinner)findViewById(R.id.cboOperaciones);

            TextView tempVal = (TextView) findViewById(R.id.txtnum1);
            double num1 = Double.parseDouble(tempVal.getText().toString());

            tempVal = (TextView) findViewById(R.id.txtnum2);
            double num2 = Double.parseDouble(tempVal.getText().toString());

            double respuesta = 0;
            //Este es para el radiogroup y los radiobuttons
            switch (optOperaciones.getCheckedRadioButtonId()) {
                case R.id.optSuma:
                    respuesta = num1 + num2;
                    break;
                case R.id.optResta:
                    respuesta = num1 - num2;
                    break;
                case R.id.optMultiplicar:
                    respuesta = num1 * num2;
                    break;
                case R.id.optDivision:
                    respuesta = num1 / num2;
                    break;
            }
            //Este es para el spinner... -> Combobox.
            switch (cboOperaciones.getSelectedItemPosition()){
                case 1: //suma
                    respuesta = num1 + num2;
                    break;
                case 2: //resta
                    respuesta = num1 - num2;
                    break;
                case 3: //multiplicacion
                    respuesta = num1 * num2;
                    break;
                case 4: //division
                    respuesta = num1 / num2;
                    break;
            }
            tempVal = (TextView) findViewById(R.id.lblRespuesta);
            tempVal.setText("Respuesta: " + respuesta);
        }catch (Exception err){
            TextView temp = (TextView) findViewById(R.id.lblRespuesta);
            temp.setText("Por favor ingrese los numeros correspondientes.");

            Toast.makeText(getApplicationContext(),"Por favor ingrese los numeros.",Toast.LENGTH_LONG).show();
        }
    }
}