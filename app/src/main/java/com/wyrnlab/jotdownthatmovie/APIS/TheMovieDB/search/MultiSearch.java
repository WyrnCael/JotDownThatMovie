package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.RuntimeTypeAdapterFactory;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelMultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelSearchMultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelPerson;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelSearchMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelShow;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Model.Person;
import com.wyrnlab.jotdownthatmovie.Model.TVShow;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import org.json.JSONStringer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 27/12/2017.
 */

public class MultiSearch extends AsyncTask<String, Integer, List<AudiovisualInterface>> {

    public AsyncResponse delegate = null;
    private List<AudiovisualInterface> results;
    private HttpsURLConnection yc;
    ProgressDialog pDialog;
    Context context;
    Integer page;

    public MultiSearch(Context context, Integer page){
        this.results = new ArrayList<AudiovisualInterface>();
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
    protected List<AudiovisualInterface> doInBackground(String... params) {
        String texto = params[0];
        List<AudiovisualInterface> devolver = new ArrayList<AudiovisualInterface>();
        try {
            devolver = buscar(texto);
        } catch (IOException e) {

        }
        return devolver;

    }

    @Override
    protected void onPostExecute(List<AudiovisualInterface> result)
    {
        pDialog.dismiss();
        super.onPostExecute(result);
        delegate.processFinish(result);
    }

    public List<AudiovisualInterface> buscar(String nombre)  throws IOException {
        String url = General.URLPRINCIPAL + "3/search/multi?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage() + "&query=" + URLEncoder.encode(nombre);
        url += this.page == null ? "" : "&page=" + this.page;

        leerJSONBuscar(MyUtils.getHttpRequest(url));

        return this.results;
    }

    private void leerJSONBuscar(String responseJson) throws IOException{


        // adding all different container classes with their flag
                final RuntimeTypeAdapterFactory<ModelMultiSearch> typeFactory = RuntimeTypeAdapterFactory
                        .of(ModelMultiSearch.class, "media_type") // Here you specify which is the parent class and what field particularizes the child class.
                        .registerSubtype(ModelMovie.class, "movie") // if the flag equals the class name, you can skip the second parameter. This is only necessary, when the "type" field does not equal the class name.
                        .registerSubtype(ModelShow.class, "tv") // if the flag equals the class name, you can skip the second parameter. This is only necessary, when the "type" field does not equal the class name.
                        .registerSubtype(ModelPerson.class, "person");

        // add the polymorphic specialization
                final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();

        // do the mapping
                final ModelSearchMultiSearch deserializedRequestList = gson.fromJson(responseJson, ModelSearchMultiSearch.class);
        final Gson gson2 = new GsonBuilder().create();

                for(ModelMultiSearch model : deserializedRequestList.results){
                    if(model.media_type.equals("movie")) {
                        Pelicula pelicula = new Pelicula();
                        pelicula.setDataFromJson((ModelMovie)model);
                        this.results.add(pelicula);
                    } else if(model.media_type.equals("tv")) {
                        TVShow show = new TVShow();
                        show.setDataFromJson((ModelShow) model);
                        this.results.add(show);
                    } else if(model.media_type.equals("person")) {
                        Person person = new Person();
                        person.setDataFromJson((ModelPerson)model, context);


                        Gson gson3 = new Gson();


                        this.results.add(person);
                    }
                }



    }

    private void getOtrosPosters(AudiovisualInterface movie) throws IOException{
        String url = General.URLPRINCIPAL + "/3/movie/" + movie.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(movie, MyUtils.getHttpRequest(url));
    }

    private void leerJSONOtrosPosters(AudiovisualInterface movie, String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        JsonArray aux = info.get("posters").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            movie.setImagePath(poster.get("file_path").asString());
        }
    }
}
