package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow;

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

import android.util.Log;
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
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.GetSimilarTVShows;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchInfoShow;
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
import com.wyrnlab.jotdownthatmovie.View.Activities.MainActivity;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Activities.SimilarMoviesModal;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.StreamingRecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.TrailerDialog;

import java.util.List;
import java.util.Locale;

/**
 * Created by Jota on 27/12/2017.
 */

public class InfoTVShowShared extends AppCompatActivity implements AsyncResponse, StreamingRecyclerViewAdapter.ItemClickListener {

    ProgressDialog pDialog;
    AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView season;
    TextView descripcion;
    TextView genero;
    TextView generoLab;
    TextView originalTitle;
    TextView originalLanguage;
    //TextView directorLab;
    Button botonAnadir;
    Button botonVolver;
    Button botonTrailer;
    Button botonSimilars;
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

        setContentView(R.layout.show_info);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!CheckInternetConection.isConnectingToInternet(InfoTVShowShared.this)){
            MyUtils.showSnacknar(findViewById(R.id.scrollViewShowInfo), getResources().getString(R.string.not_internet));
        } else {
            if(General.base_url == null){
                SearchBaseUrl searchor = new SearchBaseUrl(this) {
                    @Override
                    public void onResponseReceived(Object result) {

                    }
                };

            }

        }

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri url = intent.getData();

        String link = url.toString();

        int id;
        if(link.indexOf("-") == -1)
            id = Integer.valueOf(link.substring(link.indexOf("tv/") + 3, link.length()));
        else
            id = Integer.valueOf(link.substring(link.indexOf("tv/") + 3, link.indexOf("-")));

        SearchInfoShow searchorShow = new SearchInfoShow(this, id, getString(R.string.searching));
        searchorShow.delegate = this;
        MyUtils.execute(searchorShow);

        //Obtenemos una referencia a los controles de la interfaz
        anyo = (TextView)findViewById(R.id.Anyo);
        genero = (TextView)findViewById(R.id.genero);
        valoracion = (TextView)findViewById(R.id.valoracion);
        descripcion = (TextView)findViewById(R.id.toda_la_descripcion);
        generoLab = (TextView)findViewById(R.id.generoLab);
        //directorLab = (TextView)findViewById(R.id.directorLAb);
        botonAnadir = (Button)findViewById(R.id.BtnAnadir);
        botonVolver = (Button)findViewById(R.id.BtnAtras);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);
        botonSimilars = (Button)findViewById(R.id.BtnSimilars);
        season = (TextView)findViewById(R.id.seasons);
        originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
        originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);
        botonStreaming = (Button)findViewById(R.id.BtnStreamingInfo);

        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

        //Implementamos el evento click del botón
        botonAnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DAO.getInstance().insert(InfoTVShowShared.this, pelicula)){
                    Intent intent = new Intent(InfoTVShowShared.this, MainActivity.class);
                    intent.putExtra("Name", pelicula.getTitulo());
                    startActivity(intent);
                    finish();
                } else {
                    MyUtils.showSnacknar(findViewById(R.id.scrollViewShowInfo), " \"" + pelicula.getTitulo() + "\" " + getResources().getString(R.string.alreadySaved) + "!");
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
                if (!CheckInternetConection.isConnectingToInternet(InfoTVShowShared.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    AlertDialog.Builder builder = new TrailerDialog(InfoTVShowShared.this, pelicula.getOriginalLanguage(), SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage()), pelicula);
                    builder.show();
                }
            }
        });

        context = InfoTVShowShared.this;
        botonSimilars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.checkInternetConectionAndStoragePermission(InfoTVShowShared.this);
                if(General.base_url == null){
                    SearchBaseUrl searchor = new SearchBaseUrl(InfoTVShowShared.this){
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

                if (!CheckInternetConection.isConnectingToInternet(InfoTVShowShared.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    LinearLayout streamingRowLY = (LinearLayout) InfoTVShowShared.this.findViewById(R.id.StreamingInfoRowLY);

                    Button streamingButton = (Button) InfoTVShowShared.this.findViewById(R.id.BtnStreamingInfo);
                    streamingRowLY.removeView(streamingButton);

                    StreamingAPI searchor = new StreamingAPI(InfoTVShowShared.this, String.valueOf(pelicula.getId()), General.TVSHOW_TYPE, getResources().getString(R.string.searching)) {
                        @Override
                        public void onResponseReceived(Object result) {
                            RecyclerView recyclerView = findViewById(R.id.rvAnimals);
                            recyclerView.setLayoutManager(new LinearLayoutManager(InfoTVShowShared.this, LinearLayoutManager.HORIZONTAL, false));
                            adapterStreaming = new StreamingRecyclerViewAdapter(InfoTVShowShared.this, (List<Streaming>) result);
                            adapterStreaming.setClickListener(InfoTVShowShared.this);
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
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.themoviedb.org/tv/" + String.valueOf(pelicula.getId())));
                startActivity(intent);
            }
        });

        ImageView justWatch = (ImageView)findViewById(R.id.justWatchLogo);
        justWatch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.themoviedb.org/tv/" + String.valueOf(pelicula.getId()) + "/watch"));
                startActivity(intent);
            }
        });
    }

    private void searchSimilars(){
        GetSimilarTVShows searchorSimilars = new GetSimilarTVShows(context, pelicula.getId(), null) {
            @Override
            public void onResponseReceived(Object result) {
                pelicula.setSimilars((List<AudiovisualInterface>) result);
                if(pelicula.getSimilars().isEmpty()){
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.noSimilarMovies));
                } else {
                    similarMoviesModal = new SimilarMoviesModal(pelicula, InfoTVShowShared.this, InfoTVShowShared.this);
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
        /*String direc = "";
        for (int d = 0; d < pelicula.getDirectores().size() ; d++){
            if (d > 0){
                direc += ", " + pelicula.getDirectores().get(d);
                directorLab.setText(getResources().getString(R.string.directors));
            }
            else direc += pelicula.getDirectores().get(d);
        }*/

        descripcion.setText(pelicula.getDescripcion());
        season.setText("	" + pelicula.getSeasons());

        image = (ImageView)findViewById(R.id.poster);
        imageLoader = new ImageLoader(this);
        if(General.base_url == null){
            SearchBaseUrl searchor = new SearchBaseUrl(InfoTVShowShared.this){
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
                new PhotoFullPopupWindow(InfoTVShowShared.this, R.layout.popup_photo_full, view, null, pelicula.getImagePath() == null ? null : ImageHandler.getImage(pelicula.getImage()));

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
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/tv/" + pelicula.getId());
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
        Intent intent = new Intent(InfoTVShowShared.this, MainActivity.class);
        startActivity(intent);
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
        InfoTVShowShared.this.startActivity(intent);
    }
}