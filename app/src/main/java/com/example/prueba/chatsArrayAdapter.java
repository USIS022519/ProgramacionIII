package com.example.prueba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class chatsArrayAdapter extends ArrayAdapter {
    private Context context;
    private List<chatMessage> chatMessageList = new ArrayList<>();
    private TextView chatText;

    public chatsArrayAdapter(@NonNull Context context, int resourceId) {
        super(context, resourceId);
        this.context = context;
    }
    public void add(chatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }
    public int getCount(){
        return chatMessageList.size();
    }
    public chatMessage getItem(int index){
        return chatMessageList.get(index);
    }
    public View getView(int posicion, View view, ViewGroup viewGroup){
        View fila = view;
        try {
            chatMessage objChatMessage = getItem(posicion);

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (objChatMessage.posicion) {
                fila = layoutInflater.inflate(R.layout.msgizquierdo, viewGroup, false);
                chatText = fila.findViewById(R.id.lblmsgi);
            } else {
                fila = layoutInflater.inflate(R.layout.msgderecho, viewGroup, false);
                chatText = fila.findViewById(R.id.lblmsgd);
            }
            chatText.setText(objChatMessage.message);

        }catch (Exception ex){
            Toast.makeText(context, "Error al mostrar el msg: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return fila;
    }
}
