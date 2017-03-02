package WyrnLab.JotDownThatMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import data.General;
import WyrnLab.JotDownThatMovie.search.ActivitySearch;
import WyrnLab.pureba1.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import api.search.Pelicula;

public class AnadirPelicula extends Activity {
	
	String textoABuscar = "";
	EditText txtNombre;
	Button btnHola;
	ProgressDialog pDialog;
	final int REQUEST_CODE_LISTABUSCADAS = 1;
	Pelicula pelicula;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Obtenemos una referencia a los controles de la interfaz
        txtNombre = (EditText)findViewById(R.id.TxtNombre);
        btnHola = (Button)findViewById(R.id.BtnHola);
        
        //Implementamos el evento “click” del botón
        txtNombre.setOnKeyListener(new View.OnKeyListener() {
             @Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
            	 if (event.getAction() == KeyEvent.ACTION_DOWN)
                 {
                     switch (keyCode)
                     {
                         case KeyEvent.KEYCODE_DPAD_CENTER:
                         case KeyEvent.KEYCODE_ENTER:
                             pulsado();
                             return true;
                         default:
                             break;
                     }
                 }
                 return false;
			}
        });
        
        txtNombre.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                   pulsado();
                    return true;
                }
                return false;
            }
        });
        
        
       //Implementamos el evento “click” del botón
        btnHola.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {            	 
            	 pulsado();            	 
             }
        });
        
        txtNombre.requestFocus();
	}
	
	public void pulsado(){
		textoABuscar = txtNombre.getText().toString();
   	 
        Search searchor = new Search(AnadirPelicula.this);
         searchor.execute(textoABuscar);
	}
	
	public void muestralo(List<Pelicula> result){
		
		General.peliculasBuscadas = new ArrayList<Pelicula>();
		General.peliculasBuscadas = result;
		
		Intent intent =  new Intent(AnadirPelicula.this, ActivitySearch.class);
		
		pDialog.dismiss();  
		
		startActivityForResult(intent, REQUEST_CODE_LISTABUSCADAS); 		       
		
		// finish();		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	        case REQUEST_CODE_LISTABUSCADAS:
	        	if(resultCode == Activity.RESULT_OK){
	        		finish();
	        	}
		        
	            break;
	    }
	}

	public class Search extends AsyncTask<String, Integer, List<Pelicula>> {

    	private List<Pelicula> peliculas;
    	private HttpsURLConnection yc;
    	Context context;
    	
    	public Search(Context context){
    		this.peliculas = new ArrayList<Pelicula>();
    		this.context = context;
    	}   	
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.addMovie));
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();	             
        }
    	
    	public List<Pelicula> buscar(String nombre)  throws IOException {
    		String web = null;
    		
    		@SuppressWarnings("deprecation")
    		String url = General.URLPRINCIPAL + "3/search/movie?api_key=" + General.APIKEY + "&language=" + Locale.getDefault().getDisplayLanguage() + "&query=" + URLEncoder.encode(nombre) ;

    		URL oracle = new URL(url);
    	    yc = (HttpsURLConnection) oracle.openConnection();
    	    String json = "";
    	    
    		//yc.setDoOutput(true);
    		yc.setDoInput(true);
    		yc.setInstanceFollowRedirects(false);
    		yc.setRequestMethod("GET");
    		//yc.setUseCaches (false);	
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
            
            leerJSONBuscar(json);            
            
           return this.peliculas;
    	}
    	
    	private void leerJSONBuscar(String json) throws IOException{
    		JsonObject respuestaTotal = JsonObject.readFrom( json );
    		JsonArray res = respuestaTotal.get("results").asArray();
    		for (int i = 0; i < res.size() ; i++){  
    			pelicula = new Pelicula();
            	JsonObject results = JsonObject.readFrom(res.get(i).toString());
            	pelicula.setTituloOriginal(results.get("original_title").asString());
            	pelicula.setTitulo(results.get("title").asString());
            	pelicula.setId(results.get("id").asInt());
            	if(!results.get("release_date").isNull()){            		
            		// Recortar año
                	String an = results.get("release_date").asString();
                	String anyo;
                	System.out.println(an);
                	if (an.length() > 0){
        	        	anyo = an.substring(0, 4);
                	}
                	else{
                		anyo = "N/D";
                	}     
            		pelicula.setAnyo(anyo);
            	}
            	if(results.get("poster_path").isString()){
            		pelicula.setImagePath(results.get("poster_path").asString());
            	}
            	else{
            		getOtrosPosters();
            	}
            	pelicula.setRating(results.get("vote_average").asDouble());
            	this.peliculas.add(pelicula);            	
    		}
    	}

    	@Override
    	protected List<Pelicula> doInBackground(String... params) {
    		String texto = params[0];
    		List<Pelicula> devolver = new ArrayList<Pelicula>();
    		try {
    			devolver = buscar(texto);			
    		} catch (IOException e) {
    			
    		}
    		return devolver;
    		
    	}
    	
    	@Override
    	protected void onPostExecute(List<Pelicula> result)
    	{
    		super.onPostExecute(result);    		
            muestralo(result);
    	}   	
    	
    	
    	
    	private void getOtrosPosters() throws IOException{
    		String web = null;
    		
    		String url = General.URLPRINCIPAL + "/3/movie/" + pelicula.getId() + "/images?api_key=" + General.APIKEY;
    		
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
            
            leerJSONOtrosPosters(json);
    	}
    	
    	private void leerJSONOtrosPosters(String json) throws IOException{
    		JsonObject info = JsonObject.readFrom( json ); 
    		JsonArray aux = info.get("posters").asArray();
    		if(aux.size() > 0){
    			JsonObject poster = aux.get(0).asObject();
    			pelicula.setImagePath(poster.get("file_path").asString());
    		}
    	}
    }
}
