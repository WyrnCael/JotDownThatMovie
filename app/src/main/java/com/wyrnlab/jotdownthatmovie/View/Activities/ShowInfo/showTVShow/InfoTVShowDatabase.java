package com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchShowURLTrailer;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.FullImages.PhotoFullPopupWindow;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Activities.YoutubeActivityView;

public class InfoTVShowDatabase extends AppCompatActivity {

	ProgressDialog pDialog;
    AudiovisualInterface pelicula;
    TextView anyo;
    TextView valoracion;
    TextView seasons;
	TextView descripcion;
	TextView genero;
	TextView generoLab;
	TextView directorLab;
	Button botonVolver;
	Button botonTrailer;
    Button botonRemove;
    private ShareActionProvider mShareActionProvider;
    Integer position;
	
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
        directorLab = (TextView)findViewById(R.id.directorLAb);
        botonVolver = (Button)findViewById(R.id.BtnAtrasDB);
        botonTrailer = (Button)findViewById(R.id.BtnTrailer);
        botonRemove = (Button)findViewById(R.id.BtnDeleteDB);
        seasons = (TextView)findViewById(R.id.seasons);

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
                if(!CheckInternetConection.isConnectingToInternet(InfoTVShowDatabase.this)){
                    MyUtils.showSnacknar(findViewById(R.id.scrollView1), getResources().getString(R.string.not_internet));
                } else {
                    pDialog = new ProgressDialog(InfoTVShowDatabase.this);
                    pDialog.setMessage(getResources().getString(R.string.searching));
                    pDialog.setCancelable(true);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.show();

                    SearchShowURLTrailer searchorShow = new SearchShowURLTrailer(InfoTVShowDatabase.this, pelicula) {
                        @Override
                        public void onResponseReceived(Object result) {
                            String trailerId = (String) result;
                            if (trailerId == null) {
                                MyUtils.showSnacknar(findViewById(R.id.scrollView1), getResources().getString(R.string.notAviableTrailer));
                            } else {
                                pDialog.dismiss();
                                Intent intent = new Intent(InfoTVShowDatabase.this, YoutubeActivityView.class);
                                intent.putExtra("TrailerId", trailerId);
                                startActivityForResult(intent, 1);
                            }
                            pDialog.dismiss();
                        }
                    };
                    MyUtils.execute(searchorShow);
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
}
