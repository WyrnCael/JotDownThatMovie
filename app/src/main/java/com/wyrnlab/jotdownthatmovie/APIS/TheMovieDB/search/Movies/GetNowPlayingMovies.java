package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelSearchMovie;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetNowPlayingMovies extends AsyncTask<Void, Integer, List<Pelicula>> {

    public AsyncResponse delegate = null;
    private List<Pelicula> peliculas;
    ProgressDialog pDialog;
    Context context;
    Pelicula pelicula;
    Integer page;

    public GetNowPlayingMovies(Context context, Integer page){
        this.peliculas = new ArrayList<Pelicula>();
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
    protected List<Pelicula> doInBackground(Void... params) {
        List<Pelicula> devolver = new ArrayList<Pelicula>();
        try {
            devolver = buscar();
        } catch (IOException e) {

        }
        return devolver;
    }

    @Override
    protected void onPostExecute(List<Pelicula> result)
    {
        pDialog.dismiss();
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

    public List<Pelicula> buscar() throws IOException {
        String region = Locale.getDefault().getCountry();
        if(region == null || region.isEmpty()) {
            region = "US";
        }

        String url = General.URLPRINCIPAL + "3/movie/now_playing?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage() + "&region=" + region;
        url += this.page == null ? "" : "&page=" + this.page;

        leerJSONBuscar(MyUtils.getHttpRequest(url));

        return this.peliculas;
    }

    private void leerJSONBuscar(String json) throws IOException{
        ModelSearchMovie results = new Gson().fromJson(json, ModelSearchMovie.class);

        String minDate = results.dates != null ? results.dates.minimum : null;
        String maxDate = results.dates != null ? results.dates.maximum : null;

        if(results.results.length > 0) {
            for (ModelMovie model : results.results) {
                if(model.release_date == null || model.release_date.isEmpty()) {
                    continue;
                }
                if(minDate != null && model.release_date.compareTo(minDate) < 0) {
                    continue;
                }
                if(maxDate != null && model.release_date.compareTo(maxDate) > 0) {
                    continue;
                }

                pelicula = new Pelicula();
                pelicula.setDataFromJson(model);
                if(pelicula.getImagePath() == null) {
                    getOtrosPosters(pelicula);
                }
                this.peliculas.add(pelicula);
            }
        }
    }

    private void getOtrosPosters(AudiovisualInterface movie) throws IOException{
        String url = General.URLPRINCIPAL + "/3/movie/" + movie.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(movie, MyUtils.getHttpRequest(url));
    }

    private void leerJSONOtrosPosters(AudiovisualInterface movie, String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        if(info.get("posters") == null) {
            return;
        }
        JsonArray aux = info.get("posters").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            movie.setImagePath(poster.get("file_path").asString());
        }
    }
}
