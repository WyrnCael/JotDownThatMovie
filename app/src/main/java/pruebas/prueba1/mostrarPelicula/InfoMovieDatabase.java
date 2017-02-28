package pruebas.prueba1.mostrarPelicula;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fedorvlasov.lazylist.ImageLoader;

import data.General;
import pruebas.prueba1.AnadirPelicula;
import pruebas.prueba1.AnadirPelicula.Search;
import pruebas.prueba1.sql.PeliculasSQLiteHelper;
import pruebas.prueba1.video.GetVideoURL;
import pruebas.prueba1.video.alter.GetVideoURLAlter;
import pruebas.pureba1.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
           	 Intent intent =  new Intent(InfoMovieDatabase.this, GetVideoURLAlter.class);        
                intent.putExtra("Pelicula", pelicula);  
                startActivityForResult(intent, 1);           	 
            }
       });
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  //setContentView(R.layout.movie_info_db);
	}
}
