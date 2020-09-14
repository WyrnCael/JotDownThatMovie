package com.wyrnlab.jotdownthatmovie.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jota on 13/12/2017.
 */

public class DAO {

    private static DAO instance = null;
    private static Integer DatabaseVersion = 4;

    public static synchronized DAO getInstance(){
        if(instance == null)
            instance = new DAO();
        return instance;
    }

    public AudiovisualInterface readFromSQL(Context context, String title, String year){
        AudiovisualInterface pelicula = null;

        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase db = usdbh.getWritableDatabase();
        Cursor c = db.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language FROM Peliculas WHERE titulo = ? AND anyo = ?", new String[]{ title, year });

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
                pelicula.setTipo(c.getString(10));
                pelicula.setSeasons(c.getString(11));
                pelicula.setOriginalLanguage(c.getString(12));
                pelicula.setSource(General.DB_SOURCE);
            } while(c.moveToNext());
        }

        db.close();

        return pelicula;
    }

    public void delete(Context context, Integer id){
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            //Insertamos los datos en la tabla Peliculas
            db.execSQL("DELETE FROM Peliculas WHERE filmId = ?",
                    new Object[]{ id });
        }

        //Cerramos la base de datos
        db.close();
    }

    public void deleteFromList(Context context, List<AudiovisualInterface> records){
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            List<Integer> ids = new ArrayList<Integer>();
            for(AudiovisualInterface record : records){
                ids.add(record.getId());
            }

            String args = TextUtils.join(", ", ids);

            //Insertamos los datos en la tabla Peliculas
            db.execSQL(String.format("DELETE FROM rows WHERE filmId IN (%s);", args));
        }

        //Cerramos la base de datos
        db.close();
    }

    public Map<String, List<AudiovisualInterface>> readAll(Context context){
        Map<String, List<AudiovisualInterface>> audiovisualByType = new HashMap<String, List<AudiovisualInterface>>();
        audiovisualByType.put(General.ALL_TYPE, new ArrayList<AudiovisualInterface>());
        audiovisualByType.put(General.MOVIE_TYPE, new ArrayList<AudiovisualInterface>());
        audiovisualByType.put(General.TVSHOW_TYPE, new ArrayList<AudiovisualInterface>());

        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase dba = usdbh.getWritableDatabase();
        Cursor c = dba.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language FROM Peliculas ", null);

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
                pelicula.setTipo(c.getString(10));
                pelicula.setSeasons(c.getString(11));
                pelicula.setOriginalLanguage(c.getString(12));
                pelicula.setSource(General.DB_SOURCE);

                audiovisualByType.get(pelicula.getTipo()).add(pelicula);
                audiovisualByType.get(General.ALL_TYPE).add(pelicula);
            } while(c.moveToNext());
        }

        dba.close();

        return audiovisualByType;
    }

    public boolean insert(Context context, AudiovisualInterface pelicula){
        // Comprobamos si la pelicula ya existe
        AudiovisualInterface result = readFromSQL(context, pelicula.getTitulo(), pelicula.getAnyo());
        if(result != null)
            return false;

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

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
            String tipo = pelicula.getTipo();
            String temporadas = pelicula.getSeasons() == null ? "0" : pelicula.getSeasons();
            String idiomaOriginal = pelicula.getOriginalLanguage();

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
            String sql = "INSERT INTO Peliculas (filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            insertStmt.bindString(11, tipo);
            insertStmt.bindString(12, temporadas);
            insertStmt.bindString(13, idiomaOriginal);

            insertStmt.executeInsert();

            //Cerramos la base de datos
            db.close();
        }
        return true;
    }

    public boolean update(Context context, AudiovisualInterface pelicula){
        // Comprobamos si la pelicula ya existe
        AudiovisualInterface result = readFromSQL(context, pelicula.getTitulo(), pelicula.getAnyo());
        if(result == null)
            return false;

        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

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
            String tipo = pelicula.getTipo();
            String temporadas = pelicula.getSeasons() == null ? "0" : pelicula.getSeasons();
            String idiomaOriginal = pelicula.getOriginalLanguage();

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
            String sql = "UPDATE Peliculas SET nombre = ?, anyo = ?, titulo = ?, tituloOriginal = ?, descripcion = ?, image = ?, directores = ?, generos = ?, rating = ?, tipo = ?, temporadas = ?, original_language = ? WHERE filmId = ?";
            SQLiteStatement insertStmt = db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindString(1, nombre);
            insertStmt.bindString(2, anyo);
            insertStmt.bindString(3, titulo);
            insertStmt.bindString(4, tituloOriginal);
            insertStmt.bindString(5, descripcion);
            insertStmt.bindBlob(6, pelicula.getImage());
            insertStmt.bindString(7, directores);
            insertStmt.bindString(8, generos);
            insertStmt.bindString(9, rating);
            insertStmt.bindString(10, tipo);
            insertStmt.bindString(11, temporadas);
            insertStmt.bindString(12, idiomaOriginal);
            insertStmt.bindString(13, id);

            Log.d("Updated", String.valueOf(insertStmt.executeUpdateDelete()));

            //Cerramos la base de datos
            db.close();
        }
        return true;
    }
}
