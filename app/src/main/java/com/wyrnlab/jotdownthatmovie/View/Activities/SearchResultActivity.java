package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.SearchInfoMovie;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.TVShows.SearchInfoShow;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow.InfoTVShowSearch;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.AdapterCallback;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemDecorationAddHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemTouchAddHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.MovieRecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements
        AsyncResponse, AdapterCallback, RecyclerViewClickListener {

    public RecyclerView listView;
    String type;
    List<RowItem> rowItems;
    List<AudiovisualInterface> results;
    List<AudiovisualInterface> rowsToSave;
    MovieRecyclerViewAdapter adapter;
    int longClickPosition;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.search_principal);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type = getIntent().getStringExtra("Type");

        rowsToSave = new ArrayList<AudiovisualInterface>();
        results = (List<AudiovisualInterface>) General.getsSarchResults();
    	rowItems = new ArrayList<RowItem>();
        for (AudiovisualInterface movie : results) {
        	rowItems.add(new RowItem(SearchResultActivity.this, movie));
        }
        
 
        listView = (RecyclerView) findViewById(R.id.list);
        adapter = new MovieRecyclerViewAdapter(this,
        		R.layout.list_item, rowItems, this);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(listView);

        //Swipe
        ItemTouchAddHelper simpleItemTouchCallback = new ItemTouchAddHelper(0, android.support.v7.widget.helper.ItemTouchHelper.LEFT, SearchResultActivity.this);
        android.support.v7.widget.helper.ItemTouchHelper mItemTouchHelper = new android.support.v7.widget.helper.ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(listView);
        listView.addItemDecoration(new ItemDecorationAddHelper(SearchResultActivity.this));

    }
 
    /*@Override
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
        startActivityForResult(intent, General.REQUEST_CODE_PELIBUSCADA);
        
        // finish();
    }*/
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	        case General.REQUEST_CODE_PELIBUSCADA:
	        	if(resultCode == Activity.RESULT_OK){
	        		setResult(Activity.RESULT_OK);
	        		finish();
	        	}
	            break;
	    }
	}

    @Override
    public void recylerViewCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        rowItems.get(position).toString();
        inflater.inflate(R.menu.menu_pelicula_busqueda, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.CtxAdd:
                //addItem(item);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void processFinish(Object result){
        DAO.getInstance().insert(SearchResultActivity.this, (AudiovisualInterface) result);
        String type = ((AudiovisualInterface) result).getTipo()  == General.MOVIE_TYPE ? getResources().getString(R.string.Movie) : getResources().getString(R.string.Show);
        MyUtils.showSnacknar(listView, ((AudiovisualInterface) result).getTitulo() + " " + getResources().getString(R.string.added));
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

    @Override
    public void recyclerViewListClicked(View v, int position) {
        AudiovisualInterface pelicula = (AudiovisualInterface) ((RowItem)rowItems.get(position)).getObject();

        Intent intent;
        if(pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)) {
            intent = new Intent(SearchResultActivity.this, InfoMovieSearch.class);

        } else {
            intent = new Intent(SearchResultActivity.this, InfoTVShowSearch.class);
        }
        intent.putExtra("Pelicula", pelicula);
        intent.putExtra("Type", pelicula.getTipo());
        startActivityForResult(intent, General.REQUEST_CODE_PELIBUSCADA);
    }

    public void addItem(AudiovisualInterface item){
        if(type.equalsIgnoreCase("Movie")) {
            SearchInfoMovie searchorMovie = new SearchInfoMovie(this, item.getId());
            //searchorMovie.position = item;
            searchorMovie.delegate = SearchResultActivity.this;
            MyUtils.execute(searchorMovie);

        } else {
            SearchInfoShow searchorShow = new SearchInfoShow(this, item.getId());
            //searchorShow.position = item;
            searchorShow.delegate = SearchResultActivity.this;
            MyUtils.execute(searchorShow);
        }
    }

    @Override
    public void recyclerViewListLongClicked(View v, int position) {
        longClickPosition = position;
    }

    @Override
    public void swipeCallback(AudiovisualInterface item) {
        //rowsToSave.add(item);
    }

    @Override
    public void removeCallback(AudiovisualInterface item) {
        addItem(item);
    }

    @Override
    public void undoCallback(AudiovisualInterface item) {
        //rowsToSave.remove(item);
    }
}
