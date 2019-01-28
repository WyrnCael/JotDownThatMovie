package com.wyrnlab.jotdownthatmovie.Activities;

import java.util.ArrayList;
import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.showTVShow.InfoTVShowDatabase;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.ReadExternalStorage;
import com.wyrnlab.jotdownthatmovie.permisionsexecutiontime.WriteExternalStorage;
import com.wyrnlab.jotdownthatmovie.search.CustomListViewAdapter;
import com.wyrnlab.jotdownthatmovie.search.RowItem;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import com.wyrnlab.jotdownthatmovie.api.search.AudiovisualInterface;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class MainActivity extends AppCompatActivity {

	public final static int REQUEST_CODE_A = 1;
	FloatingActionButton fab;
	FloatingActionButton fabFilter;
	FloatingActionButton fabSearch;
	Boolean isFABOpen = false;
	private List<AudiovisualInterface> movies;
	ListView listView;
	List<RowItem> rowItems;
	private static String FILTER_ALL = "All";
	private static String FILTER_MOVIE = "Movie";
	private static String FILTER_TVSHOW = "Show";
	String filter = FILTER_ALL;
	Boolean firstTime = true;
	Boolean dontExit = false;
	Boolean searcBtnhUp = false;


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

		listView.setOnScrollListener(new AbsListView.OnScrollListener(){
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if(isFABOpen){
					closeFABMenu();
				}
			}
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}
		});

		/*// Boton buscar
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();*/
				/*Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_A);
			}
		});
		*/

		// Floating menu
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fabFilter = (FloatingActionButton) findViewById(R.id.fabFilter);
		fabFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String[] filters = {getResources().getString(R.string.FilterAll), getResources().getString(R.string.FilterMovie), getResources().getString(R.string.FilterTVShow)};

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(getResources().getString(R.string.SelectFilter));
				builder.setItems(filters, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// the user clicked on colors[which]
						switch (which){
							case 0:
								filter = FILTER_ALL;
								refreshList(FILTER_ALL);
								break;
							case 1:
								filter = FILTER_MOVIE;
								refreshList(FILTER_MOVIE);
								break;
							case 2:
								filter = FILTER_TVSHOW;
								refreshList(FILTER_TVSHOW);
								break;
						}
					}
				});
				builder.show();
			}
		});
		fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
		fabSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();*/
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

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isFABOpen){
					showFABMenu();
				}else{
					closeFABMenu();
				}
			}
		});

		refreshList(FILTER_ALL);

	}

	private void showFABMenu(){
		isFABOpen=true;
		fabFilter.animate().translationY(-getResources().getDimension(R.dimen.standard_58));
		fabSearch.animate().translationY(-getResources().getDimension(R.dimen.standard_108)).setListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if(firstTime && searcBtnhUp) {
					dontExit = true;
					new MaterialTapTargetPrompt.Builder(MainActivity.this)
							.setTarget(fabSearch)
							.setPrimaryText(getResources().getString(R.string.SearchPrimaryText))
							.setSecondaryText(getResources().getString(R.string.SearchSecondaryText))
							//.setBackButtonDismissEnabled(false)
							.setCaptureTouchEventOutsidePrompt(true)
							.setCaptureTouchEventOutsidePrompt(true)
							.setAutoDismiss(false)
							.setClipToView(getWindow().getDecorView())
							.setBackgroundColour(Color.parseColor("#ffff4444"))
							.setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
							{
								@Override
								public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
								{
									if (state == MaterialTapTargetPrompt.STATE_BACK_BUTTON_PRESSED)
									{
										prompt.finish();
										closeFABMenu();
										showTutorialSearch();
									}
								}
							})
							.show();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		}).start();
		searcBtnhUp = true;
	}

	private void closeFABMenu(){
		isFABOpen=false;
		fabFilter.animate().translationY(0);
		fabSearch.animate().translationY(0);
		searcBtnhUp = false;
	}



	private void refreshList(String typeFilter){
		closeFABMenu();
		movies = DAO.getInstance().readAll(MainActivity.this);

		rowItems = new ArrayList<RowItem>();
		RowItem item;

		for (int i = 0; i < movies.size(); i++) {
			if( typeFilter == FILTER_ALL || (typeFilter == FILTER_MOVIE && movies.get(i).getTipo() == null) || (movies.get(i).getTipo() != null && movies.get(i).getTipo().equalsIgnoreCase(typeFilter)) ) {
				if (movies.get(i).getRating() == 0.0)
					item = new RowItem(1, movies.get(i).getImage(), movies.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + movies.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + getResources().getString(R.string.notavailable)), movies.get(i).getTipo());
				else
					item = new RowItem(1, movies.get(i).getImage(), movies.get(i).getTitulo(), (getResources().getString(R.string.anyo) + " " + movies.get(i).getAnyo() + " " + getResources().getString(R.string.valoracion) + " " + movies.get(i).getRating()), movies.get(i).getTipo());
				rowItems.add(item);
			}
		}


		CustomListViewAdapter adapter;
		adapter = new CustomListViewAdapter(this,
				R.layout.list_item, rowItems);
		listView.setAdapter(adapter);

		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				AudiovisualInterface search = movies.get(position);
				AudiovisualInterface pelicula = DAO.getInstance().readFromSQL(MainActivity.this, search.getTitulo(), search.getAnyo());

				Intent intent;
				if(pelicula.getTipo() == null || pelicula.getTipo().equalsIgnoreCase("Movie")){
					intent =  new Intent(MainActivity.this, InfoMovieDatabase.class);
				} else {
					intent =  new Intent(MainActivity.this, InfoTVShowDatabase.class);
				}
				intent.putExtra("Pelicula", pelicula);
				startActivityForResult(intent, REQUEST_CODE_A);
			}
		});

		if(movies.size() == 0) {
			firstTime = true;
			showTutorialSearch();
		} else {
			firstTime = false;
		}
	}

	public void showTutorialSearch(){
		new MaterialTapTargetPrompt.Builder(this)
				.setTarget(R.id.fab)
				.setPrimaryText(getResources().getString(R.string.StartPrimaryText))
				.setSecondaryText(getResources().getString(R.string.StartSecondaryText))
				.setBackButtonDismissEnabled(false)
				.setCaptureTouchEventOutsidePrompt(true)
				.setCaptureTouchEventOutsidePrompt(true)
				.setClipToView(getWindow().getDecorView())
				.setAutoDismiss(false)
				.setBackgroundColour(Color.parseColor("#009688"))
				.show();
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
			refreshList(FILTER_ALL);
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

				refreshList(filter);

				return true;
			default:
				return super.onContextItemSelected(item);
		}
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
}
