package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.TVShow;
import com.wyrnlab.jotdownthatmovie.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;

import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

/**
 * Created by Jota on 27/12/2017.
 */

public class SearchShow extends AsyncTask<String, Integer, List<TVShow>> {

    public AsyncResponse delegate = null;
    private List<TVShow> shows;
    private HttpsURLConnection yc;
    ProgressDialog pDialog;
    Context context;
    TVShow tvShow;

    public SearchShow(Context context){
        this.shows = new ArrayList<TVShow>();
        this.context = context;
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

    public List<TVShow> buscar(String nombre)  throws IOException {
        String web = null;

        @SuppressWarnings("deprecation")
        String url = General.URLPRINCIPAL + "3/search/tv?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage()) + "&query=" + URLEncoder.encode(nombre) ;
        URL oracle = new URL(url);
        yc = (HttpsURLConnection) oracle.openConnection();
        String json = "";

        //yc.setDoOutput(true);
        yc.setDoInput(true);
        yc.setInstanceFollowRedirects(false);
        yc.setRequestMethod("GET");
        //yc.setUseCaches (false);
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

        leerJSONBuscar(json);

        return this.shows;
    }

    private void leerJSONBuscar(String json) throws IOException{
        JsonObject respuestaTotal = JsonObject.readFrom( json );
        if(respuestaTotal.get("results") != null) {
            JsonArray res = respuestaTotal.get("results").asArray();
            for (int i = 0; i < res.size(); i++) {
                tvShow = new TVShow();
                JsonObject results = JsonObject.readFrom(res.get(i).toString());
                tvShow.setTituloOriginal(results.get("original_name").asString());
                tvShow.setTitulo(results.get("name").asString());
                tvShow.setId(results.get("id").asInt());
                if (!results.get("first_air_date").isNull()) {
                    // Recortar año
                    String an = results.get("first_air_date").asString();
                    String anyo;
                    if (an.length() > 0) {
                        anyo = an.substring(0, 4);
                    } else {
                        anyo = "N/D";
                    }
                    tvShow.setAnyo(anyo);
                } else {
                    tvShow.setAnyo("N/D");
                }
                if (results.get("poster_path").isString()) {
                    tvShow.setImagePath(results.get("poster_path").asString());
                } else {
                    getOtrosPosters();
                }
                tvShow.setRating(results.get("vote_average").asDouble());
                tvShow.setTipo("Show");
                this.shows.add(tvShow);
                tvShow.setSource(General.NET_SOURCE);
            }
        } else {
            this.shows = null;
        }
    }

    @Override
    protected List<TVShow> doInBackground(String... params) {
        String texto = params[0];
        List<TVShow> devolver = new ArrayList<TVShow>();
        try {
            devolver = buscar(texto);
        } catch (IOException e) {

        }
        return devolver;

    }

    @Override
    protected void onPostExecute(List<TVShow> result)
    {
        pDialog.dismiss();
        super.onPostExecute(result);
        delegate.processFinish(result);
    }



    private void getOtrosPosters() throws IOException{
        String web = null;

        String url = General.URLPRINCIPAL + "/3/tv/" + tvShow.getId() + "/images?api_key=" + General.APIKEY;

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

        leerJSONOtrosPosters(json);
    }

    private void leerJSONOtrosPosters(String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray aux = info.get("posters").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            tvShow.setImagePath(poster.get("file_path").asString());
        }
    }
}
