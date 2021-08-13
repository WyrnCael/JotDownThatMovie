package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.StreamingAPI;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.GetSimilarMovies;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.FullImages.PhotoFullPopupWindow;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Streaming;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;
import com.wyrnlab.jotdownthatmovie.View.Activities.SimilarMoviesModal;
import com.wyrnlab.jotdownthatmovie.View.Activities.WebViewActivity;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.StreamingRecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.TrailerDialog;

import java.util.List;

/**
 * Created by Jota on 27/12/2017.
 */

public class InfoMovieShared extends AppCompatActivity implements AsyncResponse, StreamingRecyclerViewAdapter.ItemClickListener {

    ProgressDialog pDialog;
    AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView descripcion;
    TextView genero;
    TextView director;
    TextView generoLab;
    TextView directorLab;
    TextView originalTitle;
    TextView originalLanguage;
    Button botonAnadir;
    Button botonVolver;
    Button botonSimilars;
    Button botonTrailer;
    Button botonStreaming;
    private ShareActionProvider mShareActionProvider;
    SimilarMoviesModal similarMoviesModal;
    Context context;
    ImageView image;
    ImageLoader imageLoader;
    StreamingRecyclerViewAdapter adapterStreaming;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_activity_info);

        setContentView(R.layout.movie_info);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!CheckInternetConection.isConnectingToInternet(InfoMovieShared.this)){
            MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.not_internet));
        } else {
            if(General.base_url == null){
                SearchBaseUrl searchor = new SearchBaseUrl(this) {
                    @Override
                    public void onResponseReceived(Object result) {

                    }
                };
                MyUtils.execute(searchor);
            }

        }

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri url = intent.getData();

        String link = url.toString();

        int id = -1;
        String linkWoMovie = link.substring(link.indexOf("movie/") + 6, link.length());
        if(linkWoMovie.indexOf("-") == -1 && linkWoMovie.indexOf("/") == -1)
            id = Integer.valueOf(linkWoMovie);
        else if (linkWoMovie.indexOf("-") != -1)
            id = Integer.valueOf(linkWoMovie.substring(0, linkWoMovie.indexOf("-")));
        else if (linkWoMovie.indexOf("/") != -1)
            id = Integer.valueOf(linkWoMovie.substring(0, linkWoMovie.indexOf("/")));

        SearchInfoMovie searchorMovie = new SearchInfoMovie(this, id, getString(R.string.searching));
        searchorMovie.delegate = this;
        MyUtils.execute(searchorMovie);



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
        botonSimilars = (Button)findViewById(R.id.BtnSimilars);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);
        originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
        originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);
        botonStreaming = (Button)findViewById(R.id.BtnStreamingInfo);

        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

        //Implementamos el evento click del botón
        botonAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DAO.getInstance().insert(InfoMovieShared.this, pelicula)){
                    //Intent intent = new Intent(InfoMovieShared.this, MainActivity.class);
                    //intent.putExtra("Name", pelicula.getTitulo());
                    //startActivity(intent);
                    finish();
                } else {
                    MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.film) + " \"" + pelicula.getTitulo() + "\" " + getResources().getString(R.string.alreadySaved) + "!");
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
                if (!CheckInternetConection.isConnectingToInternet(InfoMovieShared.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    AlertDialog.Builder builder = new TrailerDialog(InfoMovieShared.this, pelicula.getOriginalLanguage(),SetTheLanguages.getLanguage(), pelicula);
                    builder.show();
                }
            }
        });

        context = InfoMovieShared.this;
        botonSimilars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.checkInternetConectionAndStoragePermission(InfoMovieShared.this);
                if(General.base_url == null){
                    SearchBaseUrl searchor = new SearchBaseUrl(InfoMovieShared.this){
                        @Override
                        public void onResponseReceived(Object result){
                            searchSimilars();
                        }
                    };
                    MyUtils.execute(searchor);
                } else {
                    searchSimilars();
                }
            }
        });

        botonStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CheckInternetConection.isConnectingToInternet(InfoMovieShared.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    LinearLayout streamingRowLY = (LinearLayout) InfoMovieShared.this.findViewById(R.id.StreamingInfoRowLY);

                    Button streamingButton = (Button) InfoMovieShared.this.findViewById(R.id.BtnStreamingInfo);
                    streamingRowLY.removeView(streamingButton);

                    StreamingAPI searchor = new StreamingAPI(InfoMovieShared.this, String.valueOf(pelicula.getId()), General.MOVIE_TYPE, getResources().getString(R.string.searching)) {
                        @Override
                        public void onResponseReceived(Object result) {
                            RecyclerView recyclerView = findViewById(R.id.rvAnimals);
                            recyclerView.setLayoutManager(new LinearLayoutManager(InfoMovieShared.this, LinearLayoutManager.HORIZONTAL, false));
                            adapterStreaming = new StreamingRecyclerViewAdapter(InfoMovieShared.this, (List<Streaming>) result);
                            adapterStreaming.setClickListener(InfoMovieShared.this);
                            recyclerView.setAdapter(adapterStreaming);
                        }
                    };
                    MyUtils.execute(searchor);
                }
            }
        });

        ImageView imdbLogo = (ImageView)findViewById(R.id.tmdbLogo);
        imdbLogo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String url = "https://www.themoviedb.org/movie/" + String.valueOf(pelicula.getId());
                Intent intent = new Intent(InfoMovieShared.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        ImageView justWatch = (ImageView)findViewById(R.id.justWatchLogo);
        justWatch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String url = "https://www.themoviedb.org/movie/" + String.valueOf(pelicula.getId()) + "/watch";
                Intent intent = new Intent(InfoMovieShared.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void searchSimilars(){
        GetSimilarMovies searchorSimilars = new GetSimilarMovies(context, pelicula.getId(), null) {
            @Override
            public void onResponseReceived(Object result) {
                pelicula.setSimilars((List<AudiovisualInterface>) result);
                if(pelicula.getSimilars().isEmpty()){
                    MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.noSimilarMovies));
                } else {
                    similarMoviesModal = new SimilarMoviesModal(pelicula, InfoMovieShared.this, InfoMovieShared.this);
                    similarMoviesModal.createView();
                }
            }
        };
        searchorSimilars.execute(String.valueOf(pelicula.getId()));
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
        if(pelicula.getRating() == 0.0){
            valoracion.setText("	" + getResources().getString(R.string.notavailable));
        }else{
            valoracion.setText("	" + Double.toString(pelicula.getRating()));
        }

        anyo.setText("	" + pelicula.getAnyo());
        // A?adir generos

        genero.setText("	" + pelicula.getGenerosToStrig());
        if(pelicula.getGeneros().size() > 1){
            generoLab.setText(getResources().getString(R.string.genders));
        }

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

        image = (ImageView)findViewById(R.id.poster);
        imageLoader = new ImageLoader(this, false);
        if(General.base_url == null){
            SearchBaseUrl searchor = new SearchBaseUrl(InfoMovieShared.this){
                @Override
                public void onResponseReceived(Object result){
                    imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
                }
            };
            MyUtils.execute(searchor);
        } else {
            imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to show image in full screen:
                new PhotoFullPopupWindow(InfoMovieShared.this, R.layout.popup_photo_full, view, null, pelicula.getImagePath() == null ? null : ImageHandler.getImage(pelicula.getImage()));

            }
        });
        originalLanguage.setText(General.getLanguageTranslations(pelicula.getOriginalLanguage()));
        originalTitle.setText(pelicula.getTituloOriginal());
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
        //Intent intent = new Intent(InfoMovieShared.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case General.REQUEST_CODE_PELIBUSCADA:
                if (resultCode == General.RESULT_CODE_ADD) {
                    similarMoviesModal.removeAndSaveItem(data);
                } else if(resultCode == General.RESULT_CODE_SIMILAR_CLOSED){
                    if(similarMoviesModal != null && similarMoviesModal.popupWindow != null){
                        similarMoviesModal.popupWindow.dismiss();
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(View view, int position) {
        Uri uri = Uri.parse(adapterStreaming.getItem(position).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        InfoMovieShared.this.startActivity(intent);
    }

}