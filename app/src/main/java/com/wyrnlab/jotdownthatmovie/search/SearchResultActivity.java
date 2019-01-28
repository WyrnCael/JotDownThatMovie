package com.wyrnlab.jotdownthatmovie.search;

import java.util.ArrayList;
import java.util.List;

import com.wyrnlab.jotdownthatmovie.Activities.MainActivity;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.api.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.api.search.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.api.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.api.search.TVShows.SearchInfoShow;
import com.wyrnlab.jotdownthatmovie.data.General;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.showTVShow.InfoTVShowSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResultActivity extends AppCompatActivity implements
        OnItemClickListener, AsyncResponse {
 
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
        registerForContextMenu(listView);

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
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)menuInfo;

        // Si no es añadir
        menu.setHeaderTitle(listView.getAdapter().getItem(info.position).toString());
        inflater.inflate(R.menu.menu_pelicula_busqueda, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.CtxAdd:
                AudiovisualInterface selected = General.getsSarchResults().get(info.position);

                if(type.equalsIgnoreCase("Movie")) {
                    SearchInfoMovie searchorMovie = new SearchInfoMovie(this, selected.getId());
                    searchorMovie.delegate = SearchResultActivity.this;
                    searchorMovie.execute();

                } else {
                    SearchInfoShow searchorShow = new SearchInfoShow(this, selected.getId());
                    searchorShow.delegate = SearchResultActivity.this;
                    searchorShow.execute();
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void processFinish(Object result){
        DAO.getInstance().insert(SearchResultActivity.this, (AudiovisualInterface) result);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Movie) + " \"" + ((AudiovisualInterface) result).getTitulo() + "\" " + getResources().getString(R.string.added) + "!", Toast.LENGTH_SHORT).show();
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
