package com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.wyrnlab.jotdownthatmovie.R;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonArray;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.json.JsonObject;

import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Model.General;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.Toast;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

public class GetVideoURL extends Activity{

	protected ProgressDialog pDialog;
	protected String key;
	Pelicula pelicula;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent i = getIntent();
        pelicula = (Pelicula)i.getSerializableExtra("Pelicula");
        
        setContentView(R.layout.video_player);        
        
        SearchURLMovie searchorMovie = new SearchURLMovie(this);
		MyUtils.execute(searchorMovie);
	}
	
	public void noEncontrada(){
		this.runOnUiThread(new Runnable() {
			  public void run() {
				  Toast toast = Toast.makeText(getApplicationContext(),
						  getResources().getString(R.string.notAviableTrailer),
			        Toast.LENGTH_SHORT);
			         toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			         toast.show();
			  }
			});
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
            pDialog.setMessage(getResources().getString(R.string.searching));
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
    		
    		WebView mWebView = (WebView) findViewById(R.id.webView);;
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setPluginState(PluginState.ON);
            mWebView.loadUrl("http://www.youtube.com/embed/" + key + "?autoplay=1&vq=small");
            mWebView.setWebChromeClient(new WebChromeClient());
    	}   	
    	
    	
    	private void getURLPelicula() throws IOException{
    		String web = null;

            String url = General.URLPRINCIPAL + "3/movie/" + pelicula.getId() + "/videos?api_key=" + General.APIKEY + "&language=" + SetTheLanguages.getLanguage(Locale.getDefault().getDisplayLanguage());
    		
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
