package com.example.prueba;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public class BDSQLite extends SQLiteOpenHelper {
    static String nameDB = "db_amigos"; //declaracion de la instancia de la BD
    static String tblAmigos = "CREATE TABLE amigos(idAmigo integer primary key autoincrement, uniqueID text, _id text, _rev text, nombre text, direccion text, telefono text, email text, url text, actualizado text)";
    Context contexto;

    public BDSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nameDB, factory, version); //nameDB -> Creacion de la BD en SQLite...
        this.contexto = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblAmigos);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Cursor mantenimientoAmigos(String accion, JSONObject data){
        SQLiteDatabase sqLiteDatabaseReadable = getReadableDatabase();
        SQLiteDatabase sqLiteDatabaseWritable = getWritableDatabase();
        Cursor cursor = null;
        try {
            switch (accion) {
                case "consultar":
                    cursor = sqLiteDatabaseReadable.rawQuery("SELECT * FROM amigos WHERE actualizado != 'eliminado'", null);
                    break;
                case "nuevo":
                    sqLiteDatabaseWritable.execSQL("INSERT INTO amigos (uniqueID,_id,_rev,nombre,direccion,telefono,email,url, actualizado) VALUES('"+ data.getString("uniqueID") + "','"+ data.getString("_id") + "','"+ data.getString("_rev") +"','" + data.getString("nombre") + "','" + data.getString("direccion") + "','" + data.getString("telefono") + "','" + data.getString("email") + "','" + data.getString("url") +"','"+ data.getString("actualizado") +"')");
                    break;
                case "modificar":
                    sqLiteDatabaseWritable.execSQL("UPDATE amigos SET _id='" + data.getString("_id") +"', _rev='" + data.getString("_rev") + "', nombre='" + data.getString("nombre") + "',direccion='" + data.getString("direccion") + "',telefono='" + data.getString("telefono") + "',email='" + data.getString("email") + "', url='" + data.getString("url") + "', actualizado='" + data.getString("actualizado") + "' WHERE uniqueID='" + data.getString("uniqueID") + "'");
                    break;
                case "eliminar":
                    sqLiteDatabaseWritable.execSQL("DELETE FROM amigos WHERE uniqueID='" + data.getString("uniqueID") + "'");
                    break;
            }
        }catch (Exception ex){
            Toast.makeText(contexto,"Error en  la BD: "+ ex.getMessage() , Toast.LENGTH_LONG).show();
        }
        return cursor;
    }
    public Cursor pendienteSincronizar(){
        SQLiteDatabase sqLiteDatabaseReadable = getReadableDatabase();
        Cursor cursor = sqLiteDatabaseReadable.rawQuery("SELECT * FROM amigos WHERE actualizado!='si'", null);
        return cursor;
    }
}
