package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCredits;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelSearchMovie;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 27/12/2017.
 */

public class SearchInfoMovie extends AsyncTask<String, Integer, Pelicula> implements ICallback {

    public AsyncResponse delegate = null;
    public int position;
    private HttpsURLConnection yc;
    Context context;
    ProgressDialog pDialog;
    int Id;
    Pelicula pelicula;
    String text;

    public SearchInfoMovie(Context context, int id, String text){
        this.context = context;
        this.Id = id;
        this.text = text;
        pelicula = new Pelicula();
        pelicula.setId(id);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(text);
        pDialog.setCancelable(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    @Override
    protected Pelicula doInBackground(String... params) {
        try {
            getSinopsisPelicula();
            getCreditsPelicula();
            getImage();
            getSimilars();
        } catch (IOException e) {

        }
        return this.pelicula;

    }

    @Override
    protected void onPostExecute(Pelicula result)
    {
        super.onPostExecute(result);
        pDialog.dismiss();
        if(delegate != null){
            delegate.processFinish(result);
        }
        onResponseReceived(result);
    }



    private void getSinopsisPelicula() throws IOException{
        String url = General.URLPRINCIPAL + "3/movie/" + this.Id + "?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

        leerJSONSinopsis(MyUtils.getHttpRequest(url));
    }

    private void leerJSONSinopsis(String json) throws IOException{

        ModelMovie movie = new Gson().fromJson(json, ModelMovie.class);
        pelicula.setDataFromJson(movie);
        if(pelicula.getImagePath() == null) {
            getOtrosPosters(pelicula);
        }
    }

    private void getCreditsPelicula() throws IOException{
        String url = General.URLPRINCIPAL + "3/movie/" + this.Id + "/credits?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

        leerJSONCredits(MyUtils.getHttpRequest(url));
    }

    private void leerJSONCredits(String json) throws IOException{
        ModelCredits results = new Gson().fromJson(json, ModelCredits.class);

        for (ModelCredits.ModelCrew model : results.crew){
            if (model.job.equalsIgnoreCase("Director")){
                    pelicula.addDirectores(model.name);
            }
        }
    }

    private void getImage(){
        // Convertiomos la imagen a BLOB
        byte[] image = null;
        try {
            URL url = new URL(General.base_url + "w500" + pelicula.getImagePath());
            URLConnection ucon = url.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            //We create an array of bytes
            byte[] data = new byte[50];
            int current = 0;

            while((current = bis.read(data,0,data.length)) != -1){
                buffer.write(data,0,current);
            }

            pelicula.setImage(buffer.toByteArray());
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
            Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
            pelicula.setImage(ImageHandler.getBytes(b));
        }
    }

    private void getOtrosPosters(AudiovisualInterface movie) throws IOException{
        String url = General.URLPRINCIPAL + "/3/movie/" + movie.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(movie, MyUtils.getHttpRequest(url));
    }

    private void leerJSONOtrosPosters(AudiovisualInterface movie, String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray aux = info.get("posters").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            movie.setImagePath(poster.get("file_path").asString());
        }
    }

    private void getSimilars() throws IOException{
        String url = General.URLPRINCIPAL + "3/movie/" + this.pelicula.getId() + "/similar?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

        readJSONSimilars(MyUtils.getHttpRequest(url));
    }

    private void readJSONSimilars(String json) throws IOException{
        ModelSearchMovie results = new Gson().fromJson(json, ModelSearchMovie.class);

        List<AudiovisualInterface> similars = new ArrayList<AudiovisualInterface>();

        if(results.results.length > 0) {
            for (ModelMovie model : results.results) {
                Pelicula movie = new Pelicula();
                movie.setDataFromJson(model);
                if(movie.getImagePath() == null) {
                    getOtrosPosters(movie);
                }
                similars.add(movie);
            }
        }

        this.pelicula.setSimilars(similars);
    }

    @Override
    public void onResponseReceived(Object result) {

    }
}
