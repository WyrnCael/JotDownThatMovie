package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelSearchTVShow;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelShow;
import com.wyrnlab.jotdownthatmovie.Model.TVShow;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

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
    Integer page;

    public SearchShow(Context context, Integer page){
        this.shows = new ArrayList<TVShow>();
        this.context = context;
        this.page = page;
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

    public List<TVShow> buscar(String nombre)  throws IOException {
        String url = General.URLPRINCIPAL + "3/search/tv?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage(), Locale.getDefault().getCountry()) + "&query=" + URLEncoder.encode(nombre) ;
        url += this.page == null ? "" : "&page=" + this.page;

        leerJSONBuscar(MyUtils.getHttpRequest(url));

        return this.shows;
    }

    private void leerJSONBuscar(String json) throws IOException{

        ModelSearchTVShow results = new Gson().fromJson(json, ModelSearchTVShow.class);

        if(results.results.length > 0) {
            for (ModelShow model : results.results) {
                tvShow = new TVShow();
                tvShow.setDataFromJson(model);
                if(tvShow.getImagePath() == null) {
                    getOtrosPosters();
                }
                this.shows.add(tvShow);
            }
        }
    }

    private void getOtrosPosters() throws IOException{
        String url = General.URLPRINCIPAL + "/3/tv/" + tvShow.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(MyUtils.getHttpRequest(url));
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
