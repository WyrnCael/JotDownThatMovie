package com.wyrnlab.jotdownthatmovie.search;

import java.util.ArrayList;
import java.util.List;

import api.search.AudiovisualInterface;
import data.General;
import com.wyrnlab.jotdownthatmovie.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.ShowInfo.showTVShow.InfoTVShowSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchResultActivity extends AppCompatActivity implements
        OnItemClickListener {
 
		ListView listView;
        String type;
		List<RowItem> rowItems;
		List<AudiovisualInterface> results;
		CustomListViewAdapter adapter;
		final int REQUEST_CODE_PELIBUSCADA = 5;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.search_principal);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type = getIntent().getStringExtra("Type");

        results = (List<AudiovisualInterface>) General.getsSarchResults();
    	rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < results.size(); i++) {
        	// Insertar imagen
			String img = General.base_url + "w92" +  results.get(i).getImagePath();
            RowItem item;

        	if(results.get(i).getRating() == 0.0)
        		item = new RowItem(1, img, results.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + results.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + getResources().getString(R.string.notavailable)), results.get(i).getTipo() );
			else
				item = new RowItem(1, img, results.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + results.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + results.get(i).getRating()), results.get(i).getTipo() );
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

        Intent intent;
        if(type.equalsIgnoreCase("Movie")) {
            intent = new Intent(SearchResultActivity.this, InfoMovieSearch.class);

        } else {
            intent = new Intent(SearchResultActivity.this, InfoTVShowSearch.class);
        }
        intent.putExtra("Pelicula", this.results.get(position));
        intent.putExtra("Type", type);
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
