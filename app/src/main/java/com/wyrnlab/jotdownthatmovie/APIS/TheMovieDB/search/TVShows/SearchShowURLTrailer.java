package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonValue;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Trailer;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 04/03/2017.
 */

public abstract class SearchShowURLTrailer extends AsyncTask<String, Integer, List<Trailer>> implements ICallback {

    private HttpsURLConnection yc;
    Context context;
    AudiovisualInterface pelicula;
    List<Trailer> trailers;
    String trailerId = null;
    String language;

    public SearchShowURLTrailer(Context context, String language, AudiovisualInterface pelicula){
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
        super.onPostExecute(trailers);
        onResponseReceived(trailers);
    }


    private void getURLPelicula() throws IOException{
        String web = null;

        String url = General.URLPRINCIPAL + "3/tv/" + pelicula.getId() + "/videos?api_key=" + General.APIKEY + "&language=" + language;

        URL oracle = new URL(url);
        yc = (HttpsURLConnection) oracle.openConnection();
        String json = "";

        //yc.setDoOutput(true);
        yc.setDoInput(true);
        yc.setInstanceFollowRedirects(false);
        yc.setRequestMethod("GET");
        //yc.setUseCaches (true);
        yc.setRequestProperty("Accept", "application/json");

        yc.connect();

        InputStream is = null;
        try {
            is = yc.getInputStream();
        } catch (IOException ioe) {
            if (yc instanceof HttpsURLConnection) {
                HttpsURLConnection httpConn = (HttpsURLConnection) yc;
                int statusCode = httpConn.getResponseCode();
                if (statusCode != 200) {
                    is = httpConn.getErrorStream();
                }
            }
        }

        InputStreamReader isReader = new InputStreamReader(is);
        //put output stream into a string
        BufferedReader br = new BufferedReader(isReader );
        String inputLine;
        while ((inputLine = br.readLine()) != null)
            web += inputLine;
        br.close();
        yc.disconnect();

        yc.disconnect();

        json = web.substring(4);

        leerJSONUrl(json);
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
