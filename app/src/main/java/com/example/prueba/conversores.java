package com.example.prueba;

public class conversores {
    String [][] etiquetas = {
            {"Dolar","Euro","Quetzal","Lempira","Colon SV","Cordobas","Colon CR"},
            {"Metro","CM", "Pulgadas", "Pies","Varas","Yardas","Km","Millas"},
            {"Libra", "Gramos", "Kilogramos","Onzas","Quintal","Toneladas"},
            {"MegaByte","Bit","Byte","KiloByte", "GigaByte","TB"},
            {"Hora","Segundos","Minutos","Dia","Semana"}
    };
    Double [][] valores = {
            {1.0,0.85,7.74,24.80,8.75,34.62,597.23},
            {1.0,100.0,39.3701,3.280841666667,1.1963081929167,1.0936138888889999077,0.001,0.000621371},
            {}
    };
     String[] obtenerConversor(int posicion){
        return etiquetas[posicion];
    }
    double convertir(int tipo, int de, int a, double cantidad){
         return valores[tipo][a] / valores[tipo][de] * cantidad;
    }
}
