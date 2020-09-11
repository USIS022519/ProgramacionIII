package com.example.prueba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    static String nameDB = "db_amigos"; //declaracion de la instancia de la BD
    static String tblAmigos = "CREATE TABLE amigos(idAmigo integer primary key autoincrement, nombre text, telefono text, direccion text, email text)";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nameDB, factory, version); //nameDB -> Creacion de la BD en SQLite...
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblAmigos);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void mantenimientoAmigos(String accion){
        switch (accion){
            case "consultar":

                break;
            case "nuevo":
                break;

            case "modificar":
                break;

            case "eliminar":
                break;

            default:
                break;
        }

    }
}
