package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelSearchTVShow;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelShow;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCredits;
import com.wyrnlab.jotdownthatmovie.Model.TVShow;
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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 27/12/2017.
 */

public class SearchInfoShow extends AsyncTask<String, Integer, TVShow> implements ICallback {

    public AsyncResponse delegate = null;
    public int position;
    private HttpsURLConnection yc;
    Context context;
    ProgressDialog pDialog;
    int Id;
    TVShow tvShow;
    String text;

    public SearchInfoShow(Context context, int id, String text){
        this.context = context;
        this.Id = id;
        this.text = text;
        tvShow = new TVShow();
        tvShow.setId(id);
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
    protected TVShow doInBackground(String... params) {
        try {
            getSinopsisTVShow();
            getCreditsTVShow();
            getImage();
            getSimilars();
        } catch (IOException e) {

        }
        return this.tvShow;

    }

    @Override
    protected void onPostExecute(TVShow result)
    {
        super.onPostExecute(result);
        pDialog.dismiss();
        if(delegate != null) {
            delegate.processFinish(result);
        }
        onResponseReceived(result);
    }



    private void getSinopsisTVShow() throws IOException{
        String url = General.URLPRINCIPAL + "3/tv/" + this.Id + "?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        leerJSONSinopsis(MyUtils.getHttpRequest(url));
    }

    private void leerJSONSinopsis(String json) throws IOException{
        ModelShow movie = new Gson().fromJson(json, ModelShow.class);
        tvShow.setDataFromJson(movie);
        if(tvShow.getImagePath() == null) {
            getOtrosPosters(tvShow);
        }
    }

    private void getCreditsTVShow() throws IOException{
        String url = General.URLPRINCIPAL + "3/tv/" + this.Id + "/credits?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        leerJSONCredits(MyUtils.getHttpRequest(url));
    }

    private void leerJSONCredits(String json) throws IOException{
        ModelCredits results = new Gson().fromJson(json, ModelCredits.class);

        for (ModelCredits.ModelCrew model : results.crew){
            if (model.job.equalsIgnoreCase("Director")){
                tvShow.addDirectores(model.name);
            }
        }
    }

    private void getImage(){
        // Convertiomos la imagen a BLOB
        byte[] image = null;
        try {
            if(General.base_url == null){
                SearchBaseUrl.getBaseUrl();
            }

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
            Bitmap b = SetTheLanguages.getImageStub(context);
            tvShow.setImage(ImageHandler.getBytes(b));
        }
    }

    private void getOtrosPosters(AudiovisualInterface show) throws IOException{
        String url = General.URLPRINCIPAL + "/3/tv/" + show.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(show, MyUtils.getHttpRequest(url));
    }

    private void leerJSONOtrosPosters(AudiovisualInterface show, String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray aux = info.get("posters").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            show.setImagePath(poster.get("file_path").asString());
        }
    }

    private void getSimilars() throws IOException{
        String url = General.URLPRINCIPAL + "3/tv/" + this.tvShow.getId() + "/similar?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        readJSONSimilars(MyUtils.getHttpRequest(url));
    }

    private void readJSONSimilars(String json) throws IOException{
        ModelSearchTVShow results = new Gson().fromJson(json, ModelSearchTVShow.class);

        List<AudiovisualInterface> similars = new ArrayList<AudiovisualInterface>();

        if(results.results.length > 0) {
            for (ModelShow model : results.results) {
                TVShow show = new TVShow();
                show.setDataFromJson(model);
                if(show.getImagePath() == null) {
                    getOtrosPosters(show);
                }
                similars.add(show);
            }
        }

        this.tvShow.setSimilars(similars);
    }

    @Override
    public void onResponseReceived(Object result){

    }
}
