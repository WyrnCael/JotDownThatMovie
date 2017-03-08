package com.wyrnlab.jotdownthatmovie.search;

import java.util.ArrayList;
import java.util.List;

import data.General;
import com.wyrnlab.jotdownthatmovie.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import api.search.Pelicula;
 
public class ActivitySearch extends Activity implements
        OnItemClickListener {
 
		ListView listView;
		List<RowItem> rowItems;
		List<Pelicula> peliculas;
		CustomListViewAdapter adapter;
		final int REQUEST_CODE_PELIBUSCADA = 5;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.search_principal);
        
        
        peliculas = General.getPeliculasBuscadas();
        
    	rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < peliculas.size(); i++) {
        	// Insertar imagen
			String img = General.base_url + "w92" +  peliculas.get(i).getImagePath();
            RowItem item;

        	if(peliculas.get(i).getRating() == 0.0)
        		item = new RowItem(1, img, peliculas.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + peliculas.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + getResources().getString(R.string.notavailable)) );
			else
				item = new RowItem(1, img, peliculas.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + peliculas.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + peliculas.get(i).getRating()) );
            rowItems.add(item);
        }
        
 
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListViewAdapter(this,
        		R.layout.list_item, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }
 
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        
    	Intent intent =  new Intent(ActivitySearch.this, InfoMovieSearch.class);        
        intent.putExtra("Pelicula", this.peliculas.get(position));          
		startActivityForResult(intent, REQUEST_CODE_PELIBUSCADA); 	
        
        // finish();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	        case REQUEST_CODE_PELIBUSCADA:
	        	if(resultCode == Activity.RESULT_OK){
	        		setResult(Activity.RESULT_OK);
	        		finish();
	        	}
	            break;
	    }
	}

    @Override
    public void onBackPressed() {
        adapter.clearCache();
        finish();
    }
    
}
