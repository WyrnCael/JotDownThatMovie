package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.ShareActionProvider;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.StreamingAPI;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.FullImages.PhotoFullPopupWindow;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.Streaming;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;
import com.wyrnlab.jotdownthatmovie.View.Activities.SimilarMoviesModal;
import com.wyrnlab.jotdownthatmovie.View.Activities.WebViewActivity;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.StreamingRecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.TrailerDialog;

import java.util.List;
import java.util.Locale;

public class InfoMovieSearch extends AppCompatActivity implements AsyncResponse, StreamingRecyclerViewAdapter.ItemClickListener {

	ProgressDialog pDialog;
	String type;
	AudiovisualInterface pelicula;
	TextView anyo;
	TextView valoracion;
	TextView descripcion;
	TextView genero;
	TextView director;
	TextView seasons;
	TextView generoLab;
	TextView directorLab;
	TextView originalTitle;
	TextView originalLanguage;
	Button botonAnadir;
	Button botonVolver;
	Button botonTrailer;
	Button botonSimilars;
	Button botonStreaming;
	ImageView image;
	private ShareActionProvider mShareActionProvider;
	int position;

	public RecyclerView listView;
	List<RowItem> rowItems;
	List<AudiovisualInterface> results;
	RecyclerViewAdapter adapter;
	int longClickPosition;

	SimilarMoviesModal similarMoviesModal;
	StreamingRecyclerViewAdapter adapterStreaming;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent i = getIntent();
        pelicula = (AudiovisualInterface)i.getSerializableExtra("Pelicula");
        type = i.getStringExtra("Type");
        position = i.getIntExtra("Position", 0);

        SearchInfoMovie searchorMovie = new SearchInfoMovie(this, pelicula.getId(), getString(R.string.searching));
		searchorMovie.delegate = this;
		MyUtils.execute(searchorMovie);

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
		botonSimilars = (Button)findViewById(R.id.BtnSimilars);
		originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
		originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);
		botonStreaming = (Button)findViewById(R.id.BtnStreamingInfo);

      //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

		// Title
		getSupportActionBar().setTitle(pelicula.getTitulo());

        anyo.setText("	" + pelicula.getAnyo());
        
        //Implementamos el evento click del botón
        botonAnadir.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
				 Intent resultIntent = new Intent();
				 resultIntent.putExtra("Name", pelicula.getTitulo());
				 resultIntent.putExtra("Position", position);
				 setResult(General.RESULT_CODE_ADD, resultIntent);
				 finish();
             }
        });

        similarMoviesModal = new SimilarMoviesModal(pelicula, InfoMovieSearch.this, InfoMovieSearch.this);
		botonSimilars.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pelicula.getSimilars().isEmpty()){
					MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.noSimilarMovies));
				} else {
					similarMoviesModal.createView();
				}
			}
		});
        
      //Implementamos el evento click del botón
        botonVolver.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {  
            	backPressed();
             }
        });
        
        botonTrailer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				if (!CheckInternetConection.isConnectingToInternet(InfoMovieSearch.this)) {
					MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.not_internet));
				} else {
					AlertDialog.Builder builder = new TrailerDialog(InfoMovieSearch.this, pelicula.getOriginalLanguage(),SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage(), Locale.getDefault().getCountry()), pelicula);
					builder.show();
				}
			}
  	     });

		botonStreaming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!CheckInternetConection.isConnectingToInternet(InfoMovieSearch.this)) {
					MyUtils.showSnacknar(findViewById(R.id.relativeLayoutMovieInfoDB), getResources().getString(R.string.not_internet));
				} else {
					LinearLayout streamingRowLY = (LinearLayout) InfoMovieSearch.this.findViewById(R.id.StreamingInfoRowLY);

					Button streamingButton = (Button) InfoMovieSearch.this.findViewById(R.id.BtnStreamingInfo);
					streamingRowLY.removeView(streamingButton);

					StreamingAPI searchor = new StreamingAPI(InfoMovieSearch.this, String.valueOf(pelicula.getId()), General.MOVIE_TYPE, getResources().getString(R.string.searching)) {
						@Override
						public void onResponseReceived(Object result) {
							RecyclerView recyclerView = findViewById(R.id.rvAnimals);
							recyclerView.setLayoutManager(new LinearLayoutManager(InfoMovieSearch.this, LinearLayoutManager.HORIZONTAL, false));
							adapterStreaming = new StreamingRecyclerViewAdapter(InfoMovieSearch.this, (List<Streaming>) result);
							adapterStreaming.setClickListener(InfoMovieSearch.this);
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
				Intent intent = new Intent(InfoMovieSearch.this, WebViewActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});

		ImageView justWatch = (ImageView)findViewById(R.id.justWatchLogo);
		justWatch.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				String url = "https://www.themoviedb.org/movie/" + String.valueOf(pelicula.getId()) + "/watch";
				Intent intent = new Intent(InfoMovieSearch.this, WebViewActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});


		if(pelicula.getRating() == 0.0){
			valoracion.setText("	" +  getResources().getString(R.string.notavailable));
		}else{
			valoracion.setText("	" + Double.toString(pelicula.getRating()));
		}
        

    }

	//this override the implemented method from asyncTask
	@Override
	public void processFinish(Object result){
		this.pelicula = (AudiovisualInterface) result;
		actualiza();
	}
	
	public void actualiza(){
		// A�adir generos
        genero.setText("	" + pelicula.getGenerosToStrig());
        if(pelicula.getGeneros().size() > 1){
			generoLab.setText(getResources().getString(R.string.genders));
		}

        //A�adir directores
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
		final ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Code to show image in full screen:
				new PhotoFullPopupWindow(InfoMovieSearch.this, R.layout.popup_photo_full, view, null, pelicula.getImagePath() == null ? null : ImageHandler.getImage(pelicula.getImage()));

			}
		});
		originalLanguage.setText(General.getLanguageTranslations(pelicula.getOriginalLanguage()));
		originalTitle.setText(pelicula.getTituloOriginal());

		similarMoviesModal.pelicula = this.pelicula;
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
	public void onBackPressed() {
		backPressed();
	}

	private void backPressed(){
		if(similarMoviesModal != null && similarMoviesModal.closed){
			setResult(General.RESULT_CODE_SIMILAR_CLOSED);
		} else {
			setResult(Activity.RESULT_CANCELED);
		}
		finish();
	}

	@Override
	public void onItemClick(View view, int position) {
		Uri uri = Uri.parse(adapterStreaming.getItem(position).getUrl());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		InfoMovieSearch.this.startActivity(intent);
	}
}
