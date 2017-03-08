package com.wyrnlab.jotdownthatmovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonObject;

import data.General;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.ReadExternalStorage;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.WriteExternalStorage;
import com.wyrnlab.jotdownthatmovie.sql.PeliculasSQLiteHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import api.search.Pelicula;
import data.SetTheLanguages;

public class MainActivity extends Activity {

	private ListView mainListView;
	private PeliculasSQLiteHelper usdbh;
	public final static int REQUEST_CODE_A = 1;
	private ArrayAdaptado listAdapter;
	private DrawerLayout NavDrawerLayout;
	private ListView NavList;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_peliculas);

		// Vaciar cache imagenes
		ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.clearCache();

		// Solicitar permisos
		ReadExternalStorage permRead = new ReadExternalStorage(this);
		permRead.getPermissions();
		WriteExternalStorage permWrite = new WriteExternalStorage(this);
		permWrite.getPermissions();

        SearchBaseUrl searchor = new SearchBaseUrl(this);
        searchor.execute();

        NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//Lista
        NavList = (ListView) findViewById(R.id.lista);


        //Localizar los controles
        mainListView = (ListView) findViewById( R.id.mainListView );

        refreshList();

    }

	private void refreshList(){
		String[] planets = new String[] { getResources().getString(R.string.addMovie) };
    	ArrayList<String> planetList = new ArrayList<String>();  
        planetList.addAll( Arrays.asList(planets) );      	
     
        // Create ArrayAdapter using the planet list.  
        listAdapter = new ArrayAdaptado(this, R.layout.simplerow, planetList); 
        


        usdbh = new PeliculasSQLiteHelper(this, "DBPeliculas", null, 1);
		
		SQLiteDatabase dba = usdbh.getWritableDatabase();        
        Cursor c = dba.rawQuery(" SELECT nombre, anyo FROM Peliculas ", null);
        
        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
             //Recorremos el cursor hasta que no haya más registros
        	
        	do {
            	 String nombre = c.getString(0);
                  String anyo  = c.getString(1);
                  listAdapter.add(nombre + " (" + anyo + ")\n");
                  
             } while(c.moveToNext());
        }
        
        dba.close();
        	
        // Set the ArrayAdapter as the ListView's adapter.  
         mainListView.setAdapter( listAdapter );   
         
         registerForContextMenu(mainListView);
         
         mainListView.setOnItemClickListener(new OnItemClickListener() {
        	 @Override
        	 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        		 if (position == 0){
            		 Intent intent =  new Intent(MainActivity.this, AnadirPelicula.class);
            		 startActivityForResult(intent, REQUEST_CODE_A);  
            	 }
        		 else{        		 
	        		Intent intent =  new Intent(MainActivity.this, InfoMovieDatabase.class);
	            	Pelicula pelicula = readFromSQL(position);
			        intent.putExtra("Pelicula", pelicula);          
					startActivity(intent);
        		 }
        	 }    
         });
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo)
	{
	    super.onCreateContextMenu(menu, v, menuInfo);
	 
	    MenuInflater inflater = getMenuInflater();
	 
	    AdapterView.AdapterContextMenuInfo info =
		            (AdapterView.AdapterContextMenuInfo)menuInfo;
	    
	    // Si no es añadir
	    if (info.position != 0){
		    menu.setHeaderTitle(mainListView.getAdapter().getItem(info.position).toString());
			inflater.inflate(R.menu.menu_pelicula_lista, menu);
	    }
	   
	}
	
	private void delete(String nombre, String anyo){
		//Abrimos la base de datos 'DBUsuarios' en modo escritura
		usdbh = new PeliculasSQLiteHelper(this, "DBPeliculas", null, 1);
 
        SQLiteDatabase db = usdbh.getWritableDatabase();
 
        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
           //Insertamos los datos en la tabla Peliculas
            db.execSQL("DELETE FROM Peliculas WHERE nombre='" + nombre + "' AND anyo=" + anyo);
             
            //Cerramos la base de datos  
            db.close();          
        }  
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	        case REQUEST_CODE_A:
	        	refreshList();
	            break;
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	 
	    AdapterContextMenuInfo info =
	        (AdapterContextMenuInfo) item.getMenuInfo();

		 switch (item.getItemId()) {
	        case R.id.CtxLstOpc2:


	        	String[] selected = textParser(listAdapter.getItem(info.position).toString());
	        	
	        	delete(selected[0], selected[1]);
	        	
	        	Toast.makeText(getApplicationContext(), "Pelicula " + selected[0] + " eliminada!", Toast.LENGTH_SHORT).show();
	        	
	        	refreshList();
	        	
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public String[] textParser(String cadena){
		int posicionParentesis = cadena.indexOf("(");
		int posicionUltimoParentesis = cadena.indexOf(")");
		String[] ret = new String[2];
		ret[0] = cadena.substring(0, (posicionParentesis-1));
		ret[1] = cadena.substring((posicionParentesis+1), (posicionUltimoParentesis));
		
		return ret;
		
	}
	
	public Pelicula readFromSQL(int position){
		String[] campos = new String[] {"filmId", "nombre", "anyo", "titulo", "tituloOriginal", "descripcion", "image", "directores", "generos", "rating" };
		String[] args = new String[]  { Integer.toString(position) };
		
		Pelicula pelicula = new Pelicula();
		
		PeliculasSQLiteHelper usdbh = new PeliculasSQLiteHelper(this, "DBPeliculas", null, 1);		 
        SQLiteDatabase db = usdbh.getWritableDatabase();
		
		Cursor c = db.query("Peliculas", campos, null, null, null, null, null);
		 
		//Nos aseguramos de que existe al menos un registro
		int pos = 1;
		if (c.moveToFirst()) {
		     //Recorremos el cursor hasta que no haya más registros
		     do {
		    	 if (pos == position){
			          pelicula.setId(Integer.parseInt(c.getString(0)));
			          pelicula.setAnyo(c.getString(2));
			          pelicula.setTitulo(c.getString(3));
			          pelicula.setTituloOriginal(c.getString(4));
			          pelicula.setDescripcion(c.getString(5));
					  pelicula.setImage(c.getBlob(6));
			          pelicula.addDirectores(c.getString(7));
			          pelicula.addGeneros(c.getString(8));
			          pelicula.setRating(Double.parseDouble(c.getString(9)));
		    	 }
		    	 pos++;
		     } while(c.moveToNext());
		}
		
		return pelicula;
	}
	
	public class SearchBaseUrl extends AsyncTask<String, Integer, List<Pelicula>> {

    	private HttpsURLConnection yc;
    	Context context;
    	
    	public SearchBaseUrl(Context context){
    		this.context = context;
    	}   	
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
        }    	

    	@Override
    	protected List<Pelicula> doInBackground(String... params) {
    		getBaseUrl();
    		return null;
    		
    	}
    	
    	@Override
    	protected void onPostExecute(List<Pelicula> result)
    	{
    		super.onPostExecute(result);
    	}   
    	
    	private void getBaseUrl() {
			String web = null;
			
			String url = General.URLPRINCIPAL + "3/configuration?api_key=" + General.APIKEY;
			try {
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
	        
	        JsonObject respuestaTotal = JsonObject.readFrom( json );
	        JsonObject images = respuestaTotal.get("images").asObject();
			General.base_url = images.get("base_url").asString();
	        
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
	
	private class ArrayAdaptado extends ArrayAdapter<String>{
		public ArrayAdaptado(Context context, int lay, List<String> objects) {
		    super(context, lay, objects);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
		    View v = super.getView(position, convertView, parent);
		    if(position == 0){
		    	v.setBackgroundColor(Color.rgb(183, 214, 171));
		    }
		    return v;

		}
	}
}
