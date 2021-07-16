package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.APIS.Analytics.OpenApp;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.materialtaptagetprompt.MaterialTapTargetPrompt;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.permisionsexecutiontime.ReadExternalStorage;
import com.wyrnlab.jotdownthatmovie.Utils.permisionsexecutiontime.WriteExternalStorage;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow.InfoTVShowDatabase;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.AdapterCallback;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemDecorationRemoveHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemTouchRemoveHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;

import java.nio.channels.FileLockInterruptionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener, AdapterCallback {

	public final static int REQUEST_CODE_A = 1;
	FloatingActionButton fab;
	FloatingActionButton fabFilter;
	FloatingActionButton fabSearch;
	Boolean isFABOpen = false;
	private Map<String, List<AudiovisualInterface>> moviesByType;
	public RecyclerView listView;
	List<RowItem> rowItems;
	private static String FILTER_ALL = "All";
	private static String FILTER_MOVIE = "Movie";
	private static String FILTER_TVSHOW = "Show";
	private static String FILTER_VIEWED = General.VIEWED;
	String filter = FILTER_ALL;
	Boolean firstTime = true;
	Boolean dontExit = false;
	Boolean searcBtnhUp = false;
	Menu menu;
	RecyclerViewAdapter adapter;
	TabLayout tabLayout;
	private int longClickPosition;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		OpenApp analytic = new OpenApp(this);
		MyUtils.execute(analytic);

		setContentView(R.layout.mostrar_peliculas);
		moviesByType = DAO.getInstance().readAll(MainActivity.this);

		// Vaciar cache imagenes
		ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.clearCache();

		// Solicitar permisos
		ReadExternalStorage permRead = new ReadExternalStorage(this);
		permRead.getPermissions();
		WriteExternalStorage permWrite = new WriteExternalStorage(this);
		permWrite.getPermissions();

		//Localizar los controles
		listView = (RecyclerView) findViewById( R.id.mainListView );
		rowItems = new ArrayList<RowItem>();
		adapter = new RecyclerViewAdapter(this, (AdapterCallback) this, R.layout.list_item, rowItems, this);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));
		registerForContextMenu(listView);

		if(getIntent().getStringExtra("Name") != null) MyUtils.showSnacknar(listView, "\"" + getIntent().getStringExtra("Name") + "\" " + getResources().getString(R.string.added) + "!");

		//Swipe
		ItemTouchRemoveHelper simpleItemTouchCallback = new ItemTouchRemoveHelper(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT, MainActivity.this);
		androidx.recyclerview.widget.ItemTouchHelper mItemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(simpleItemTouchCallback);
		mItemTouchHelper.attachToRecyclerView(listView);
		listView.addItemDecoration(new ItemDecorationRemoveHelper());

		// TabLayout
		tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.FilterAll) + " (" + moviesByType.get(FILTER_ALL).size() + ")"));
		tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.FilterMovie) + " (" + moviesByType.get(FILTER_MOVIE).size() + ")"));
		tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.FilterTVShow) + " (" + moviesByType.get(FILTER_TVSHOW).size() + ")"));
		tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.Viewed) + " (" + moviesByType.get(FILTER_VIEWED).size() + ")"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				switch (tab.getPosition()){
					case 0:
						refreshList(FILTER_ALL);
						break;
					case 1:
						refreshList(FILTER_MOVIE);
						break;
					case 2:
						refreshList(FILTER_TVSHOW);
						break;
					case 3:
						refreshList(FILTER_VIEWED);
						break;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

		fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
		fabSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_A);
			}
		});

		fabSearch.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
									   int oldTop, int oldRight, int oldBottom) {
				if(fabSearch.getTranslationY() == -getResources().getDimension(R.dimen.standard_108)){
					if(firstTime) {
						new MaterialTapTargetPrompt.Builder(MainActivity.this)
								.setTarget(R.id.fabSearch)
								.setPrimaryText(getResources().getString(R.string.StartPrimaryText))
								.setSecondaryText(getResources().getString(R.string.StartSecondaryText))
								.show();
					}
				}
			}
		});

		refreshList(FILTER_ALL);

	}

	private void showFABMenu(){
		isFABOpen=true;
	}

	private void closeFABMenu(){
		isFABOpen=false;
	}



	private void refreshList(String typeFilter){
		closeFABMenu();
		filter = typeFilter;

		Parcelable recylerViewState = listView.getLayoutManager().onSaveInstanceState();

		if(adapter.snackbar != null && adapter.snackbar.isShown()){ adapter.snackbar.dismiss(); }
		adapter.clear();
		listView.setAdapter(null);

		adapter = new RecyclerViewAdapter(this, (AdapterCallback) this, R.layout.list_item, rowItems, this);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));

		moviesByType = DAO.getInstance().readAll(MainActivity.this);

		for (AudiovisualInterface movie : moviesByType.get(typeFilter)) {
			rowItems.add(new RowItem(MainActivity.this, movie));
		}

		adapter.notifyDataSetChanged();

		refreshTabs();

		listView.getLayoutManager().onRestoreInstanceState(recylerViewState);

		if(moviesByType.get(FILTER_ALL).isEmpty() && moviesByType.get(FILTER_VIEWED).isEmpty()) {
			firstTime = true;
			showTutorialSearch();
		} else {
			firstTime = false;
		}
	}

	public void refreshTabs(){
		tabLayout.getTabAt(0).setText(getString(R.string.FilterAll) + " (" + moviesByType.get(FILTER_ALL).size() + ")");
		tabLayout.getTabAt(1).setText(getString(R.string.FilterMovie) + " (" + moviesByType.get(FILTER_MOVIE).size() + ")");
		tabLayout.getTabAt(2).setText(getString(R.string.FilterTVShow) + " (" + moviesByType.get(FILTER_TVSHOW).size() + ")");
		tabLayout.getTabAt(3).setText(getString(R.string.Viewed) + " (" + moviesByType.get(FILTER_VIEWED).size() + ")");
	}

	public void showTutorialSearch(){
		if(firstTime) {
			new MaterialTapTargetPrompt.Builder(MainActivity.this)
					.setTarget(fabSearch)
					.setPrimaryText(getResources().getString(R.string.SearchPrimaryText))
					.setSecondaryText(getResources().getString(R.string.SearchSecondaryText))
					//.setBackButtonDismissEnabled(false)
					.setCaptureTouchEventOutsidePrompt(true)
					.setCaptureTouchEventOutsidePrompt(true)
					.setAutoDismiss(false)
					.setClipToView(getWindow().getDecorView())
					.setBackgroundColour(Color.parseColor("#009688"))
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode){
			case General.RESULT_CODE_REMOVED:
				adapter.pendingRemoval(data.getIntExtra("Position", 0));
				break;
			case General.RESULT_CODE_ADD:
				adapter.items.add(new RowItem(MainActivity.this, (AudiovisualInterface)data.getExtras().getSerializable("Pelicula")));
				adapter.notifyDataSetChanged();
				MyUtils.showSnacknar(findViewById(R.id.realtiveLayoutMovieInfo), "\"" + data.getStringExtra("Name") + "\" " + getResources().getString(R.string.added) + "!");
				break;
			case General.RESULT_CODE_NEEDS_REFRESH:
				refreshList(filter);
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				Intent intentAbout =  new Intent(MainActivity.this, About.class);
				startActivity(intentAbout);
				return true;

			case R.id.action_quit:
				Intent homeIntent = new Intent(Intent.ACTION_MAIN);
				homeIntent.addCategory( Intent.CATEGORY_HOME );
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);
				finish();
				return true;

			case R.id.action_search:
				Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_A);
				return true;

			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final RowItem row = rowItems.get(longClickPosition);
		switch (item.getItemId()) {
			case R.id.CtxLstOpc2:
				adapter.pendingRemoval(longClickPosition);
				return true;
			case R.id.SetAsViewed:
				setAsViewedOrNotViewed(longClickPosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	public void removeItemFromDB(AudiovisualInterface item){
		DAO.getInstance().delete(MainActivity.this, item.getId());
	}

	public void setAsViewedOrNotViewed(Integer longClickPosition){
		RowItem item = adapter.items.get(longClickPosition);
		AudiovisualInterface pelicula = (AudiovisualInterface) item.getObject();
		pelicula.setId(item.getId());
		if(pelicula.getViewed()){
			pelicula.setViewed(false);
		} else {
			pelicula.setViewed(true);
		}

		if(DAO.getInstance().updateAsViewed(MainActivity.this, pelicula)){
			MyUtils.showSnacknar(((Activity)MainActivity.this).findViewById(R.id.mainListView), getResources().getString(R.string.MarkedAsViewed));
			adapter.items.remove(longClickPosition);
			adapter.notifyDataSetChanged();
		} else {
			MyUtils.showSnacknar(((Activity)MainActivity.this).findViewById(R.id.mainListView), getResources().getString(R.string.MarkAsViewedError));
		}

		refreshList(filter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
			startActivityForResult(intent, REQUEST_CODE_A);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onBackPressed() {
		if ( dontExit){
			dontExit = false;
		} else if (!isFABOpen) {
			finish();
		}else{
			closeFABMenu();
		}
	}

	@Override
	public void recyclerViewListLongClicked(View v, int position) {
		longClickPosition = position;
	}

	@Override
	public void recylerViewCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();

		menu.setHeaderTitle(rowItems.get(position).toString());
		inflater.inflate(R.menu.menu_pelicula_lista, menu);


		MenuItem item = menu.findItem(R.id.SetAsViewed);
		if(filter.equalsIgnoreCase(FILTER_VIEWED)){
			item.setTitle(getString(R.string.MarkAsNOTViewed));
		} else {
			item.setTitle(getString(R.string.MarkAsViewed));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public void recyclerViewListClicked(View v, int position) {

		AudiovisualInterface pelicula = (AudiovisualInterface) ((RowItem)rowItems.get(position)).getObject();

		Intent intent;
		if(pelicula.getTipo() == null || pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)){
			intent =  new Intent(MainActivity.this, InfoMovieDatabase.class);
		} else {
			intent =  new Intent(MainActivity.this, InfoTVShowDatabase.class);
		}
		intent.putExtra("Pelicula", pelicula);
		intent.putExtra("Position", position);
		startActivityForResult(intent, REQUEST_CODE_A);
	}

	@Override
	public void removeCallback(AudiovisualInterface item) {
		removeItemFromDB(item);
	}

	@Override
	public void undoCallback(AudiovisualInterface item) {
		moviesByType.get(item.getTipo()).add(item);
		moviesByType.get(FILTER_ALL).add(item);
		refreshTabs();
	}

	@Override
	public void swipeCallback(AudiovisualInterface item){
		moviesByType.get(item.getTipo()).remove(item);
		moviesByType.get(FILTER_ALL).remove(item);
		refreshTabs();
	}
}
