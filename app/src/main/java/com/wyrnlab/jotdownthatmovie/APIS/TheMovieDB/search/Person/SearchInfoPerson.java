package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Person;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;
import com.wyrnlab.jotdownthatmovie.Model.AudioVisualInterfaceVoreAverageComparator;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.Cast;
import com.wyrnlab.jotdownthatmovie.Model.CastShow;
import com.wyrnlab.jotdownthatmovie.Model.Crew;
import com.wyrnlab.jotdownthatmovie.Model.AudioVisualInterfacePopularityComparator;
import com.wyrnlab.jotdownthatmovie.Model.CrewShow;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCast;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCredits;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelCrew;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelPerson;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.Movies.ModelSearchMovie;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelCastShow;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelCrewShow;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.TVShows.ModelPersonShow;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Model.Person;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jota on 27/12/2017.
 */

public class SearchInfoPerson extends AsyncTask<String, Integer, Person> implements ICallback {

    public AsyncResponse delegate = null;
    public int position;
    private HttpsURLConnection yc;
    Context context;
    ProgressDialog pDialog;
    int Id;
    Person person;
    String text;

    public SearchInfoPerson(Context context, int id, String text){
        this.context = context;
        this.Id = id;
        this.text = text;
        person = new Person();
        person.setId(id);
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
    protected Person doInBackground(String... params) {
        try {
            getPersonInfo();
            getMovies();
            getShows();
            sortCrewAndCast();
            getImage();
        } catch (IOException e) {

        }
        return this.person;

    }

    @Override
    protected void onPostExecute(Person result)
    {
        super.onPostExecute(result);
        pDialog.dismiss();
        if(delegate != null){
            delegate.processFinish(result);
        }
        onResponseReceived(result);
    }



    private void getPersonInfo() throws IOException{
        String url = General.URLPRINCIPAL + "3/person/" + this.Id + "?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        readPersonInfo(MyUtils.getHttpRequest(url));
    }

    private void readPersonInfo(String json) throws IOException{
        ModelPerson personModel = new Gson().fromJson(json, ModelPerson.class);
        person.setDataFromJson(personModel, context);
    }

    private void getCreditsPelicula() throws IOException{
        String url = General.URLPRINCIPAL + "3/person/" + this.Id + "/credits?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        leerJSONCredits(MyUtils.getHttpRequest(url));
    }

    private void leerJSONCredits(String json) throws IOException{
        ModelCredits results = new Gson().fromJson(json, ModelCredits.class);

        for (ModelCredits.ModelCrew model : results.crew){
            if (model.job.equalsIgnoreCase("Director")){
                person.addDirectores(model.name);
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
            URL url = new URL(General.base_url + "w500" + person.getImagePath());
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

            person.setImage(buffer.toByteArray());
        } catch (Exception e) {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(),
                    SetTheLanguages.getPersonImageStubResourceId());
            person.setImage(ImageHandler.getBytes(b));
        }
    }

    private void getOtrosPosters(AudiovisualInterface person) throws IOException{
        String url = General.URLPRINCIPAL + "/3/person/" + person.getId() + "/images?api_key=" + General.APIKEY;

        leerJSONOtrosPosters(person, MyUtils.getHttpRequest(url));
    }

    private void leerJSONOtrosPosters(AudiovisualInterface movie, String json) throws IOException{
        JsonObject info = JsonObject.readFrom( json );
        /*JsonArray aux = info.get("profiles").asArray();
        if(aux.size() > 0){
            JsonObject poster = aux.get(0).asObject();
            movie.setImagePath(poster.get("file_path").asString());
        }*/
    }

    private void getMovies() throws IOException{
        String url = General.URLPRINCIPAL + "3/person/" + this.person.getId() + "/movie_credits?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        readJSONMovies(MyUtils.getHttpRequest(url));
    }

    private void readJSONMovies(String json) throws IOException{
        ModelPerson results = new Gson().fromJson(json, ModelPerson.class);
        Map<Integer, AudiovisualInterface> moviesById = new HashMap<Integer, AudiovisualInterface>();
        List<AudiovisualInterface> moviesCast = new ArrayList<AudiovisualInterface>();

        if(results.crew.length > 0) {
            for (ModelCrew model : results.crew) {
                Crew movie = new Crew();
                movie.setDataFromJson(model);
                if(movie.getImagePath() == null) {
                    getOtrosPosters(movie);
                }

                if(moviesById.containsKey(movie.getId())) {
                    movie.setJob(moviesById.get(movie.getId()).getJob() + ", " + SetTheLanguages.getJobTranslation(context, movie.getJob()));
                } else{
                    movie.setJob(SetTheLanguages.getJobTranslation(context, movie.getJob()));
                }
                movie.setTipo(General.MOVIE_TYPE);

                moviesById.put(movie.getId(), movie);
            }
            this.person.addCrew(new ArrayList<AudiovisualInterface>(moviesById.values()));
        }

        if(results.cast.length > 0) {
            for (ModelCast model : results.cast) {
                Cast movie = new Cast();
                movie.setDataFromJson(model);
                if(movie.getImagePath() == null) {
                    getOtrosPosters(movie);
                }
                movie.setTipo(General.MOVIE_TYPE);

                moviesCast.add(movie);
            }
            this.person.addCast(new ArrayList<AudiovisualInterface>(moviesCast));
        }

    }

    private void getShows() throws IOException{
        String url = General.URLPRINCIPAL + "3/person/" + this.person.getId() + "/tv_credits?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage();

        readJSONShows(MyUtils.getHttpRequest(url));
    }

    private void readJSONShows(String json) throws IOException{
        ModelPersonShow results = new Gson().fromJson(json, ModelPersonShow.class);
        Map<Integer, AudiovisualInterface> moviesById = new HashMap<Integer, AudiovisualInterface>();
        List<AudiovisualInterface> moviesCast = new ArrayList<AudiovisualInterface>();

        if(results.crew.length > 0) {
            for (ModelCrewShow model : results.crew) {
                CrewShow movie = new CrewShow();
                movie.setDataFromJson(model);
                if(movie.getImagePath() == null) {
                    getOtrosPosters(movie);
                }

                if(moviesById.containsKey(movie.getId())) {
                    movie.setJob(moviesById.get(movie.getId()).getJob() + ", " + SetTheLanguages.getJobTranslation(context, movie.getJob()));
                } else{
                    movie.setJob(SetTheLanguages.getJobTranslation(context, movie.getJob()));
                }

                movie.setTipo(General.TVSHOW_TYPE);

                moviesById.put(movie.getId(), movie);
            }
            this.person.addCrew(new ArrayList<AudiovisualInterface>(moviesById.values()));


        }

        if(results.cast.length > 0) {
            for (ModelCastShow model : results.cast) {
                CastShow movie = new CastShow();
                movie.setDataFromJson(model);
                if(movie.getImagePath() == null) {
                    getOtrosPosters(movie);
                }

                moviesCast.add(movie);
                movie.setTipo(General.TVSHOW_TYPE);
            }
            this.person.addCast(new ArrayList<AudiovisualInterface>(moviesCast));


        }

    }

    private void sortCrewAndCast(){
        Collections.sort(this.person.getCrew(), new AudioVisualInterfaceVoreAverageComparator());
        Collections.sort(this.person.getCast(), new AudioVisualInterfaceVoreAverageComparator());
    }

    @Override
    public void onResponseReceived(Object result) {

    }
}
