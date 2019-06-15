package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula;

import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchMovieURLTrailer;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.View.Activities.YoutubeActivityView;
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

import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;

public class InfoMovieDatabase extends AppCompatActivity {

	ProgressDialog pDialog;
    AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView seasons;
	TextView descripcion;
	TextView genero;
	TextView director;
	TextView generoLab;
	TextView directorLab;
	Button botonVolver;
	Button botonTrailer;
    Button botonRemove;
    private ShareActionProvider mShareActionProvider;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Intent i = getIntent();
        pelicula = (AudiovisualInterface)i.getSerializableExtra("Pelicula");

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
        botonRemove = (Button)findViewById(R.id.BtnDeleteDB);

        // Title
        getSupportActionBar().setTitle(pelicula.getTitulo());

        anyo.setText("	" + pelicula.getAnyo());
        if (pelicula.getGeneros().size() > 0) genero.setText("	" + pelicula.getGeneros().get(0));
        if(pelicula.getRating() == 0.0){
            valoracion.setText("	" + getResources().getString(R.string.notavailable));
        }else{
            valoracion.setText("	" + Double.toString(pelicula.getRating()));
        }
        descripcion.setText(pelicula.getDescripcion());
        ImageView image = (ImageView)findViewById(R.id.poster);
        image.setImageBitmap(ImageHandler.getImage(pelicula.getImage()));
        if (pelicula.getDirectores().size() > 0) director.setText("	" + pelicula.getDirectores().get(0));
        
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
                if (!CheckInternetConection.isConnectingToInternet(InfoMovieDatabase.this)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.not_internet),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    pDialog = new ProgressDialog(InfoMovieDatabase.this);
                    pDialog.setMessage(getResources().getString(R.string.searching));
                    pDialog.setCancelable(true);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.show();

                    SearchMovieURLTrailer searchorMovie = new SearchMovieURLTrailer(InfoMovieDatabase.this, pelicula) {
                        @Override
                        public void onResponseReceived(Object result) {
                            String trailerId = (String) result;
                            if (trailerId == null) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        getResources().getString(R.string.notAviableTrailer),
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {
                                pDialog.dismiss();
                                Intent intent = new Intent(InfoMovieDatabase.this, YoutubeActivityView.class);
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

        botonRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DAO.getInstance().delete(InfoMovieDatabase.this, pelicula.getTitulo(), pelicula.getAnyo());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Movie) + " \"" + pelicula.getTitulo() + "\" " + getResources().getString(R.string.removed) + "!", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
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
}
