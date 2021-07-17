package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelSearchMovie;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 27/12/2017.
 */

public abstract class GetSimilarMovies extends AsyncTask<String, Integer, List<Pelicula>> implements ICallback {

    public AsyncResponse delegate = null;
    private List<Pelicula> peliculas;
    private HttpsURLConnection yc;
    ProgressDialog pDialog;
    Context context;
    Pelicula pelicula;
    Integer page;
    Integer id;

    public GetSimilarMovies(Context context, Integer id, Integer page){
        this.peliculas = new ArrayList<Pelicula>();
        this.context = context;
        this.page = page;
        this.id = id;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.searching));
        pDialog.setCancelable(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();
    }

    @Override
    protected List<Pelicula> doInBackground(String... params) {
        String texto = params[0];
        List<Pelicula> devolver = new ArrayList<Pelicula>();
        try {
            devolver = buscar(texto);
        } catch (IOException e) {

        }
        return devolver;

    }

    @Override
    protected void onPostExecute(List<Pelicula> result)
    {
        pDialog.dismiss();
        onResponseReceived(result);
        super.onPostExecute(result);
        if(delegate != null){
            delegate.processFinish(result);
        }
    }

    public List<Pelicula> buscar(String nombre)  throws IOException {
        String url = General.URLPRINCIPAL + "3/movie/" + this.id + "/similar?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();
        url += this.page == null ? "" : "&page=" + this.page;

        leerJSONBuscar(MyUtils.getHttpRequest(url));

        return this.peliculas;
    }

    private void leerJSONBuscar(String json) throws IOException{
        ModelSearchMovie results = new Gson().fromJson(json, ModelSearchMovie.class);

        if(results.results.length > 0) {
            for (ModelMovie model : results.results) {
                pelicula = new Pelicula();
                pelicula.setDataFromJson(model);
                if(pelicula.getImagePath() == null) {
                    getOtrosPosters();
                }
                this.peliculas.add(pelicula);
            }
        }
    }

    private void getOtrosPosters() throws IOException{
        String url = General.URLPRINCIPAL + "/3/movie/" + pelicula.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(MyUtils.getHttpRequest(url));
    }

    private void leerJSONOtrosPosters(String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray aux = info.get("posters").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            pelicula.setImagePath(poster.get("file_path").asString());
        }
    }

    public abstract void onResponseReceived(Object result);
}
