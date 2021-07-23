package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelMultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelSearchMultiSearch;
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
	Spinner dropdown;
	final int REQUEST_CODE_LISTABUSCADAS = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_activity_saludo);
        setContentView(R.layout.search);

		// Back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		//Obtenemos una referencia a los controles de la interfaz
        txtSearchName = (SearchView) findViewById(R.id.TxtNombre);
        btnSearch = (Button)findViewById(R.id.BtnHola);
		dropdown = findViewById(R.id.spinner);
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

		MyUtils.checkInternetConectionAndStoragePermission(SearchActivity.this);
		MyUtils.getGeneralURL(SearchActivity.this);
	}
	
	public void pulsado(String query){
		textoABuscar = query;

		if(!CheckInternetConection.isConnectingToInternet(SearchActivity.this)){
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.not_internet));
		} else if (textoABuscar == null || query.length() == 0){
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.empty_search));
		} else {
			if(searchMode.equalsIgnoreCase("Movie")){
				com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.MultiSearch searchor = new com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.MultiSearch(SearchActivity.this, null);
				searchor.delegate = this;
				searchor.execute(textoABuscar);
			}
			else{
				com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchShow searchor = new com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchShow(SearchActivity.this, null);
				searchor.delegate = this;
				searchor.execute(textoABuscar);
			}

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
		muestralo((ModelSearchMultiSearch) result);
	}
	
	public void muestralo(ModelSearchMultiSearch result){

		General.searchResults = new ModelSearchMultiSearch();

		if(result != null){
			General.setSearchResults((ModelSearchMultiSearch) result);
		}

		if(result == null){
			MyUtils.showSnacknar(findViewById(R.id.LinearLayout1), getResources().getString(R.string.empty_search));
		} else if(result.results.length == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);

			final String searchType = searchMode.equalsIgnoreCase("Movie") ? getString(R.string.Movies) : getString(R.string.TVShows);
			String otherType = searchMode.equalsIgnoreCase("Movie") ? getString(R.string.TVShows) : getString(R.string.Movies);

			builder.setTitle(getString(R.string.Noresults) + " " + searchType + " " + getString(R.string.For)+ " '" + textoABuscar + "'");
			builder.setMessage(getString(R.string.Trywith) + " " + otherType + "?");
			builder.setNegativeButton(getString(R.string.Cancel), null);
			builder.setPositiveButton(getString(R.string.Search), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(searchMode.equalsIgnoreCase("Movie")){
						searchMode = "Show";
						dropdown.setSelection(1);
					} else {
						searchMode = "Movie";
						dropdown.setSelection(0);
					}
					pulsado(String.valueOf(txtSearchName.getQuery()));
				}
			});
			builder.show();
        } else {
            Intent intent =  new Intent(SearchActivity.this, SearchResultActivity.class);
            intent.putExtra("Type", searchMode);
			intent.putExtra("TextoABuscar", textoABuscar);
            startActivityForResult(intent, REQUEST_CODE_LISTABUSCADAS);
        }

		
		// finish();		
		
	}

	@Override
	public void onBackPressed()
	{
		setResult(General.RESULT_CODE_NEEDS_REFRESH);
		super.onBackPressed();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	        case REQUEST_CODE_LISTABUSCADAS:
	        	if(resultCode == Activity.RESULT_OK){
	        		setResult(General.RESULT_CODE_NEEDS_REFRESH);
	        		finish();
	        	}
		        
	            break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
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
