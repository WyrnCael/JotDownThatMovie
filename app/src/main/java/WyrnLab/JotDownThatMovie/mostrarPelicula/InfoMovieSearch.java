package WyrnLab.JotDownThatMovie.mostrarPelicula;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fedorvlasov.lazylist.ImageLoader;

import data.General;
import WyrnLab.JotDownThatMovie.sql.PeliculasSQLiteHelper;
import WyrnLab.JotDownThatMovie.video.alter.GetVideoURLAlter;
import WyrnLab.pureba1.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import api.search.Pelicula;

public class InfoMovieSearch extends Activity {

	ProgressDialog pDialog;
	Pelicula pelicula;
	TextView descripcion;
	TextView genero;
	TextView director;
	TextView generoLab;
	TextView directorLab;
	Button botonAnadir;
	Button botonVolver;
	Button botonTrailer;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        pelicula = (Pelicula)i.getSerializableExtra("Pelicula");
        
        SearchInfoMovie searchorMovie = new SearchInfoMovie(this);
        searchorMovie.execute();
        
        setContentView(R.layout.movie_info);
        
      //Obtenemos una referencia a los controles de la interfaz
        TextView titulo = (TextView)findViewById(R.id.titulo);
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
 
        titulo.setText(pelicula.getTitulo());
        anyo.setText("	" + pelicula.getAnyo());
        
        //Implementamos el evento “click” del botón
        botonAnadir.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	 
            	 insert(pelicula);
            	 
            	 Toast toast = Toast.makeText(getApplicationContext(),
						 getResources().getString(R.string.film) + " " + pelicula.getTitulo() + " " + getResources().getString(R.string.added) + "!",
                 Toast.LENGTH_SHORT);
		         toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		         toast.show();      
		         
		         setResult(Activity.RESULT_OK);
		         finish();
             }
        });
        
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
           	 Intent intent =  new Intent(InfoMovieSearch.this, GetVideoURLAlter.class);        
                intent.putExtra("Pelicula", pelicula);  
                startActivityForResult(intent, 1);           	 
            }
       });
        
        
        valoracion.setText("	" + Double.toString(pelicula.getRating()));
        
        ImageView image = (ImageView)findViewById(R.id.poster);
        ImageLoader imageLoader = new ImageLoader(this);
        imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
    }
	
	public void actualiza(){
		// Añadir generos
        String gene = "";
        for (int j = 0; j < pelicula.getGeneros().size() ; j++){
        	if ( j > 0){
        		gene += ", " + pelicula.getGeneros().get(j).toLowerCase();
        		generoLab.setText(getResources().getString(R.string.genders));
        	}
        	else gene += pelicula.getGeneros().get(j);        	
        }
        genero.setText("	" + gene);

        //Añadir directores
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
	
	public class SearchInfoMovie extends AsyncTask<String, Integer, List<Pelicula>> {

    	private HttpsURLConnection yc;
    	Context context;
    	
    	public SearchInfoMovie(Context context){
    		this.context = context;
    	}   	
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.searching));
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();	             
        }    	

    	@Override
    	protected List<Pelicula> doInBackground(String... params) {
    		try {
    			getSinopsisPelicula();	
    			getCreditsPelicula();
    		} catch (IOException e) {
    			
    		}
    		return null;
    		
    	}
    	
    	@Override
    	protected void onPostExecute(List<Pelicula> result)
    	{
    		super.onPostExecute(result);
    		pDialog.dismiss();
    		actualiza();
    	}   	
    	
    	
    	
    	private void getSinopsisPelicula() throws IOException{
    		String web = null;
    		
    		String url = General.URLPRINCIPAL + "3/movie/" + pelicula.getId() + "?api_key=" + General.APIKEY + "&language=" + Locale.getDefault().getDisplayLanguage();
    		
    		URL oracle = new URL(url);
    	    yc = (HttpsURLConnection) oracle.openConnection();
    	    String json = "";
    		
    		//yc.setDoOutput(true);
    		yc.setDoInput(true);
    		yc.setInstanceFollowRedirects(false);
    		yc.setRequestMethod("GET");
    		//yc.setUseCaches (true);	
    		yc.setRequestProperty("Accept", "application/json");
    		
    		yc.connect();
    		
    		InputStream is = null;
    		try {
    		    is = yc.getInputStream();
    		} catch (IOException ioe) {
    		    if (yc instanceof HttpsURLConnection) {
    		        HttpsURLConnection httpConn = (HttpsURLConnection) yc;
    		        int statusCode = httpConn.getResponseCode();
    		        if (statusCode != 200) {
    		            is = httpConn.getErrorStream();
    		        }
    		    }
    		}
    		
    		InputStreamReader isReader = new InputStreamReader(is); 
    		//put output stream into a string
    		BufferedReader br = new BufferedReader(isReader );
            String inputLine;
            while ((inputLine = br.readLine()) != null) 
                web += inputLine;
            br.close();
            yc.disconnect();      
            
            yc.disconnect();
            
            json = web.substring(4);    
            
            leerJSONSinopsis(json);
    	}
    	
    	private void leerJSONSinopsis(String json) throws IOException{
    		JsonObject info = JsonObject.readFrom( json );     
    		if(info.get("overview").isNull()){
    			pelicula.setDescripcion("");
    		}
    		else{
    			pelicula.setDescripcion(info.get("overview").asString());
    		}
    		
    		JsonArray aux = info.get("genres").asArray();
    		for (int i = 0; i < aux.size() ; i++){
    			JsonObject genero = aux.get(i).asObject();
    			pelicula.addGeneros(genero.get("name").asString());
    		}
    	}
    	
    	private void getCreditsPelicula() throws IOException{
    		String web = null;
    		
    		String url = General.URLPRINCIPAL + "3/movie/" + pelicula.getId() + "/credits?api_key=" + General.APIKEY + "&language=" + Locale.getDefault().getDisplayLanguage();
    		
    		URL oracle = new URL(url);
    	    yc = (HttpsURLConnection) oracle.openConnection();
    	    String json = "";
    		
    		//yc.setDoOutput(true);
    		yc.setDoInput(true);
    		yc.setInstanceFollowRedirects(false);
    		yc.setRequestMethod("GET");
    		//yc.setUseCaches (true);	
    		yc.setRequestProperty("Accept", "application/json");
    		
    		yc.connect();
    		
    		InputStream is = null;
    		try {
    		    is = yc.getInputStream();
    		} catch (IOException ioe) {
    		    if (yc instanceof HttpsURLConnection) {
    		        HttpsURLConnection httpConn = (HttpsURLConnection) yc;
    		        int statusCode = httpConn.getResponseCode();
    		        if (statusCode != 200) {
    		            is = httpConn.getErrorStream();
    		        }
    		    }
    		}
    		
    		InputStreamReader isReader = new InputStreamReader(is); 
    		//put output stream into a string
    		BufferedReader br = new BufferedReader(isReader );
            String inputLine;
            while ((inputLine = br.readLine()) != null) 
                web += inputLine;
            br.close();
            yc.disconnect();      
            
            yc.disconnect();
            
            json = web.substring(4);    
            
            leerJSONCredits(json);
    	}
    	
    	private void leerJSONCredits(String json) throws IOException{
    		JsonObject info = JsonObject.readFrom( json );    
    		JsonArray aux = info.get("crew").asArray();
    		String[] directores = new String[aux.size()];    		
    		for (int i = 0; i < aux.size() ; i++){
    			JsonObject person = aux.get(i).asObject();
    			try{
	    			if (person.get("job").asString().equalsIgnoreCase("Director")){
	    				pelicula.addDirectores(person.get("name").asString());
	    			}
    			} catch (NullPointerException e){
    				
    			}
    		}
    	}
    }
	
	 private void insert(Pelicula pelicula){
			//Abrimos la base de datos 'DBUsuarios' en modo escritura
			PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(this, "DBPeliculas", null, 1);
	 
	        SQLiteDatabase db = usdbh.getWritableDatabase();
	 
	        //Si hemos abierto correctamente la base de datos
	        if(db != null)
	        {
	           //Generamos los datos
	        	String id = Integer.toString(pelicula.getId());
	            String nombre = pelicula.getTitulo();
	            String anyo = pelicula.getAnyo();
	            String titulo = pelicula.getTitulo();
	            String tituloOriginal = pelicula.getTituloOriginal();
	            String descripcion = pelicula.getDescripcion();
	            String imagePath = pelicula.getImagePath();
	            
	            String directores = "";
	            if(pelicula.getDirectores().size() > 0){
	            	directores = pelicula.getDirectores().get(0);
	            	for (int i = 1; i < pelicula.getDirectores().size() ; i++){
	            		if (i > 0){
	            			directores += ", " + pelicula.getDirectores().get(i);
	            		}
	            		else directores += pelicula.getDirectores().get(i);
	            	}
	            }   
	            
	            String generos = "";
	            if(pelicula.getGeneros().size() > 0){
	            	generos = pelicula.getGeneros().get(0);
	            	for (int i = 1; i < pelicula.getGeneros().size() ; i++){
	            		if (i>0){
	            			generos += ", " + pelicula.getGeneros().get(i).toLowerCase();
	            		}
	            		else generos += pelicula.getGeneros().get(i);
	            	}
	            }   
	            
	            String rating = Double.toString(pelicula.getRating());
	            
	            
	            //Insertamos los datos en la tabla Peliculas
	            db.execSQL("INSERT INTO Peliculas (filmId, nombre, anyo, titulo, tituloOriginal, descripcion, imagePath, directores, generos, rating) " +
	                       "VALUES ('" + id + "', '" + nombre + "', '" + anyo + "', '" + titulo + "', '" + tituloOriginal + "', '" + descripcion + "', '" + imagePath + "', '" + directores + "', '" + generos + "', '" + rating + "')");
	             
	            //Cerramos la base de datos  
	            db.close();          
	        } 
		}
}
