package com.wyrnlab.jotdownthatmovie.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wyrnlab.jotdownthatmovie.sql.PeliculasSQLiteHelper;

import java.util.ArrayList;
import java.util.List;

import api.search.Pelicula;

/**
 * Created by Jota on 13/12/2017.
 */

public class DAO {

    private static DAO instance = null;

    public static synchronized DAO getInstance(){
        if(instance == null)
            instance = new DAO();
        return instance;
    }

    public Pelicula readFromSQL(Context context, int position){
        String[] campos = new String[] {"filmId", "nombre", "anyo", "titulo", "tituloOriginal", "descripcion", "image", "directores", "generos", "rating" };
        String[] args = new String[]  { Integer.toString(position) };

        Pelicula pelicula = new Pelicula();

        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        Cursor c = db.query("Peliculas", campos, null, null, null, null, null);

        //Nos aseguramos de que existe al menos un registro
        int pos = 1;
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                if (pos == position){
                    pelicula.setId(Integer.parseInt(c.getString(0)));
                    pelicula.setAnyo(c.getString(2));
                    pelicula.setTitulo(c.getString(3));
                    pelicula.setTituloOriginal(c.getString(4));
                    pelicula.setDescripcion(c.getString(5));
                    pelicula.setImage(c.getBlob(6));
                    pelicula.addDirectores(c.getString(7));
                    pelicula.addGeneros(c.getString(8));
                    pelicula.setRating(Double.parseDouble(c.getString(9)));
                }
                pos++;
            } while(c.moveToNext());
        }

        db.close();

        return pelicula;
    }

    public void delete(Context context, String nombre, String anyo){
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, 1);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            //Insertamos los datos en la tabla Peliculas
            db.execSQL("DELETE FROM Peliculas WHERE nombre='" + nombre + "' AND anyo=" + anyo);
        }

        //Cerramos la base de datos
        db.close();
    }

    public List<String> readAll(Context context){
        List<String> result = new ArrayList<String>();
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, 1);

        SQLiteDatabase dba = usdbh.getWritableDatabase();
        Cursor c = dba.rawQuery(" SELECT nombre, anyo FROM Peliculas ", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros

            do {
                String nombre = c.getString(0);
                String anyo  = c.getString(1);
                result.add(nombre + " (" + anyo + ")\n");

            } while(c.moveToNext());
        }

        dba.close();

        return result;
    }
}
