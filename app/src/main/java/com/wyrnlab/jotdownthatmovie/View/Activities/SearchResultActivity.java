package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.JavaClasses.SaveAudiovisual;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow.InfoTVShowSearch;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.AdapterCallback;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.EndlessRecyclerViewScrollListener;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemDecorationAddHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemTouchAddHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements
        AsyncResponse, AdapterCallback, RecyclerViewClickListener {

    public RecyclerView listView;
    List<RowItemInterface> rowItems;
    List<AudiovisualInterface> results;
    List<AudiovisualInterface> rowsToSave;
    RecyclerViewAdapter adapter;
    int longClickPosition;
    private EndlessRecyclerViewScrollListener scrollListener;
    String searchText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_results);
        
        setContentView(R.layout.search_principal);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchText = getIntent().getStringExtra("TextoABuscar");

        rowsToSave = new ArrayList<AudiovisualInterface>();
        results = General.getsSarchResults();
    	rowItems = new ArrayList<RowItemInterface>();
        for (AudiovisualInterface movie : results) {
        	rowItems.add(new RowItem(SearchResultActivity.this, movie));
        }
        
 
        listView = (RecyclerView) findViewById(R.id.list);
        adapter = new RecyclerViewAdapter(this, (AdapterCallback) this,
        		R.layout.list_item, rowItems, this);
        listView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(linearLayoutManager);
        registerForContextMenu(listView);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.MultiSearch searchor = new com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.MultiSearch(SearchResultActivity.this, page);
                searchor.delegate = SearchResultActivity.this;
                searchor.execute(searchText);
            }
        };
        // Adds the scroll listener to RecyclerView
        listView.addOnScrollListener(scrollListener);

        //Swipe
        ItemTouchAddHelper simpleItemTouchCallback = new ItemTouchAddHelper(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT, SearchResultActivity.this, adapter);
        androidx.recyclerview.widget.ItemTouchHelper mItemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(listView);
        listView.addItemDecoration(new ItemDecorationAddHelper(SearchResultActivity.this));

    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch(requestCode) {
	        case General.REQUEST_CODE_PELIBUSCADA:
	            if (resultCode == General.RESULT_CODE_ADD) {
                    adapter.remove(data.getIntExtra("Position", 0));
                }
	        	if(resultCode == Activity.RESULT_OK){
	        		setResult(Activity.RESULT_OK);
	        		finish();
	        	}
	            break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    public void recylerViewCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(!rowItems.get(position).getType().equalsIgnoreCase(General.PERSON_TYPE)) {
            MenuInflater inflater = getMenuInflater();
            rowItems.get(position).toString();
            inflater.inflate(R.menu.menu_pelicula_busqueda, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.CtxAdd:
                //addItem(item);
                adapter.remove(longClickPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void processFinish(Object result){
        if(result instanceof AudiovisualInterface) {
            if(DAO.getInstance().insert(SearchResultActivity.this, (AudiovisualInterface) result)){
                MyUtils.showSnacknar(listView, ((AudiovisualInterface) result).getTitulo() + " " + getResources().getString(R.string.added));
            } else {
                MyUtils.showSnacknar(listView, ((AudiovisualInterface) result).getTitulo() + " " + getResources().getString(R.string.alreadySaved));
            }
        } else {
            results.addAll((List<AudiovisualInterface>) result);
            for (AudiovisualInterface movie : ((List<AudiovisualInterface>) result)) {
                rowItems.add(new RowItem(SearchResultActivity.this, movie));
            }
            adapter.notifyDataSetChanged();
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

    @Override
    public void recyclerViewListClicked(View v, int position) {
        AudiovisualInterface pelicula = (AudiovisualInterface) ((RowItem)rowItems.get(position)).getObject();
        Context context = v.getContext();
        Intent intent = null;
        if(pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)) {
            intent = new Intent(SearchResultActivity.this, InfoMovieSearch.class);
        } else if (pelicula.getTipo().equalsIgnoreCase(General.TVSHOW_TYPE)) {
            intent = new Intent(SearchResultActivity.this, InfoTVShowSearch.class);
        } else if (pelicula.getTipo().equalsIgnoreCase(General.PERSON_TYPE)) {
            intent = new Intent(SearchResultActivity.this, InfoPersonActivity.class);
        }
        intent.putExtra("Pelicula", pelicula);
        intent.putExtra("Type", pelicula.getTipo());
        intent.putExtra("Position", position);
        startActivityForResult(intent, General.REQUEST_CODE_PELIBUSCADA);
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
        SaveAudiovisual.saveItem(SearchResultActivity.this, SearchResultActivity.this, item, item.getTipo());
    }

    @Override
    public void undoCallback(AudiovisualInterface item) {
        //rowsToSave.remove(item);
    }
}
