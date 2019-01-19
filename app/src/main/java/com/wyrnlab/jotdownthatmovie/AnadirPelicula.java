package com.wyrnlab.jotdownthatmovie;

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

import api.conexion.SearchBaseUrl;
import api.search.AsyncResponse;
import api.search.Search;
import data.General;
import com.wyrnlab.jotdownthatmovie.search.ActivitySearch;
import com.wyrnlab.jotdownthatmovie.search.CheckInternetConection;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import api.search.Pelicula;
import data.SetTheLanguages;

public class AnadirPelicula extends AppCompatActivity implements AsyncResponse {

	String textoABuscar = "";
	EditText txtNombre;
	Button btnHola;
	final int REQUEST_CODE_LISTABUSCADAS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// Back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
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

		if(!CheckInternetConection.isConnectingToInternet(AnadirPelicula.this)){
			Toast toast = Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.not_internet),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		} else {
			if(General.base_url == null){
				SearchBaseUrl searchor = new SearchBaseUrl(this);
				searchor.execute();
			}

		}

		isStoragePermissionGranted();
	}
	
	public void pulsado(){
		textoABuscar = txtNombre.getText().toString();

		if(!CheckInternetConection.isConnectingToInternet(AnadirPelicula.this)){
            Toast toast = Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.not_internet),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		} else {
            Search searchor = new Search(AnadirPelicula.this);
			searchor.delegate = this;
			searchor.execute(textoABuscar);
		}
	}

	public  boolean isStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {
				Log.v("TAG","Permission is granted");
				return true;
			} else {

				Log.v("TAG","Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				return false;
			}
		}
		else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG","Permission is granted");
			return true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
			Log.v("TAG","Permission: "+permissions[0]+ "was "+grantResults[0]);
			//resume tasks needing this permission
		}
	}

	//this override the implemented method from asyncTask
	@Override
	public void processFinish(Object result){
		muestralo((List<Pelicula>) result);
	}
	
	public void muestralo(List<Pelicula> result){
		
		General.peliculasBuscadas = new ArrayList<Pelicula>();
		if(result != null) General.setPeliculasBuscadas(result);

		if(result == null){
			Toast toast = Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.empty_search),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		} else if(result.size() == 0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.without_results),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } else {
            Intent intent =  new Intent(AnadirPelicula.this, ActivitySearch.class);
            startActivityForResult(intent, REQUEST_CODE_LISTABUSCADAS);
        }

		
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
