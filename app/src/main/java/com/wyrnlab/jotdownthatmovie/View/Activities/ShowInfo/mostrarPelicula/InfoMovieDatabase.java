package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.View.OnClickListener;
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

public class InfoMovieDatabase extends AppCompatActivity implements AsyncResponse, StreamingRecyclerViewAdapter.ItemClickListener {

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
    Button botonViewed;
    Button botonStreaming;
    private ShareActionProvider mShareActionProvider;
    Integer position;
    SimilarMoviesModal similarMoviesModal;
    Context context;
    StreamingRecyclerViewAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_info);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent i = getIntent();
        pelicula = (AudiovisualInterface)i.getSerializableExtra("Pelicula");
        position = i.getIntExtra("Position", 0);

        //Recuperamos la informaci�n pasada en el intent
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
        botonViewed = (Button)findViewById(R.id.BtnViewed);
        botonStreaming = (Button)findViewById(R.id.BtnStreamingInfo);
        originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
        originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);


        actualiza();
        
      //Implementamos el evento �click� del bot�n
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
                    AlertDialog.Builder builder = new TrailerDialog(InfoMovieDatabase.this, pelicula.getOriginalLanguage(),SetTheLanguages.getLanguage(), pelicula);
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

        botonViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                pelicula.setViewed(pelicula.getViewed() ? false : true);

                if(DAO.getInstance().updateAsViewed(InfoMovieDatabase.this, pelicula)){
                    setViewedState();
                    if(pelicula.getViewed()){
                        MyUtils.showSnacknar(((Activity) InfoMovieDatabase.this).findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.MarkedAsViewed));
                    } else {
                        MyUtils.showSnacknar(((Activity) InfoMovieDatabase.this).findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.MarkedAsNOTViewed));
                    }
                } else {
                    MyUtils.showSnacknar(((Activity)InfoMovieDatabase.this).findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.MarkAsViewedError));
                }
            }
        });

        botonStreaming.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CheckInternetConection.isConnectingToInternet(InfoMovieDatabase.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    LinearLayout streamingRowLY = (LinearLayout) ((Activity) context).findViewById(R.id.StreamingInfoRowLY);

                    Button streamingButton = (Button) ((Activity) context).findViewById(R.id.BtnStreamingInfo);
                    streamingRowLY.removeView(streamingButton);

                    StreamingAPI searchor = new StreamingAPI(InfoMovieDatabase.this, String.valueOf(pelicula.getId()), General.MOVIE_TYPE, getResources().getString(R.string.searching)) {
                        @Override
                        public void onResponseReceived(Object result) {
                            RecyclerView recyclerView = findViewById(R.id.rvAnimals);
                            recyclerView.setLayoutManager(new LinearLayoutManager(InfoMovieDatabase.this, LinearLayoutManager.HORIZONTAL, false));
                            adapter = new StreamingRecyclerViewAdapter(InfoMovieDatabase.this, (List<Streaming>) result);
                            adapter.setClickListener(InfoMovieDatabase.this);
                            recyclerView.setAdapter(adapter);
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
                Intent intent = new Intent(InfoMovieDatabase.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        ImageView justWatch = (ImageView)findViewById(R.id.justWatchLogo);
        justWatch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String url = "https://www.themoviedb.org/movie/" + String.valueOf(pelicula.getId()) + "/watch";
                Intent intent = new Intent(InfoMovieDatabase.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void actualiza(){
        getSupportActionBar().setTitle(pelicula.getTitulo());

        anyo.setText("	" + pelicula.getAnyo());

        genero.setText("	" + pelicula.getGenerosToStrig());
        if(pelicula.getGeneros().size() > 1){
            generoLab.setText(getResources().getString(R.string.genders));
        }

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
        Bitmap stub = SetTheLanguages.getImageStub(InfoMovieDatabase.this);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to show image in full screen:
                new PhotoFullPopupWindow(InfoMovieDatabase.this, R.layout.popup_photo_full, view, null, pelicula.getImage() == null ? null : ImageHandler.getImage(pelicula.getImage()));

            }
        });
        if (pelicula.getDirectores().size() > 0) director.setText("	" + pelicula.getDirectores().get(0));

        setViewedState();
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

    private void setNotViewedOption(){
        botonViewed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.strike_eye_red, 0, 0, 0);
        botonViewed.setText(getString(R.string.MarkAsNOTViewed));
        botonViewed.setTextColor(Color.parseColor("#cc0000"));
    }

    private void setViewedOption(){
        botonViewed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_viewed, 0, 0, 0);
        botonViewed.setText(getString(R.string.MarkAsViewed));
        botonViewed.setTextColor(Color.BLACK);
    }

    private void setViewedState(){
        if(pelicula.getViewed()){
            setNotViewedOption();
        } else {
            setViewedOption();
        }
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
                } else if(resultCode == General.RESULT_CODE_SIMILAR_CLOSED){
                    if(similarMoviesModal != null && similarMoviesModal.popupWindow != null){
                        similarMoviesModal.popupWindow.dismiss();
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        setResult(General.RESULT_CODE_NEEDS_REFRESH);
        super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {
        Uri uri = Uri.parse(adapter.getItem(position).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
