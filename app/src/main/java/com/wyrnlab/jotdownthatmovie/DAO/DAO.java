package com.wyrnlab.jotdownthatmovie.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

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

    public Pelicula readFromSQL(Context context, String title, String year){
        Pelicula pelicula = null;

        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, 1);

        SQLiteDatabase db = usdbh.getWritableDatabase();
        Cursor c = db.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating FROM Peliculas WHERE titulo = ? AND anyo = ?", new String[]{ title, year });

        //Nos aseguramos de que existe al menos un registro
        int pos = 1;
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                pelicula = new Pelicula();
                pelicula.setId(Integer.parseInt(c.getString(0)));
                pelicula.setAnyo(c.getString(2));
                pelicula.setTitulo(c.getString(3));
                pelicula.setTituloOriginal(c.getString(4));
                pelicula.setDescripcion(c.getString(5));
                pelicula.setImage(c.getBlob(6));
                pelicula.addDirectores(c.getString(7));
                pelicula.addGeneros(c.getString(8));
                pelicula.setRating(Double.parseDouble(c.getString(9)));
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
            db.execSQL("DELETE FROM Peliculas WHERE nombre=? AND anyo=?",
                    new Object[]{ nombre, anyo });
        }

        //Cerramos la base de datos
        db.close();
    }

    public List<Pelicula> readAll(Context context){
        List<Pelicula> result = new ArrayList<Pelicula>();
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, 1);

        SQLiteDatabase dba = usdbh.getWritableDatabase();
        Cursor c = dba.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating FROM Peliculas ", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros

            do {
                Pelicula pelicula = new Pelicula();
                pelicula.setId(Integer.parseInt(c.getString(0)));
                pelicula.setAnyo(c.getString(2));
                pelicula.setTitulo(c.getString(3));
                pelicula.setTituloOriginal(c.getString(4));
                pelicula.setDescripcion(c.getString(5));
                pelicula.setImage(c.getBlob(6));
                pelicula.addDirectores(c.getString(7));
                pelicula.addGeneros(c.getString(8));
                pelicula.setRating(Double.parseDouble(c.getString(9)));
                result.add(pelicula);
            } while(c.moveToNext());
        }

        dba.close();

        return result;
    }

    public boolean insert(Context context, Pelicula pelicula){
        // Comprobamos si la pelicula ya existe
        Pelicula result = readFromSQL(context, pelicula.getTitulo(), pelicula.getAnyo());
        if(result != null)
            return false;

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, 1);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            //Generamos los datos
            String id = Integer.toString(pelicula.getId());
            String nombre = pelicula.getTitulo();
            String anyo = pelicula.getAnyo();
            String titulo = pelicula.getTitulo();
            String tituloOriginal = pelicula.getTituloOriginal();
            String descripcion = pelicula.getDescripcion();
            String imagePath = pelicula.getImagePath();

            String directores = "";
            if(pelicula.getDirectores().size() > 0){
                directores = pelicula.getDirectores().get(0);
                for (int i = 1; i < pelicula.getDirectores().size() ; i++){
                    if (i > 0){
                        directores += ", " + pelicula.getDirectores().get(i);
                    }
                    else directores += pelicula.getDirectores().get(i);
                }
            }

            String generos = "";
            if(pelicula.getGeneros().size() > 0){
                generos = pelicula.getGeneros().get(0);
                for (int i = 1; i < pelicula.getGeneros().size() ; i++){
                    if (i>0){
                        generos += ", " + pelicula.getGeneros().get(i).toLowerCase();
                    }
                    else generos += pelicula.getGeneros().get(i);
                }
            }

            String rating = Double.toString(pelicula.getRating());


            //Insertamos los datos en la tabla Peliculas
            String sql = "INSERT INTO Peliculas (filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStmt = db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindString(1, id);
            insertStmt.bindString(2, nombre);
            insertStmt.bindString(3, anyo);
            insertStmt.bindString(4, titulo);
            insertStmt.bindString(5, tituloOriginal);
            insertStmt.bindString(6, descripcion);
            insertStmt.bindBlob(7, pelicula.getImage());
            insertStmt.bindString(8, directores);
            insertStmt.bindString(9, generos);
            insertStmt.bindString(10, rating);

            insertStmt.executeInsert();

            //Cerramos la base de datos
            db.close();
        }
        return true;
    }
}
