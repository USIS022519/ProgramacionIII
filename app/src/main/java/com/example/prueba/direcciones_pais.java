package com.example.prueba;

public class direcciones_pais {
    String [][] mun = {
            {"Seleccione un depto"},
            {"Usulutan","Santa Maria", "Santa Elena", "Jiquilisco"},
            {"El Transito", "San Jorge", "San Rafael"},
            {"SRL","La Union","Anamoros","El Carmen"},
            {""}
    };
    String[] paises = {"Guatemala","Honduras","El Salvador","Nicaragua","Costa Rica"};

     String[] obtenerMunicipio(int posicion){
        return mun[posicion];
    }
    String[] obtenerPaises(){
         return paises;
    }

}
