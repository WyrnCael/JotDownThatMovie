package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies;

import android.content.Context;
import android.os.AsyncTask;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.IOException;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 04/03/2017.
 */

public abstract class SearchMovieURLTrailer extends AsyncTask<String, Integer, String> implements ICallback {

    private HttpsURLConnection yc;
    Context context;
    AudiovisualInterface pelicula;
    String trailerId = null;

    public SearchMovieURLTrailer(Context context, AudiovisualInterface pelicula){
        this.context = context;
        this.pelicula = pelicula;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            getURLPelicula();
        } catch (IOException e) {

        }
        return trailerId;

    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        onResponseReceived(trailerId);
    }


    private void getURLPelicula() throws IOException{
        String url = General.URLPRINCIPAL + "3/movie/" + pelicula.getId() + "/videos?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

        leerJSONUrl(MyUtils.getHttpRequest(url));
    }

    private void leerJSONUrl(String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray results = info.get("results").asArray();
        if(results != null && !results.isNull()){
            if(results.size() > 0){
                for (int i = 0; i < 1 ; i++){
                    JsonObject video = results.get(i).asObject();
                    trailerId = video.get("key").asString();
                }
            }
        }
    }

    public abstract void onResponseReceived(Object result);
}
