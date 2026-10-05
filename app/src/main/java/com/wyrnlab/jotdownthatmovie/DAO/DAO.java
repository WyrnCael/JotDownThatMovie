package com.wyrnlab.jotdownthatmovie.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.stream.JsonWriter;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;

import java.io.IOException;
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
        Cursor c = db.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language, viewed FROM Peliculas WHERE titulo = ? AND anyo = ?", new String[]{ title, year });

        //Nos aseguramos de que existe al menos un registro
        int pos = 1;
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros
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
                pelicula.setViewed(c.getInt(13));
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
        audiovisualByType.put(General.VIEWED, new ArrayList<AudiovisualInterface>());

        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase dba = usdbh.getWritableDatabase();
        Cursor c = dba.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language, viewed FROM Peliculas ", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya m�s registros

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
                pelicula.setViewed(c.getInt(13));
                pelicula.setSource(General.DB_SOURCE);

                if(pelicula.getViewed()){
                    audiovisualByType.get(General.VIEWED).add(pelicula);
                } else {
                    audiovisualByType.get(pelicula.getTipo()).add(pelicula);
                    audiovisualByType.get(General.ALL_TYPE).add(pelicula);
                }
            } while(c.moveToNext());
        }

        dba.close();

        return audiovisualByType;
    }

    // Streams every record straight from the cursor into the JsonWriter, one row at a
    // time, so exporting thousands of records (each with a poster image blob) never has
    // to hold the whole dataset - or its base64-inflated JSON - in memory at once.
    public void streamExportAll(Context context, JsonWriter writer) throws IOException {
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase dba = usdbh.getWritableDatabase();
        Cursor c = dba.rawQuery(" SELECT filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language, viewed FROM Peliculas ", null);

        try {
            if (c.moveToFirst()) {
                do {
                    byte[] image = c.getBlob(6);

                    writer.beginObject();
                    writer.name("filmId").value(Integer.parseInt(c.getString(0)));
                    writer.name("titulo").value(c.getString(3));
                    writer.name("tituloOriginal").value(c.getString(4));
                    writer.name("anyo").value(c.getString(2));
                    writer.name("descripcion").value(c.getString(5));
                    writer.name("imageBase64").value(image != null ? Base64.encodeToString(image, Base64.NO_WRAP) : null);
                    writer.name("directores").value(c.getString(7));
                    writer.name("generos").value(c.getString(8));
                    writer.name("rating").value(c.getString(9));
                    writer.name("tipo").value(c.getString(10));
                    writer.name("temporadas").value(c.getString(11));
                    writer.name("originalLanguage").value(c.getString(12));
                    writer.name("viewed").value(c.getInt(13) != 0);
                    writer.endObject();
                } while (c.moveToNext());
            }
        } finally {
            c.close();
            dba.close();
        }
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
            String directores = pelicula.getDirectoresToString();
            String generos = pelicula.getGenerosToStrig();
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
            String directores = pelicula.getDirectoresToString();
            String generos = pelicula.getGenerosToStrig();

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

            insertStmt.executeUpdateDelete();

            //Cerramos la base de datos
            db.close();
        }
        return true;
    }

    public void deleteAll(Context context){
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.execSQL("DELETE FROM Peliculas");
        db.close();
    }

    public void bulkInsert(Context context, List<AudiovisualInterface> items){
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        db.beginTransaction();
        try {
            String sql = "INSERT INTO Peliculas (filmId, nombre, anyo, titulo, tituloOriginal, descripcion, image, directores, generos, rating, tipo, temporadas, original_language, viewed) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStmt = db.compileStatement(sql);

            for (AudiovisualInterface pelicula : items) {
                insertStmt.clearBindings();
                insertStmt.bindString(1, Integer.toString(pelicula.getId()));
                insertStmt.bindString(2, pelicula.getTitulo());
                insertStmt.bindString(3, pelicula.getAnyo());
                insertStmt.bindString(4, pelicula.getTitulo());
                insertStmt.bindString(5, pelicula.getTituloOriginal());
                insertStmt.bindString(6, pelicula.getDescripcion());
                insertStmt.bindBlob(7, pelicula.getImage() == null ? new byte[0] : pelicula.getImage());
                insertStmt.bindString(8, pelicula.getDirectoresToString());
                insertStmt.bindString(9, pelicula.getGenerosToStrig());
                insertStmt.bindString(10, Double.toString(pelicula.getRating() == null ? 0.0 : pelicula.getRating()));
                insertStmt.bindString(11, pelicula.getTipo());
                insertStmt.bindString(12, pelicula.getSeasons() == null ? "0" : pelicula.getSeasons());
                insertStmt.bindString(13, pelicula.getOriginalLanguage() == null ? "" : pelicula.getOriginalLanguage());
                insertStmt.bindLong(14, pelicula.getViewed() ? 1 : 0);
                insertStmt.executeInsert();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean updateAsViewed(Context context, AudiovisualInterface pelicula){
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(context, "DBPeliculas", null, DatabaseVersion);

        SQLiteDatabase db = usdbh.getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            String id = Integer.toString(pelicula.getId());
            Integer viewed = pelicula.getViewed() ? 1 : 0;

            //Insertamos los datos en la tabla Peliculas
            String sql = "UPDATE Peliculas SET viewed = ? WHERE filmId = ?";
            SQLiteStatement insertStmt = db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindLong(1, viewed);
            insertStmt.bindString(2, id);

            insertStmt.executeUpdateDelete();

            //Cerramos la base de datos
            db.close();
        }
        return true;
    }
}
