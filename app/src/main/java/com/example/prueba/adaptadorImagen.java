package com.example.prueba;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class adaptadorImagen extends BaseAdapter {
    Context context;
    ArrayList<amigos> datos;
    LayoutInflater layoutInflater;
    amigos amigo;

    public adaptadorImagen(Context context, ArrayList<amigos> datos){
        this.context = context;
        try {
            this.datos = datos;
        }catch (Exception ex){}
    }
    @Override
    public int getCount() {
        try {
            return datos.size();
        }catch (Exception ex) {
            return 0;
        }
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        amigo = datos.get(i);

        TextView textView = itemView.findViewById(R.id.txtTitulo);
        textView.setText(amigo.getNombre());

        textView = itemView.findViewById(R.id.txtTelefono);
        textView.setText(amigo.getTelefono());

        ImageView imageView = itemView.findViewById(R.id.img);
        try {
            Bitmap imageBitmap = BitmapFactory.decodeFile(amigo.getUrlImg());
            imageView.setImageBitmap(imageBitmap);
        }catch (Exception ex){ }
        return itemView;
    }
}
