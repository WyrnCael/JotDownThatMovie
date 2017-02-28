package pruebas.prueba1.video.alter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import pruebas.prueba1.mostrarPelicula.InfoMovieSearch.SearchInfoMovie;
import pruebas.pureba1.R;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import data.General;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.Toast;
import api.search.Pelicula;

public class GetVideoURLAlter extends Activity{

	protected ProgressDialog pDialog;
	protected String key;
	Pelicula pelicula;
	
	private VideoEnabledWebView webView;
	private VideoEnabledWebChromeClient webChromeClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);

	    Intent i = getIntent();
        pelicula = (Pelicula)i.getSerializableExtra("Pelicula");
        
        SearchURLMovie searchorMovie = new SearchURLMovie(this);
        searchorMovie.execute();
	    
	    
	    // Set layout
	    setContentView(R.layout.video_player_alter);

	    // Save the web view
	    webView = (VideoEnabledWebView) findViewById(R.id.webView);
	}
	
	public void noEncontrada(){
		this.runOnUiThread(new Runnable() {
			  public void run() {
				  Toast toast = Toast.makeText(getApplicationContext(),
			        "Trailer no disponible",
			        Toast.LENGTH_SHORT);
			         toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			         toast.show();
			  }
			});	    
	}
	
	public void encontrada(){
		// Initialize the VideoEnabledWebChromeClient and set event handlers
	    View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
	    ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
	    View loadingView = getLayoutInflater().inflate(R.layout.video_player_alter, null); // Your own view, read class comments
	    webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
	    {
	        // Subscribe to standard events, such as onProgressChanged()...
	        @Override
	        public void onProgressChanged(WebView view, int progress)
	        {
	            // Your code...
	        }
	    };
	    webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
	    {
	        @Override
	        public void toggledFullscreen(boolean fullscreen)
	        {
	            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
	            if (fullscreen)
	            {
	                WindowManager.LayoutParams attrs = getWindow().getAttributes();
	                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
	                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
	                getWindow().setAttributes(attrs);
	                if (android.os.Build.VERSION.SDK_INT >= 14)
	                {
	                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	                }
	            }
	            else
	            {
	                WindowManager.LayoutParams attrs = getWindow().getAttributes();
	                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
	                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
	                getWindow().setAttributes(attrs);
	                if (android.os.Build.VERSION.SDK_INT >= 14)
	                {
	                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
	                }
	            }

	        }
	    });
	    webView.setWebChromeClient(webChromeClient);

	    // Navigate everywhere you want, this classes have only been tested on YouTube's mobile site
	    webView.loadUrl("http://www.youtube.com/embed/" + key + "?autoplay=1&vq=small");
	}
	
	@Override
	public void onBackPressed()
	{
	    // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
	    if (!webChromeClient.onBackPressed())
	    {
	        if (webView.canGoBack())
	        {
	            webView.goBack();
	        }
	        else
	        {
	            // Close app (presumably)
	            super.onBackPressed();
	        }
	    }
	}
	
	public class SearchURLMovie extends AsyncTask<String, Integer, List<Pelicula>> {

    	private HttpsURLConnection yc;
    	Context context;
    	
    	public SearchURLMovie(Context context){
    		this.context = context;
    	}   	
    	
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Buscando");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();	             
        }    	

    	@Override
    	protected List<Pelicula> doInBackground(String... params) {
    		try {
    			getURLPelicula();	
    		} catch (IOException e) {
    			
    		}
    		return null;
    		
    	}
    	
    	@Override
    	protected void onPostExecute(List<Pelicula> result)
    	{
    		super.onPostExecute(result);
    		pDialog.dismiss();
    		encontrada();
    	}   	
    	
    	
    	private void getURLPelicula() throws IOException{
    		String web = null;

            String url = General.URLPRINCIPAL + "3/movie/" + pelicula.getId() + "/videos?api_key=" + General.APIKEY + "&language=es";
    		
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
            
            leerJSONUrl(json);
    	}
    	
    	private void leerJSONUrl(String json) throws IOException{
    		key = null;
    		
    		System.out.println(json);
    		
    		JsonObject info = JsonObject.readFrom( json );     
    		JsonArray results = info.get("results").asArray();
    		if(!results.isNull()){
    			if(results.size() > 0){
		    		for (int i = 0; i < 1 ; i++){
		    			JsonObject video = results.get(i).asObject();
		    			key = video.get("key").asString();
		    		}
	    		} 
    			else{
    				noEncontrada();
    				finish();
    			}
    		}
    		else{
    			noEncontrada();
    			finish();
    		}
    	}
	}
}
