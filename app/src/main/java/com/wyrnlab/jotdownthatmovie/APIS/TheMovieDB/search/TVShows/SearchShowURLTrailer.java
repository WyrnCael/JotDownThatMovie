package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows;

import android.content.Context;
import android.os.AsyncTask;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Utils.ICallback;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 04/03/2017.
 */

public abstract class SearchShowURLTrailer extends AsyncTask<String, Integer, String> implements ICallback {

    private HttpsURLConnection yc;
    Context context;
    AudiovisualInterface pelicula;
    String trailerId = null;

    public SearchShowURLTrailer(Context context, AudiovisualInterface pelicula){
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
        String web = null;

        String url = General.URLPRINCIPAL + "3/tv/" + pelicula.getId() + "/videos?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

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
        if(!results.isNull()){
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
