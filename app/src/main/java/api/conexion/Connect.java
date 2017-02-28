package api.conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

import com.eclipsesource.json.JsonObject;

import data.General;

public class Connect extends AsyncTask<String, Integer, Integer> {

	private HttpsURLConnection yc;
	
	@Override
	protected Integer doInBackground(String... params) {
		String web = null;
		
		String url = General.URLPRINCIPAL + "3/configuration?api_key=" + General.APIKEY;
		try {
		URL oracle = new URL(url);
	    
			yc = (HttpsURLConnection) oracle.openConnection();
		
	    String json = "";
		
		yc.setDoOutput(true);
		yc.setDoInput(true);
		yc.setInstanceFollowRedirects(false);
		yc.setRequestMethod("GET");
		yc.setUseCaches (false);	
		yc.setRequestProperty("Accept", "application/json");
		
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) 
            web += inputLine;
        in.close();
        yc.disconnect();        
        
        json = web.substring(4);    
        
        JsonObject respuestaTotal = JsonObject.readFrom( json );
        JsonObject images = respuestaTotal.get("images").asObject();
        General.base_url = images.get("base_url").asString(); 
        
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return 0;
	}
}
