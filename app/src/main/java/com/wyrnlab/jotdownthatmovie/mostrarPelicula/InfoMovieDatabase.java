package com.wyrnlab.jotdownthatmovie.mostrarPelicula;

import com.fedorvlasov.lazylist.ImageLoader;

import com.wyrnlab.jotdownthatmovie.search.SearchURLTrailer;
import com.wyrnlab.jotdownthatmovie.video.YoutubeApi.YoutubeActivityView;
import data.General;
import com.wyrnlab.jotdownthatmovie.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import api.search.Pelicula;

public class InfoMovieDatabase extends Activity {

	ProgressDialog pDialog;
	Pelicula pelicula;
	TextView descripcion;
	TextView genero;
	TextView director;
	TextView generoLab;
	TextView directorLab;
	Button botonVolver;
	Button botonTrailer;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        pelicula = (Pelicula)i.getSerializableExtra("Pelicula");
        
        setContentView(R.layout.movie_info_db);
        
      //Obtenemos una referencia a los controles de la interfaz
        TextView titulo = (TextView)findViewById(R.id.titulo);
        TextView anyo = (TextView)findViewById(R.id.Anyo);
        genero = (TextView)findViewById(R.id.genero);
        director = (TextView)findViewById(R.id.director);
        TextView valoracion = (TextView)findViewById(R.id.valoracion);
        descripcion = (TextView)findViewById(R.id.toda_la_descripcion);
        generoLab = (TextView)findViewById(R.id.generoLab);
        directorLab = (TextView)findViewById(R.id.directorLAb);
        botonVolver = (Button)findViewById(R.id.BtnAtrasDB);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);
        
      //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();
 
        titulo.setText(pelicula.getTitulo());
        anyo.setText("	" + pelicula.getAnyo());
        if (pelicula.getGeneros().size() > 0) genero.setText("	" + pelicula.getGeneros().get(0));
        if (pelicula.getDirectores().size() > 0) director.setText("	" + pelicula.getDirectores().get(0));
        valoracion.setText("	" + Double.toString(pelicula.getRating()));
        descripcion.setText(pelicula.getDescripcion());        
        ImageView image = (ImageView)findViewById(R.id.poster);
        ImageLoader imageLoader = new ImageLoader(this);
        imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
        
      //Implementamos el evento “click” del botón
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

                pDialog = new ProgressDialog(InfoMovieDatabase.this);
                pDialog.setMessage(getResources().getString(R.string.searching));
                pDialog.setCancelable(true);
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();

                SearchURLTrailer searchorMovie = new SearchURLTrailer(InfoMovieDatabase.this, pelicula);
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
                            Intent intent =  new Intent(InfoMovieDatabase.this, YoutubeActivityView.class);
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  //setContentView(R.layout.movie_info_db);
	}
}
