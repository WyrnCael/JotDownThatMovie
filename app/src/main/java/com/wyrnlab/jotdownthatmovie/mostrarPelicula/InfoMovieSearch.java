package com.wyrnlab.jotdownthatmovie.mostrarPelicula;

import java.util.concurrent.ExecutionException;

import com.fedorvlasov.lazylist.ImageLoader;

import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.search.SearchURLTrailer;
import com.wyrnlab.jotdownthatmovie.video.YoutubeApi.YoutubeActivityView;

import api.search.AsyncResponse;
import api.search.AudiovisualInterface;
import api.search.Movies.SearchInfoMovie;
import api.search.TVShows.SearchInfoShow;
import data.General;

import com.wyrnlab.jotdownthatmovie.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.search.Movies.Pelicula;

public class InfoMovieSearch extends AppCompatActivity implements AsyncResponse {

	ProgressDialog pDialog;
	String type;
	AudiovisualInterface pelicula;
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
        
        Intent i = getIntent();
        pelicula = (AudiovisualInterface)i.getSerializableExtra("Pelicula");
        type = i.getStringExtra("Type");

        if(type.equalsIgnoreCase("Movie")) {
			SearchInfoMovie searchorMovie = new SearchInfoMovie(this, pelicula.getId());
			searchorMovie.delegate = this;
			searchorMovie.execute();
		} else {
			SearchInfoShow searchorShow = new SearchInfoShow(this, pelicula.getId());
			searchorShow.delegate = this;
			searchorShow.execute();
		}

        setContentView(R.layout.movie_info);
        
      //Obtenemos una referencia a los controles de la interfaz
        TextView anyo = (TextView)findViewById(R.id.Anyo);
        genero = (TextView)findViewById(R.id.genero);
        director = (TextView)findViewById(R.id.director);
        TextView valoracion = (TextView)findViewById(R.id.valoracion);
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
				 if(DAO.getInstance().insert(InfoMovieSearch.this, pelicula)){
					 Toast toast = Toast.makeText(getApplicationContext(),
							 getResources().getString(R.string.film) + " \"" + pelicula.getTitulo() + "\" " + getResources().getString(R.string.added) + "!",
							 Toast.LENGTH_SHORT);
					 toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
					 toast.show();

					 setResult(Activity.RESULT_OK);
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
        
      //Implementamos el evento �click� del bot�n
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
                pDialog = new ProgressDialog(InfoMovieSearch.this);
                pDialog.setMessage(getResources().getString(R.string.searching));
                pDialog.setCancelable(true);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();

                SearchURLTrailer searchorMovie = new SearchURLTrailer(InfoMovieSearch.this, pelicula);
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
                        Intent intent =  new Intent(InfoMovieSearch.this, YoutubeActivityView.class);
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


		if(pelicula.getRating() == 0.0){
			valoracion.setText("	" +  getResources().getString(R.string.notavailable));
		}else{
			valoracion.setText("	" + Double.toString(pelicula.getRating()));
		}
        
        ImageView image = (ImageView)findViewById(R.id.poster);
        ImageLoader imageLoader = new ImageLoader(this);
        imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
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
		System.out.println("aqui");
		if (mShareActionProvider != null) {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			if(type.equalsIgnoreCase("Movie")){
				sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/movie/" + pelicula.getId());
			} else {
				sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/tv/" + pelicula.getId());
			}
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
