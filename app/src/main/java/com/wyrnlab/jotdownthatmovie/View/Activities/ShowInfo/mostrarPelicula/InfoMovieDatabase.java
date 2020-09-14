package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.GetSimilarMovies;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchMovieURLTrailer;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.FullImages.PhotoFullPopupWindow;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Model.Trailer;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;
import com.wyrnlab.jotdownthatmovie.View.Activities.SearchActivity;
import com.wyrnlab.jotdownthatmovie.View.Activities.SearchResultActivity;
import com.wyrnlab.jotdownthatmovie.View.Activities.SimilarMoviesModal;
import com.wyrnlab.jotdownthatmovie.View.Activities.YoutubeActivityView;
import com.wyrnlab.jotdownthatmovie.View.TrailerDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class InfoMovieDatabase extends AppCompatActivity implements AsyncResponse {

	AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView seasons;
	TextView descripcion;
	TextView genero;
	TextView director;
	TextView generoLab;
	TextView directorLab;
    TextView originalTitle;
    TextView originalLanguage;
	Button botonVolver;
    Button botonRefresh;
	Button botonTrailer;
    Button botonSimilars;
    Button botonRemove;
    private ShareActionProvider mShareActionProvider;
    Integer position;
    SimilarMoviesModal similarMoviesModal;
    Context context;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent i = getIntent();
        pelicula = (AudiovisualInterface)i.getSerializableExtra("Pelicula");
        position = i.getIntExtra("Position", 0);

        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

        setContentView(R.layout.movie_info_db);

        //Obtenemos una referencia a los controles de la interfaz
         anyo = (TextView)findViewById(R.id.Anyo);
        genero = (TextView)findViewById(R.id.genero);
        director = (TextView)findViewById(R.id.director);
        valoracion = (TextView)findViewById(R.id.valoracion);
        descripcion = (TextView)findViewById(R.id.toda_la_descripcion);
        generoLab = (TextView)findViewById(R.id.generoLab);
        directorLab = (TextView)findViewById(R.id.directorLAb);
        botonVolver = (Button)findViewById(R.id.BtnAtrasDB);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);
        botonSimilars = (Button)findViewById(R.id.BtnSimilars);
        botonRemove = (Button)findViewById(R.id.BtnDeleteDB);
        botonRefresh = (Button)findViewById(R.id.BtnRefresh);
        originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
        originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);


        actualiza();
        
      //Implementamos el evento “click” del botón
        botonVolver.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {  
            	 setResult(General.RESULT_CODE_NEEDS_REFRESH);
    	         finish();
             }
        });
        
        botonTrailer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckInternetConection.isConnectingToInternet(InfoMovieDatabase.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    AlertDialog.Builder builder = new TrailerDialog(InfoMovieDatabase.this, pelicula.getOriginalLanguage(),SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage()), pelicula);
                    builder.show();
                }
            }
        });

        context = InfoMovieDatabase.this;
        botonSimilars.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.checkInternetConectionAndStoragePermission(InfoMovieDatabase.this);
                if(General.base_url == null){
                    SearchBaseUrl searchor = new SearchBaseUrl(InfoMovieDatabase.this){
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

        botonRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Position", position);
                setResult(General.RESULT_CODE_REMOVED, resultIntent);
                finish();
            }
        });

        botonVolver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(General.RESULT_CODE_NEEDS_REFRESH);
                finish();
            }
        });

        botonRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.checkInternetConectionAndStoragePermission(InfoMovieDatabase.this);
                if(General.base_url == null){
                    SearchBaseUrl searchor = new SearchBaseUrl(InfoMovieDatabase.this){
                        @Override
                        public void onResponseReceived(Object result){
                            refreshSearch();
                        }
                    };
                    MyUtils.execute(searchor);
                } else {
                    refreshSearch();
                }
            }
        });
    }

    private void actualiza(){
        getSupportActionBar().setTitle(pelicula.getTitulo());

        anyo.setText("	" + pelicula.getAnyo());
        if (pelicula.getGeneros().size() > 0) genero.setText("	" + pelicula.getGeneros().get(0));
        valoracion.setText(pelicula.getRating() == 0.0 ? "	" + getResources().getString(R.string.notavailable) : "	" + Double.toString(pelicula.getRating()));
        descripcion.setText(pelicula.getDescripcion());
        if(pelicula.getOriginalLanguage() == null){
            originalLanguage.setText(getString(R.string.NeedsRefresh));
            originalLanguage.setTextColor(Color.RED);
        } else {
            originalLanguage.setText(General.getLanguageTranslations(pelicula.getOriginalLanguage()));
            originalLanguage.setTextColor(Color.BLACK);
        }
        originalTitle.setText(pelicula.getTituloOriginal());
        ImageView image = (ImageView)findViewById(R.id.poster);
        image.setImageBitmap(ImageHandler.getImage(pelicula.getImage()));
        Bitmap stub = BitmapFactory.decodeResource(getResources(), R.drawable.stub);
        if(ImageHandler.getImage(pelicula.getImage()) != stub){
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Code to show image in full screen:
                    new PhotoFullPopupWindow(InfoMovieDatabase.this, R.layout.popup_photo_full, view, null, ImageHandler.getImage(pelicula.getImage()));

                }
            });
        }
        if (pelicula.getDirectores().size() > 0) director.setText("	" + pelicula.getDirectores().get(0));
    }

    private void refreshSearch(){
        SearchInfoMovie searchorMovie = new SearchInfoMovie(InfoMovieDatabase.this, pelicula.getId(), getString(R.string.searching)){
            @Override
            public void onResponseReceived(Object result) {
                pelicula = (AudiovisualInterface) result;
                actualiza();
                if(DAO.getInstance().update(InfoMovieDatabase.this, (AudiovisualInterface) result)){
                    MyUtils.showSnacknar(((Activity)InfoMovieDatabase.this).findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.RefreshedSuccess));
                } else {
                    MyUtils.showSnacknar(((Activity)InfoMovieDatabase.this).findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.RefreshedError));
                }
            }
        };
        searchorMovie.execute();
    }

    private void searchSimilars(){
        GetSimilarMovies searchorSimilars = new GetSimilarMovies(context, pelicula.getId(), null) {
            @Override
            public void onResponseReceived(Object result) {
                pelicula.setSimilars((List<AudiovisualInterface>) result);
                if(pelicula.getSimilars().isEmpty()){
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.noSimilarMovies));
                } else {
                    similarMoviesModal = new SimilarMoviesModal(pelicula, InfoMovieDatabase.this, InfoMovieDatabase.this);
                    similarMoviesModal.createView();
                }
            }
        };
        searchorSimilars.execute(String.valueOf(pelicula.getId()));
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  //setContentView(R.layout.movie_info_db);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_movie, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        setShareIntent();

        // Return true to display menu
        return true;
    }

    private void setShareIntent() {
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
    public void processFinish(Object result) {
        if( ((List<AudiovisualInterface>) result).size() == 0) {
            MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.without_results));
        } else {
            this.pelicula.setSimilars((List<AudiovisualInterface>) result);
            similarMoviesModal.createView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
            case General.REQUEST_CODE_PELIBUSCADA:
                if (resultCode == General.RESULT_CODE_ADD) {
                    similarMoviesModal.removeAndSaveItem(data);
                }
        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(General.RESULT_CODE_NEEDS_REFRESH);
        super.onBackPressed();
    }
}
