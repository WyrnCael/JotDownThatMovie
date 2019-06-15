package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.TVShow;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

/**
 * Created by Jota on 27/12/2017.
 */

public class SearchInfoShow extends AsyncTask<String, Integer, TVShow> {

    public AsyncResponse delegate = null;
    public int position;
    private HttpsURLConnection yc;
    Context context;
    ProgressDialog pDialog;
    int Id;
    TVShow tvShow;

    public SearchInfoShow(Context context, int id){
        this.context = context;
        this.Id = id;
        tvShow = new TVShow();
        tvShow.setId(id);
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
    protected TVShow doInBackground(String... params) {
        try {
            getSinopsisTVShow();
            getCreditsTVShow();
            getImage();
        } catch (IOException e) {

        }
        return this.tvShow;

    }

    @Override
    protected void onPostExecute(TVShow result)
    {
        super.onPostExecute(result);
        pDialog.dismiss();
        delegate.processFinish(result, position);
    }



    private void getSinopsisTVShow() throws IOException{
        String web = null;

        String url = General.URLPRINCIPAL + "3/tv/" + this.Id + "?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

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

        leerJSONSinopsis(json);
    }

    private void leerJSONSinopsis(String json) throws IOException{
        Log.d("JSON" , json);
        JsonObject info = JsonObject.readFrom( json );
        tvShow.setTituloOriginal(info.get("original_name").asString());
        tvShow.setTitulo(info.get("name").asString());
        tvShow.setId(info.get("id").asInt());
        if (!info.get("first_air_date").isNull()) {
            // Recortar año
            String an = info.get("first_air_date").asString();
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
        if (info.get("poster_path").isString()) {
            tvShow.setImagePath(info.get("poster_path").asString());
        } else {
            getOtrosPosters();
        }
        tvShow.setRating(info.get("vote_average").asDouble());
        if(info.get("overview").isNull()){
            tvShow.setDescripcion("");
        }
        else{
            tvShow.setDescripcion(info.get("overview").asString());
        }

        JsonArray aux = info.get("genres").asArray();
        for (int i = 0; i < aux.size() ; i++){
            JsonObject genero = aux.get(i).asObject();
            tvShow.addGeneros(genero.get("name").asString());
        }
        tvShow.setSeasons(String.valueOf(info.get("number_of_seasons").asInt()));
        tvShow.setTipo("Show");
        tvShow.setSource(General.NET_SOURCE);
    }

    private void getCreditsTVShow() throws IOException{
        String web = null;

        String url = General.URLPRINCIPAL + "3/tv/" + this.Id + "/credits?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());

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

        leerJSONCredits(json);
    }

    private void leerJSONCredits(String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray aux = info.get("crew").asArray();
        String[] directores = new String[aux.size()];
        for (int i = 0; i < aux.size() ; i++){
            JsonObject person = aux.get(i).asObject();
            try{
                if (person.get("job").asString().equalsIgnoreCase("Director")){
                    tvShow.addDirectores(person.get("name").asString());
                }
            } catch (NullPointerException e){

            }
        }
    }

    private void getImage(){
        // Convertiomos la imagen a BLOB
        byte[] image = null;
        try {
            URL url = new URL(General.base_url + "w500" + tvShow.getImagePath());
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

            tvShow.setImage(buffer.toByteArray());
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
            Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.stub);
            tvShow.setImage(ImageHandler.getBytes(b));
        }
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
