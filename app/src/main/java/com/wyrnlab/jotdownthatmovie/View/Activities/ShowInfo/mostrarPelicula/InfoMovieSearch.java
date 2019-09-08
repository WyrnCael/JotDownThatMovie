package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchMovieURLTrailer;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.FullImages.PhotoFullPopupWindow;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.View.Activities.YoutubeActivityView;

public class InfoMovieSearch extends AppCompatActivity implements AsyncResponse {

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
	Button botonAnadir;
	Button botonVolver;
	Button botonTrailer;
	ImageView image;
	private ShareActionProvider mShareActionProvider;
	int position;
	
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
        
      //Implementamos el evento click del botón
        botonVolver.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {  
            	setResult(Activity.RESULT_CANCELED);
		        finish();
             }
        });
        
        botonTrailer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				if(!CheckInternetConection.isConnectingToInternet(InfoMovieSearch.this)){
					MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.not_internet));
				} else {
					pDialog = new ProgressDialog(InfoMovieSearch.this);
					pDialog.setMessage(getResources().getString(R.string.searching));
					pDialog.setCancelable(true);
					pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pDialog.show();

					SearchMovieURLTrailer searchorMovie = new SearchMovieURLTrailer(InfoMovieSearch.this, pelicula) {
						@Override
						public void onResponseReceived(Object result) {
							String trailerId = (String) result;
							if (trailerId == null) {
								MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), getResources().getString(R.string.notAviableTrailer));
							} else {
								pDialog.dismiss();
								Intent intent = new Intent(InfoMovieSearch.this, YoutubeActivityView.class);
								intent.putExtra("TrailerId", trailerId);
								startActivityForResult(intent, 1);
							}
							pDialog.dismiss();
						}
					};
					MyUtils.execute(searchorMovie);
				}
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
        String gene = "";
        for (int j = 0; j < pelicula.getGeneros().size() ; j++){
        	if ( j > 0){
        		gene += ", " + pelicula.getGeneros().get(j).toLowerCase();
        		generoLab.setText(getResources().getString(R.string.genders));
        	}
        	else gene += pelicula.getGeneros().get(j);        	
        }
        genero.setText("	" + gene);

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
}
