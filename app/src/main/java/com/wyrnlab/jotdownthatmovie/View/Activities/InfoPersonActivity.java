package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.StreamingAPI;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.AsyncResponse;
import com.wyrnlab.jotdownthatmovie.APIS.TheMovieDB.search.Person.SearchInfoPerson;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.FullImages.PhotoFullPopupWindow;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.Model.RowItemPerson;
import com.wyrnlab.jotdownthatmovie.Model.Streaming;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.CheckInternetConection;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow.InfoTVShowSearch;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.AdapterCallback;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.StreamingRecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.TrailerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InfoPersonActivity extends AppCompatActivity implements AsyncResponse, StreamingRecyclerViewAdapter.ItemClickListener, AdapterCallback, RecyclerViewClickListener {

	ProgressDialog pDialog;
	String type;
	AudiovisualInterface pelicula;
	TextView name;
	TextView valoracion;
	TextView descripcion;
	TextView genero;
	TextView director;
	TextView knownFor;
	TextView birth;
	TextView seasons;
	TextView generoLab;
	TextView directorLab;
	TextView originalTitle;
	TextView originalLanguage;
	Button botonVolver;
	ImageView image;
	private ShareActionProvider mShareActionProvider;
	int position;
	TabLayout tabLayout;
	Map<Integer, List<AudiovisualInterface>> audiovisualsByTab = new HashMap<Integer, List<AudiovisualInterface>>();

	public RecyclerView listView;
	List<RowItemInterface> rowItems;
	RecyclerViewAdapter adapter;
	int longClickPosition;

	SimilarMoviesModal similarMoviesModal;
	StreamingRecyclerViewAdapter adapterStreaming;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setTitle(R.string.title_activity_info);

		// Back button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        pelicula = (AudiovisualInterface)i.getSerializableExtra("Pelicula");
        type = i.getStringExtra("Type");
        position = i.getIntExtra("Position", 0);

        SearchInfoPerson searchorPerson = new SearchInfoPerson(InfoPersonActivity.this, pelicula.getId(), getString(R.string.searching));
		searchorPerson.delegate = this;
		MyUtils.execute(searchorPerson);

		setContentView(R.layout.person_info);

		//Obtenemos una referencia a los controles de la interfaz
		name = (TextView)findViewById(R.id.Name);
		genero = (TextView)findViewById(R.id.genero);
		director = (TextView)findViewById(R.id.director);
		valoracion = (TextView)findViewById(R.id.valoracion);
		descripcion = (TextView)findViewById(R.id.toda_la_descripcion);
		generoLab = (TextView)findViewById(R.id.generoLab);
		directorLab = (TextView)findViewById(R.id.directorLAb);
		botonVolver = (Button)findViewById(R.id.BtnAtras);
		originalTitle = (TextView)findViewById(R.id.OriginalTitleText);
		originalLanguage = (TextView)findViewById(R.id.OriginalLangugeText);
		knownFor = (TextView)findViewById(R.id.KnownFor);
		birth = (TextView)findViewById(R.id.Birth);
		tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		listView = (RecyclerView) findViewById( R.id.list );

		rowItems = new ArrayList<RowItemInterface>();
		adapter = new RecyclerViewAdapter(this, (AdapterCallback) this, R.layout.list_item, rowItems, this);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));

      //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

		// Title
		getSupportActionBar().setTitle(pelicula.getTitulo());

        //anyo.setText("	" + pelicula.getAnyo());


        name.setText(pelicula.getTitulo());

      //Implementamos el evento click del botón
        botonVolver.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	backPressed();
             }
        });


		ImageView imdbLogo = (ImageView)findViewById(R.id.tmdbLogo);
		imdbLogo.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String url = "https://www.themoviedb.org/movie/" + String.valueOf(pelicula.getId());
				Intent intent = new Intent(InfoPersonActivity.this, WebViewActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});

		/*ImageView justWatch = (ImageView)findViewById(R.id.justWatchLogo);
		justWatch.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String url = "https://www.themoviedb.org/movie/" + String.valueOf(pelicula.getId()) + "/watch";
				Intent intent = new Intent(InfoPersonActivity.this, WebViewActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});


		if(pelicula.getRating() == 0.0){
			valoracion.setText("	" +  getResources().getString(R.string.notavailable));
		}else{
			valoracion.setText("	" + Double.toString(pelicula.getRating()));
		}*/


		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				switch (tab.getPosition()){
					case 0:
						refreshList(0);
						break;
					case 1:
						refreshList(1);
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


    }

	private void refreshList(Integer tabIndex){
		if(adapter.snackbar != null && adapter.snackbar.isShown()){ adapter.snackbar.dismiss(); }
		adapter.clear();
		listView.setAdapter(null);

		adapter = new RecyclerViewAdapter(this, (AdapterCallback) this, R.layout.list_item, rowItems, this);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(this));

		if(audiovisualsByTab != null && audiovisualsByTab.containsKey(tabIndex)){
			for (AudiovisualInterface movie : audiovisualsByTab.get(tabIndex)) {
				rowItems.add(new RowItemPerson(InfoPersonActivity.this, movie));
			}
		}

		adapter.notifyDataSetChanged();
	}

	//this override the implemented method from asyncTask
	@Override
	public void processFinish(Object result){
		this.pelicula = (AudiovisualInterface) result;
		actualiza();
	}

	public void actualiza(){


		// A�adir generos
        /*genero.setText("	" + pelicula.getGenerosToStrig());
        if(pelicula.getGeneros().size() > 1){
			generoLab.setText(getResources().getString(R.string.genders));
		}

        //A�adir directores
        String direc = "";
        for (int d = 0; d < pelicula.getDirectores().size() ; d++){
        	if (d > 0){
        		direc += ", " + pelicula.getDirectores().get(d);
        		directorLab.setText(getResources().getString(R.string.directors));
        	}
        	else direc += pelicula.getDirectores().get(d);
        }
        director.setText("	" + direc);

        descripcion.setText(pelicula.getDescripcion());
*/
        Integer tabsInserted = 0;
		if(!pelicula.getCast().isEmpty()){
			tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.Cast)));
			audiovisualsByTab.put(tabsInserted, new ArrayList<AudiovisualInterface>());
			audiovisualsByTab.get(tabsInserted).addAll(pelicula.getCast());
			tabsInserted++;
		}

		if (!pelicula.getCrew().isEmpty()){
			tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.Crew)));
			audiovisualsByTab.put(tabsInserted, new ArrayList<AudiovisualInterface>());
			audiovisualsByTab.get(tabsInserted).addAll(pelicula.getCrew());
			tabsInserted++;
		}

		if(!audiovisualsByTab.get(0).isEmpty()){
			for (AudiovisualInterface movie : audiovisualsByTab.get(0)) {
				rowItems.add(new RowItemPerson(this, movie));
			}
		}


		image = (ImageView)findViewById(R.id.poster);
		final ImageLoader imageLoader = new ImageLoader(this, true);
		imageLoader.DisplayImage((General.base_url + "w500" + pelicula.getImagePath()), image);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Code to show image in full screen:
				new PhotoFullPopupWindow(InfoPersonActivity.this, R.layout.popup_photo_full, view, null, pelicula.getImagePath() == null ? null : ImageHandler.getImage(pelicula.getImage()));

			}
		});
		/*originalLanguage.setText(General.getLanguageTranslations(pelicula.getOriginalLanguage()));
		originalTitle.setText(pelicula.getTituloOriginal());

		similarMoviesModal.pelicula = this.pelicula;*/

		knownFor.setText(pelicula.getKnownFor());
		birth.setText(pelicula.getBirthday());

		listView = (RecyclerView) findViewById(R.id.list);
		adapter = new RecyclerViewAdapter(InfoPersonActivity.this, this,
				R.layout.list_item, rowItems, InfoPersonActivity.this);
		listView.setAdapter(adapter);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InfoPersonActivity.this);
		listView.setLayoutManager(linearLayoutManager);
		registerForContextMenu(listView);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case General.REQUEST_CODE_PELIBUSCADA:
				if (resultCode == General.RESULT_CODE_ADD) {
					similarMoviesModal.removeAndSaveItem(data);
				} else if(resultCode == General.RESULT_CODE_SIMILAR_CLOSED){
					if(similarMoviesModal != null && similarMoviesModal.popupWindow != null){
						similarMoviesModal.popupWindow.dismiss();
					}
				}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  //setContentView(R.layout.movie_info);
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate menu resource file.
		getMenuInflater().inflate(R.menu.menu_movie, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);

		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

		setShareIntent();

		// Return true to display menu
		return true;
	}

	private void setShareIntent() {
		if (mShareActionProvider != null) {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/movie/" + pelicula.getId());
			sendIntent.setType("text/plain");
			mShareActionProvider.setShareIntent(sendIntent);
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

	@Override
	public void onBackPressed() {
		backPressed();
	}

	private void backPressed(){
		if(similarMoviesModal != null && similarMoviesModal.closed){
			setResult(General.RESULT_CODE_SIMILAR_CLOSED);
		} else {
			setResult(Activity.RESULT_CANCELED);
		}
		finish();
	}

	@Override
	public void onItemClick(View view, int position) {
		Uri uri = Uri.parse(adapterStreaming.getItem(position).getUrl());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		InfoPersonActivity.this.startActivity(intent);
	}

	@Override
	public void swipeCallback(AudiovisualInterface item) {

	}

	@Override
	public void removeCallback(AudiovisualInterface item) {

	}

	@Override
	public void undoCallback(AudiovisualInterface item) {

	}

	@Override
	public void recyclerViewListClicked(View v, int position) {
		AudiovisualInterface pelicula = (AudiovisualInterface) ((RowItemPerson)rowItems.get(position)).getObject();

		Intent intent;
		if(pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)) {
			intent = new Intent(InfoPersonActivity.this, InfoMovieSearch.class);

		} else {
			intent = new Intent(InfoPersonActivity.this, InfoTVShowSearch.class);
		}
		intent.putExtra("Pelicula", pelicula);
		intent.putExtra("Type", pelicula.getTipo());
		intent.putExtra("Position", position);
		InfoPersonActivity.this.startActivityForResult(intent, General.REQUEST_CODE_PELIBUSCADA);
	}

	@Override
	public void recyclerViewListLongClicked(View v, int position) {

	}

	@Override
	public void recylerViewCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {

	}
}
