package com.wyrnlab.jotdownthatmovie;

import java.util.ArrayList;
import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.ReadExternalStorage;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.WriteExternalStorage;
import com.wyrnlab.jotdownthatmovie.search.CustomListViewAdapter;
import com.wyrnlab.jotdownthatmovie.search.RowItem;

import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import api.search.AudiovisualInterface;
import api.search.Movies.Pelicula;

public class MainActivity extends AppCompatActivity {

	public final static int REQUEST_CODE_A = 1;
	private List<AudiovisualInterface> movies;
	ListView listView;
	List<RowItem> rowItems;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mostrar_peliculas);

		// Vaciar cache imagenes
		ImageLoader imageLoader = new ImageLoader(this);
		imageLoader.clearCache();

		// Solicitar permisos
		ReadExternalStorage permRead = new ReadExternalStorage(this);
		permRead.getPermissions();
		WriteExternalStorage permWrite = new WriteExternalStorage(this);
		permWrite.getPermissions();

		//Localizar los controles
		listView = (ListView) findViewById( R.id.mainListView );

		// Boton buscar
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();*/
				Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_A);
			}
		});

		refreshList();

	}

	private void refreshList(){

		movies = DAO.getInstance().readAll(MainActivity.this);

		rowItems = new ArrayList<RowItem>();
		RowItem item;
		for (int i = 0; i < movies.size(); i++) {
			if(movies.get(i).getRating() == 0.0)
				item = new RowItem(1, movies.get(i).getImage(), movies.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + movies.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + getResources().getString(R.string.notavailable)) );
			else
				item = new RowItem(1, movies.get(i).getImage(), movies.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + movies.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + movies.get(i).getRating()) );
			rowItems.add(item);
		}


		listView = (ListView) findViewById(R.id.mainListView);
		CustomListViewAdapter adapter;
		adapter = new CustomListViewAdapter(this,
				R.layout.list_item, rowItems);
		listView.setAdapter(adapter);

		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Intent intent =  new Intent(MainActivity.this, InfoMovieDatabase.class);
				AudiovisualInterface search = movies.get(position);
				AudiovisualInterface pelicula = DAO.getInstance().readFromSQL(MainActivity.this, search.getTitulo(), search.getAnyo());
				intent.putExtra("Pelicula", pelicula);
				startActivityForResult(intent, REQUEST_CODE_A);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();

		AdapterView.AdapterContextMenuInfo info =
				(AdapterView.AdapterContextMenuInfo)menuInfo;

		// Si no es añadir
		menu.setHeaderTitle(listView.getAdapter().getItem(info.position).toString());
		inflater.inflate(R.menu.menu_pelicula_lista, menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_A) {
			refreshList();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				// Set about visible
				/*FrameLayout frameLayout = (FrameLayout ) findViewById(R.id.about_frame);
				frameLayout.setVisibility(View.VISIBLE);
				frameLayout.setClickable(false);

				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						FrameLayout frameLayout = (FrameLayout ) findViewById(R.id.about_frame);
						frameLayout.setVisibility(View.GONE);

						refreshList();
					}
				});*/
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

		AdapterContextMenuInfo info =
				(AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
			case R.id.CtxLstOpc2:
				AudiovisualInterface selected = movies.get(info.position);

				DAO.getInstance().delete(MainActivity.this, selected.getTitulo(), selected.getAnyo());

				Toast.makeText(getApplicationContext(), getResources().getString(R.string.Movie) + " \"" + selected.getTitulo() + "\" " + getResources().getString(R.string.removed) + "!", Toast.LENGTH_SHORT).show();

				refreshList();

				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onBackPressed()
	{
		finish();
	}
}
