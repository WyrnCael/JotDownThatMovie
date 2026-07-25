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

import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.conexion.SearchBaseUrl;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Movies.GetUpcomingMovies;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.JavaClasses.SaveAudiovisual;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.AdapterCallback;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.EndlessRecyclerViewScrollListener;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemDecorationAddHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemTouchAddHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class UpcomingActivity extends AppCompatActivity implements
        AsyncResponse, AdapterCallback, RecyclerViewClickListener {

    public RecyclerView listView;
    List<RowItemInterface> rowItems;
    List<AudiovisualInterface> results;
    RecyclerViewAdapter adapter;
    int longClickPosition;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_upcoming);

        setContentView(R.layout.search_principal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        results = new ArrayList<AudiovisualInterface>();
        rowItems = new ArrayList<RowItemInterface>();

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
                GetUpcomingMovies task = new GetUpcomingMovies(UpcomingActivity.this, page);
                task.delegate = UpcomingActivity.this;
                task.execute();
            }
        };
        listView.addOnScrollListener(scrollListener);

        //Swipe
        ItemTouchAddHelper simpleItemTouchCallback = new ItemTouchAddHelper(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT, UpcomingActivity.this, adapter);
        androidx.recyclerview.widget.ItemTouchHelper mItemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(listView);
        listView.addItemDecoration(new ItemDecorationAddHelper(UpcomingActivity.this));

        if (General.base_url == null) {
            SearchBaseUrl searchBaseUrl = new SearchBaseUrl(UpcomingActivity.this) {
                @Override
                public void onResponseReceived(Object result) {
                    fetchFirstPage();
                }
            };
            MyUtils.execute(searchBaseUrl);
        } else {
            fetchFirstPage();
        }
    }

    private void fetchFirstPage() {
        GetUpcomingMovies task = new GetUpcomingMovies(UpcomingActivity.this, null);
        task.delegate = UpcomingActivity.this;
        task.execute();
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

        MenuInflater inflater = getMenuInflater();
        rowItems.get(position).toString();
        inflater.inflate(R.menu.menu_pelicula_busqueda, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.CtxAdd:
                adapter.remove(longClickPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void processFinish(Object result){
        if(result instanceof AudiovisualInterface) {
            if(DAO.getInstance().insert(UpcomingActivity.this, (AudiovisualInterface) result)){
                MyUtils.showSnacknar(listView, ((AudiovisualInterface) result).getTitulo() + " " + getResources().getString(R.string.added));
            } else {
                MyUtils.showSnacknar(listView, ((AudiovisualInterface) result).getTitulo() + " " + getResources().getString(R.string.alreadySaved));
            }
        } else {
            results.addAll((List<AudiovisualInterface>) result);
            for (AudiovisualInterface movie : ((List<AudiovisualInterface>) result)) {
                rowItems.add(new RowItem(UpcomingActivity.this, movie));
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
        Intent intent = new Intent(UpcomingActivity.this, InfoMovieSearch.class);
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
    }

    @Override
    public void removeCallback(AudiovisualInterface item) {
        SaveAudiovisual.saveItem(UpcomingActivity.this, UpcomingActivity.this, item, item.getTipo());
    }

    @Override
    public void undoCallback(AudiovisualInterface item) {
    }
}
