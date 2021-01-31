package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.GetSimilarTVShows;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchInfoShow;
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
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Activities.SimilarMoviesModal;
import com.wyrnlab.jotdownthatmovie.View.Activities.WebViewActivity;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.StreamingRecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.TrailerDialog;

import java.util.List;
import java.util.Locale;

public class InfoTVShowDatabase extends AppCompatActivity implements AsyncResponse, StreamingRecyclerViewAdapter.ItemClickListener {

	ProgressDialog pDialog;
    AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView seasons;
	TextView descripcion;
	TextView genero;
	TextView generoLab;
	//TextView directorLab;
	Button botonVolver;
	Button botonTrailer;
    Button botonSimilars;
    Button botonRemove;
    Button botonRefresh;
    Button botonViewed;
    Button botonStreaming;
    TextView originalTitle;
    TextView originalLanguage;
    private ShareActionProvider mShareActionProvider;
    Integer position;
    SimilarMoviesModal similarMoviesModal;
    Context context;
    StreamingRecyclerViewAdapter adapterStreaming;
	
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

        setContentView(R.layout.show_info_db);

        //Obtenemos una referencia a los controles de la interfaz
         anyo = (TextView)findViewById(R.id.Anyo);
        genero = (TextView)findViewById(R.id.genero);
        valoracion = (TextView)findViewById(R.id.valoracion);
        descripcion = (TextView)findViewById(R.id.toda_la_descripcion);
        generoLab = (TextView)findViewById(R.id.generoLab);
        //directorLab = (TextView)findViewById(R.id.directorLAb);
        botonVolver = (Button)findViewById(R.id.BtnAtrasDB);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);
        botonSimilars = (Button)findViewById(R.id.BtnSimilars);
        botonRemove = (Button)findViewById(R.id.BtnDeleteDB);
        seasons = (TextView)findViewById(R.id.seasons);
        botonRefresh = (Button)findViewById(R.id.BtnRefresh);
        botonViewed = (Button)findViewById(R.id.BtnViewed);
        originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
        originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);
        botonStreaming = (Button)findViewById(R.id.BtnStreamingInfo);

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
                if (!CheckInternetConection.isConnectingToInternet(InfoTVShowDatabase.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    AlertDialog.Builder builder = new TrailerDialog(InfoTVShowDatabase.this, pelicula.getOriginalLanguage(), SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage()), pelicula);
                    builder.show();
                }
            }
        });

        context = InfoTVShowDatabase.this;
        botonSimilars.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.checkInternetConectionAndStoragePermission(InfoTVShowDatabase.this);
                if(General.base_url == null){
                    SearchBaseUrl searchor = new SearchBaseUrl(InfoTVShowDatabase.this){
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

        botonRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.checkInternetConectionAndStoragePermission(InfoTVShowDatabase.this);
                if(General.base_url == null){
                    SearchBaseUrl searchor = new SearchBaseUrl(InfoTVShowDatabase.this){
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

                if(DAO.getInstance().updateAsViewed(InfoTVShowDatabase.this, pelicula)){
                    setViewedState();
                    if(pelicula.getViewed()){
                        MyUtils.showSnacknar(((Activity) InfoTVShowDatabase.this).findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.MarkedAsViewed));
                    } else {
                        MyUtils.showSnacknar(((Activity) InfoTVShowDatabase.this).findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.MarkedAsNOTViewed));
                    }
                } else {
                    MyUtils.showSnacknar(((Activity)InfoTVShowDatabase.this).findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.MarkAsViewedError));
                }
            }
        });

        botonStreaming.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CheckInternetConection.isConnectingToInternet(InfoTVShowDatabase.this)) {
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
                } else {
                    LinearLayout streamingRowLY = (LinearLayout) InfoTVShowDatabase.this.findViewById(R.id.StreamingInfoRowLY);

                    Button streamingButton = (Button) InfoTVShowDatabase.this.findViewById(R.id.BtnStreamingInfo);
                    streamingRowLY.removeView(streamingButton);

                    StreamingAPI searchor = new StreamingAPI(InfoTVShowDatabase.this, String.valueOf(pelicula.getId()), General.TVSHOW_TYPE, getResources().getString(R.string.searching)) {
                        @Override
                        public void onResponseReceived(Object result) {
                            RecyclerView recyclerView = findViewById(R.id.rvAnimals);
                            recyclerView.setLayoutManager(new LinearLayoutManager(InfoTVShowDatabase.this, LinearLayoutManager.HORIZONTAL, false));
                            adapterStreaming = new StreamingRecyclerViewAdapter(InfoTVShowDatabase.this, (List<Streaming>) result);
                            adapterStreaming.setClickListener(InfoTVShowDatabase.this);
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
                String url = "https://www.themoviedb.org/tv/" + String.valueOf(pelicula.getId());
                Intent intent = new Intent(InfoTVShowDatabase.this, WebViewActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        ImageView justWatch = (ImageView)findViewById(R.id.justWatchLogo);
        justWatch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String url = "https://www.themoviedb.org/tv/" + String.valueOf(pelicula.getId()) + "/watch";
                Intent intent = new Intent(InfoTVShowDatabase.this, WebViewActivity.class);
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

        if(pelicula.getRating() == 0.0){
            valoracion.setText("	" + getResources().getString(R.string.notavailable));
        }else{
            valoracion.setText("	" + Double.toString(pelicula.getRating()));
        }
        descripcion.setText(pelicula.getDescripcion());
        seasons.setText("	" + pelicula.getSeasons());
        ImageView image = (ImageView)findViewById(R.id.poster);
        image.setImageBitmap(ImageHandler.getImage(pelicula.getImage()));
        Bitmap stub = BitmapFactory.decodeResource(getResources(), R.drawable.stub);
        if(ImageHandler.getImage(pelicula.getImage()) != stub){
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Code to show image in full screen:
                    new PhotoFullPopupWindow(InfoTVShowDatabase.this, R.layout.popup_photo_full, view, null, ImageHandler.getImage(pelicula.getImage()));

                }
            });
        }
        if(pelicula.getOriginalLanguage() == null){
            originalLanguage.setText(getString(R.string.NeedsRefresh));
            originalLanguage.setTextColor(Color.RED);
        } else {
            originalLanguage.setText(General.getLanguageTranslations(pelicula.getOriginalLanguage()));
            originalLanguage.setTextColor(Color.BLACK);
        }
        originalTitle.setText(pelicula.getTituloOriginal());
    }

    private void refreshSearch(){
        SearchInfoShow searchorMovie = new SearchInfoShow(InfoTVShowDatabase.this, pelicula.getId(), getString(R.string.searching)){
            @Override
            public void onResponseReceived(Object result) {
                pelicula = (AudiovisualInterface) result;
                actualiza();
                if(DAO.getInstance().update(InfoTVShowDatabase.this, (AudiovisualInterface) result)){
                    MyUtils.showSnacknar(((Activity)InfoTVShowDatabase.this).findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.RefreshedSuccess));
                } else {
                    MyUtils.showSnacknar(((Activity)InfoTVShowDatabase.this).findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.RefreshedError));
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
        GetSimilarTVShows searchorSimilars = new GetSimilarTVShows(context, pelicula.getId(), null) {
            @Override
            public void onResponseReceived(Object result) {
                pelicula.setSimilars((List<AudiovisualInterface>) result);
                if(pelicula.getSimilars().isEmpty()){
                    MyUtils.showSnacknar(findViewById(R.id.relativeLayoutTVInfoDB), getResources().getString(R.string.noSimilarMovies));
                } else {
                    similarMoviesModal = new SimilarMoviesModal(pelicula, InfoTVShowDatabase.this, InfoTVShowDatabase.this);
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
        Uri uri = Uri.parse(adapterStreaming.getItem(position).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        InfoTVShowDatabase.this.startActivity(intent);
    }
}
