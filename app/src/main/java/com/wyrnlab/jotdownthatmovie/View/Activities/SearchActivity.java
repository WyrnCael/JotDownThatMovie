package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.wyrnlab.jotdownthatmovie.APIS.Analytics.SearchAnalytics;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AsyncResponse {

	String textoABuscar = "";
	String searchMode = "Movie";
	SearchView txtSearchName;
	Button btnSearch;
	final int REQUEST_CODE_LISTABUSCADAS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

		// Back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Obtenemos una referencia a los controles de la interfaz
        txtSearchName = (SearchView) findViewById(R.id.TxtNombre);
        btnSearch = (Button)findViewById(R.id.BtnHola);
		Spinner dropdown = findViewById(R.id.spinner);
		// Dropdown
		String[] items = new String[]{getResources().getString(R.string.Movies), getResources().getString(R.string.TVShows)};
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
		dropdown.setAdapter(adapter);
		dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				if(position == 0) {
					searchMode = "Movie";
				}
				else{
					searchMode = "Show";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});


		txtSearchName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				pulsado(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

        
       //Implementamos el evento “click” del botón
        btnSearch.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	 pulsado(String.valueOf(txtSearchName.getQuery()));
             }
        });

		txtSearchName.setIconified(false);
        txtSearchName.requestFocus();

		if(!CheckInternetConection.isConnectingToInternet(SearchActivity.this)){
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.not_internet));

		} else {
			if(General.base_url == null){
				SearchBaseUrl searchor = new SearchBaseUrl(this);
				MyUtils.execute(searchor);
			}

		}

		isStoragePermissionGranted();
	}
	
	public void pulsado(String query){
		textoABuscar = query;

		if(!CheckInternetConection.isConnectingToInternet(SearchActivity.this)){
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.not_internet));
		} else {
			if(searchMode.equalsIgnoreCase("Movie")){
				com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.Search searchor = new com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.Search(SearchActivity.this);
				searchor.delegate = this;
				searchor.execute(textoABuscar);
			}
			else{
				com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchShow searchor = new com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchShow(SearchActivity.this);
				searchor.delegate = this;
				searchor.execute(textoABuscar);
			}

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
		muestralo(result);
	}
	
	public void muestralo(Object result){

		General.searchResults = new ArrayList<>();

		if(result != null){
			General.setSearchResults((List<AudiovisualInterface>) result);
			// Analytics
			SearchAnalytics analytics = new SearchAnalytics(this, textoABuscar, searchMode, ((List<AudiovisualInterface>) result).size());
			MyUtils.execute(analytics);
		}

		if(result == null){
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.empty_search));
		} else if( ((List<AudiovisualInterface>) result).size() == 0) {
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.without_results));
        } else {
            Intent intent =  new Intent(SearchActivity.this, SearchResultActivity.class);
            intent.putExtra("Type", searchMode);
			intent.putExtra("TextoABuscar", textoABuscar);
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
