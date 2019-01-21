package com.wyrnlab.jotdownthatmovie.ShowInfo.mostrarPelicula;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.MainActivity;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.search.CheckInternetConection;
import api.search.Movies.SearchMovieURLTrailer;
import com.wyrnlab.jotdownthatmovie.video.YoutubeApi.YoutubeActivityView;

import java.util.concurrent.ExecutionException;

import api.conexion.SearchBaseUrl;
import api.search.AsyncResponse;
import api.search.AudiovisualInterface;
import api.search.Movies.SearchInfoMovie;
import data.General;

/**
 * Created by Jota on 27/12/2017.
 */

public class InfoMovieShared extends AppCompatActivity implements AsyncResponse {

    ProgressDialog pDialog;
    AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView descripcion;
    TextView genero;
    TextView director;
    TextView generoLab;
    TextView directorLab;
    Button botonAnadir;
    Button botonVolver;
    Button botonTrailer;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!CheckInternetConection.isConnectingToInternet(InfoMovieShared.this)){
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.not_internet),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            if(General.base_url == null){
                SearchBaseUrl searchor = new SearchBaseUrl(this);
                searchor.execute();
            }

        }

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri url = intent.getData();

        String link = url.toString();

        int id;
        if(link.indexOf("-") == -1)
            id = Integer.valueOf(link.substring(link.indexOf("movie/") + 6, link.length()));
        else
            id = Integer.valueOf(link.substring(link.indexOf("movie/") + 6, link.indexOf("-")));

        SearchInfoMovie searchorMovie = new SearchInfoMovie(this, id);
        searchorMovie.delegate = this;
        searchorMovie.execute();

        setContentView(R.layout.movie_info);

        //Obtenemos una referencia a los controles de la interfaz
        anyo = (TextView)findViewById(R.id.Anyo);
        genero = (TextView)findViewById(R.id.genero);
        director = (TextView)findViewById(R.id.director);
        valoracion = (TextView)findViewById(R.id.valoracion);
        descripcion = (TextView)findViewById(R.id.toda_la_descripcion);
        generoLab = (TextView)findViewById(R.id.generoLab);
        directorLab = (TextView)findViewById(R.id.directorLAb);
        botonAnadir = (Button)findViewById(R.id.BtnAnadir);
        botonVolver = (Button)findViewById(R.id.BtnAtras);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);

        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

        //Implementamos el evento click del botón
        botonAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DAO.getInstance().insert(InfoMovieShared.this, pelicula)){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.film) + " \"" + pelicula.getTitulo() + "\" " + getResources().getString(R.string.added) + "!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    Intent intent = new Intent(InfoMovieShared.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.film) + " \"" + pelicula.getTitulo() + "\" " + getResources().getString(R.string.alreadySaved) + "!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }


            }
        });

        //Implementamos el evento ?click? del bot?n
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        botonTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(InfoMovieShared.this);
                pDialog.setMessage(getResources().getString(R.string.searching));
                pDialog.setCancelable(true);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();

                SearchMovieURLTrailer searchorMovie = new SearchMovieURLTrailer(InfoMovieShared.this, pelicula);
                try {
                    String trailerId = searchorMovie.execute().get();
                    if(trailerId == null){
                        Toast toast = Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.notAviableTrailer),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                    else {
                        pDialog.dismiss();
                        Intent intent =  new Intent(InfoMovieShared.this, YoutubeActivityView.class);
                        intent.putExtra("TrailerId", trailerId);
                        startActivityForResult(intent, 1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    pDialog.dismiss();
                }
            }
        });
    }

    //this override the implemented method from asyncTask
    @Override
    public void processFinish(Object result){
        this.pelicula = (AudiovisualInterface) result;
        actualiza();
    }

    public void actualiza(){
        setShareIntent();

        // Title
        getSupportActionBar().setTitle(pelicula.getTitulo());

        Log.d("AA", Double.toString(pelicula.getRating()));
        if(pelicula.getRating() == 0.0){
            valoracion.setText("	" + getResources().getString(R.string.notavailable));
        }else{
            valoracion.setText("	" + Double.toString(pelicula.getRating()));
        }

        anyo.setText("	" + pelicula.getAnyo());
        // A?adir generos
        String gene = "";
        for (int j = 0; j < pelicula.getGeneros().size() ; j++){
            if ( j > 0){
                gene += ", " + pelicula.getGeneros().get(j).toLowerCase();
                generoLab.setText(getResources().getString(R.string.genders));
            }
            else gene += pelicula.getGeneros().get(j);
        }
        genero.setText("	" + gene);

        //A?adir directores
        String direc = "";
        for (int d = 0; d < pelicula.getDirectores().size() ; d++){
            if (d > 0){
                direc += ", " + pelicula.getDirectores().get(d);
                directorLab.setText(getResources().getString(R.string.directors));
            }
            else direc += pelicula.getDirectores().get(d);
        }
        director.setText("	" + direc);

        descripcion.setText(pelicula.getDescripcion());

        ImageView image = (ImageView)findViewById(R.id.poster);
        ImageLoader imageLoader = new ImageLoader(this);
        imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.movie_info);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_movie, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        // Return true to display menu
        return true;
    }

    private void setShareIntent() {
        System.out.println("aqui");
        if (mShareActionProvider != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/movie/" + pelicula.getId());
            sendIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(sendIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(InfoMovieShared.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}