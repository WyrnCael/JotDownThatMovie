package com.wyrnlab.jotdownthatmovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.eclipsesource.json.JsonObject;

import data.General;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.ReadExternalStorage;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.WriteExternalStorage;
import com.wyrnlab.jotdownthatmovie.search.CustomListViewAdapter;
import com.wyrnlab.jotdownthatmovie.search.RowItem;
import com.wyrnlab.jotdownthatmovie.sql.PeliculasSQLiteHelper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Debug;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import api.search.Pelicula;
import data.SetTheLanguages;

public class MainActivity extends Activity {

	public final static int REQUEST_CODE_A = 1;
	private List<Pelicula> movies;
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

		SearchBaseUrl searchor = new SearchBaseUrl(this);
		searchor.execute();

		//Localizar los controles
		listView = (ListView) findViewById( R.id.mainListView );

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
				Pelicula search = movies.get(position);
				Pelicula pelicula = DAO.getInstance().readFromSQL(MainActivity.this, search.getTitulo(), search.getAnyo());
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

		// Si no es a�adir
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
				FrameLayout frameLayout = (FrameLayout ) findViewById(R.id.about_frame);
				frameLayout.setVisibility(View.VISIBLE);
				frameLayout.setClickable(false);

				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						FrameLayout frameLayout = (FrameLayout ) findViewById(R.id.about_frame);
						frameLayout.setVisibility(View.GONE);

						refreshList();
					}
				});
				return true;

			case R.id.action_quit:
				Intent homeIntent = new Intent(Intent.ACTION_MAIN);
				homeIntent.addCategory( Intent.CATEGORY_HOME );
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);
				finish();
				return true;

			case R.id.action_search:
				Intent intent =  new Intent(MainActivity.this, AnadirPelicula.class);
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
				Pelicula selected = movies.get(info.position);

				DAO.getInstance().delete(MainActivity.this, selected.getTitulo(), selected.getAnyo());

				Toast.makeText(getApplicationContext(), getResources().getString(R.string.Movie) + " \"" + selected.getTitulo() + "\" " + getResources().getString(R.string.removed) + "!", Toast.LENGTH_SHORT).show();

				refreshList();

				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	public class SearchBaseUrl extends AsyncTask<String, Integer, List<Pelicula>> {

		private HttpsURLConnection yc;
		Context context;

		public SearchBaseUrl(Context context){
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<Pelicula> doInBackground(String... params) {
			getBaseUrl();
			return null;

		}

		@Override
		protected void onPostExecute(List<Pelicula> result)
		{
			super.onPostExecute(result);
		}

		private void getBaseUrl() {
			String web = null;

			String url = General.URLPRINCIPAL + "3/configuration?api_key=" + General.APIKEY;
			try {
				URL oracle = new URL(url);

				yc = (HttpsURLConnection) oracle.openConnection();

				String json = "";

				//yc.setDoOutput(true);
				yc.setDoInput(true);
				yc.setInstanceFollowRedirects(false);
				yc.setRequestMethod("GET");
				//yc.setUseCaches (true);
				yc.setRequestProperty("Accept", "application/json");

				yc.connect();

				InputStream is = null;
				try {
					is = yc.getInputStream();
				} catch (IOException ioe) {
					if (yc instanceof HttpsURLConnection) {
						HttpsURLConnection httpConn = (HttpsURLConnection) yc;
						int statusCode = httpConn.getResponseCode();
						if (statusCode != 200) {
							is = httpConn.getErrorStream();
						}
					}
				}

				InputStreamReader isReader = new InputStreamReader(is);
				//put output stream into a string
				BufferedReader br = new BufferedReader(isReader );
				String inputLine;
				while ((inputLine = br.readLine()) != null)
					web += inputLine;
				br.close();
				yc.disconnect();

				yc.disconnect();

				json = web.substring(4);

				JsonObject respuestaTotal = JsonObject.readFrom( json );
				JsonObject images = respuestaTotal.get("images").asObject();
				General.base_url = images.get("base_url").asString();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
