package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonValue;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.Trailer;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 04/03/2017.
 */

public abstract class SearchMovieURLTrailer extends AsyncTask<String, Integer, List<Trailer>> implements ICallback {

    private HttpsURLConnection yc;
    Context context;
    AudiovisualInterface pelicula;
    List<Trailer> trailers;
    String language;

    public SearchMovieURLTrailer(Context context, String language, AudiovisualInterface pelicula){
        this.context = context;
        this.pelicula = pelicula;
        this.language = language;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        trailers = new ArrayList<Trailer>();
    }

    @Override
    protected List<Trailer> doInBackground(String... params) {
        try {
            getURLPelicula();
        } catch (IOException e) {

        }
        return trailers;

    }

    @Override
    protected void onPostExecute(List<Trailer> result)
    {
        super.onPostExecute(result);
        onResponseReceived(trailers);
    }


    private void getURLPelicula() throws IOException{
        String url = General.URLPRINCIPAL + "3/movie/" + pelicula.getId() + "/videos?api_key=" + General.APIKEY + "&language=" + language;

        leerJSONUrl(MyUtils.getHttpRequest(url));
    }

    private void leerJSONUrl(String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray results = info.get("results").asArray();
        if(results != null && !results.isNull()){
            if(results.size() > 0){
                for (JsonValue result : results){
                    Trailer trailer = new Gson().fromJson(result.toString(), Trailer.class);
                    if(trailer.getSite().equalsIgnoreCase("YouTube")){
                        trailers.add(trailer);
                    }
                }
            }
        }
    }

    public abstract void onResponseReceived(Object result);
}
