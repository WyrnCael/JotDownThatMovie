package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
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

public class Search extends AsyncTask<String, Integer, List<Pelicula>> {

    public AsyncResponse delegate = null;
    private List<Pelicula> peliculas;
    private HttpsURLConnection yc;
    ProgressDialog pDialog;
    Context context;
    Pelicula pelicula;

    public Search(Context context){
        this.peliculas = new ArrayList<Pelicula>();
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

    public List<Pelicula> buscar(String nombre)  throws IOException {
        String web = null;

        @SuppressWarnings("deprecation")
        String url = General.URLPRINCIPAL + "3/search/movie?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage()) + "&query=" + URLEncoder.encode(nombre) ;
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

        return this.peliculas;
    }

    private void leerJSONBuscar(String json) throws IOException{
        JsonObject respuestaTotal = JsonObject.readFrom( json );
        if(respuestaTotal.get("results") != null) {
            JsonArray res = respuestaTotal.get("results").asArray();
            for (int i = 0; i < res.size(); i++) {
                pelicula = new Pelicula();
                JsonObject results = JsonObject.readFrom(res.get(i).toString());
                pelicula.setTituloOriginal(results.get("original_title").asString());
                pelicula.setTitulo(results.get("title").asString());
                pelicula.setId(results.get("id").asInt());
                if (!results.get("release_date").isNull()) {
                    // Recortar año
                    String an = results.get("release_date").asString();
                    String anyo;
                    if (an.length() > 0) {
                        anyo = an.substring(0, 4);
                    } else {
                        anyo = "N/D";
                    }
                    pelicula.setAnyo(anyo);
                } else {
                    pelicula.setAnyo("N/D");
                }
                if (results.get("poster_path").isString()) {
                    pelicula.setImagePath(results.get("poster_path").asString());
                } else {
                    getOtrosPosters();
                }
                pelicula.setRating(results.get("vote_average").asDouble());
                pelicula.setTipo("Movie");
                pelicula.setSource(General.NET_SOURCE);
                this.peliculas.add(pelicula);
            }
        } else {
            this.peliculas = null;
        }
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
        super.onPostExecute(result);
        delegate.processFinish(result, 0);
    }



    private void getOtrosPosters() throws IOException{
        String web = null;

        String url = General.URLPRINCIPAL + "/3/movie/" + pelicula.getId() + "/images?api_key=" + General.APIKEY;

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
            pelicula.setImagePath(poster.get("file_path").asString());
        }
    }
}
