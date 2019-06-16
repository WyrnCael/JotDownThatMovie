package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion;

import android.content.Context;
import android.os.AsyncTask;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 27/12/2017.
 */

public class SearchBaseUrl extends AsyncTask<String, Integer, List<Pelicula>> {

    private HttpsURLConnection yc;
    Context context;

    public SearchBaseUrl(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Pelicula> doInBackground(String... params) {
        getBaseUrl();
        return null;

    }

    @Override
    protected void onPostExecute(List<Pelicula> result)
    {
        super.onPostExecute(result);
    }

    private void getBaseUrl() {
        String web = null;

        String url = General.URLPRINCIPAL + "3/configuration?api_key=" + General.APIKEY;
        try {
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

            JsonObject respuestaTotal = JsonObject.readFrom( json );
            JsonObject images = respuestaTotal.get("images").asObject();
            General.base_url = images.get("base_url").asString();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
