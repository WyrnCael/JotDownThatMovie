package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.DAO.DAO;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.ExternalLibraries.materialtaptagetprompt.MaterialTapTargetPrompt;
import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.JSONModels.ModelMultiSearch;
import com.wyrnlab.jotdownthatmovie.Model.Pelicula;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.MyUtils;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;
import com.wyrnlab.jotdownthatmovie.Utils.permisionsexecutiontime.ReadExternalStorage;
import com.wyrnlab.jotdownthatmovie.Utils.permisionsexecutiontime.WriteExternalStorage;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.View.Activities.ShowInfo.showTVShow.InfoTVShowDatabase;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.AdapterCallback;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemDecorationRemoveHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.ItemTouchRemoveHelper;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	List<RowItemInterface> rowItems;
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
	RadioGroup radioGroup;
	String orderTypeAD = null;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/*SharedPreferences settings = getSharedPreferences(General.LANGUAGE_SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();*/

		if(SetTheLanguages.isEmptyLangageSettings(this)){
			inflateLangagueOptions();
		}

		loadOrderSetting();

		setTitle(R.string.app_name);

		setContentView(R.layout.mostrar_peliculas);
		moviesByType = DAO.getInstance().readAll(MainActivity.this);

		// Vaciar cache imagenes
		ImageLoader imageLoader = new ImageLoader(this, false);
		imageLoader.clearCache();

		// Solicitar permisos
		ReadExternalStorage permRead = new ReadExternalStorage(this);
		permRead.getPermissions();
		WriteExternalStorage permWrite = new WriteExternalStorage(this);
		permWrite.getPermissions();

		//Localizar los controles
		listView = (RecyclerView) findViewById( R.id.mainListView );
		rowItems = new ArrayList<RowItemInterface>();
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
						listView.scrollToPosition(0);
						break;
					case 1:
						refreshList(FILTER_MOVIE);
                        listView.scrollToPosition(0);
						break;
					case 2:
						refreshList(FILTER_TVSHOW);
                        listView.scrollToPosition(0);
						break;
					case 3:
						refreshList(FILTER_VIEWED);
                        listView.scrollToPosition(0);
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

		if(General.orderType.equals(General.orderArray.get(0)) && General.orderTypeAD.equals(General.orderADArray.get(1))){
			Collections.reverse(rowItems);
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

		orderArray();
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

			/*case R.id.action_search:
				Intent intent =  new Intent(MainActivity.this, SearchActivity.class);
				startActivityForResult(intent, REQUEST_CODE_A);
				return true;
			*/
			//
			case R.id.action_language:
				inflateLangagueOptions();
				return true;

			case R.id.action_filter:
				inflateOrderOptions();
				return true;

			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final RowItemInterface row = rowItems.get(longClickPosition);
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
		RowItemInterface item = adapter.items.get(longClickPosition);
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

	public void inflateLangagueOptions(){
		LayoutInflater inflater = getLayoutInflater();
		final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setView(inflater.inflate(R.layout.language_options, null))
				.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// sign in the user ...
						SetTheLanguages.savePreferrences(MainActivity.this);

					}
				})
				.setTitle(R.string.LanguageSettings)
				.create();

		dialog.setCanceledOnTouchOutside(false);

		dialog.setOnKeyListener(new Dialog.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
								 KeyEvent event) {
				// Do Nothing
				return true;
			}
		});

		final View appLanguagesView = inflater.inflate(R.layout.language_options, null);
		dialog.setView(appLanguagesView);
		Spinner appLanguages = (Spinner) appLanguagesView .findViewById(R.id.app_language_options);
		ArrayAdapter<CharSequence> appLanguagesAdapter = ArrayAdapter.createFromResource(this,
				R.array.languages_array, android.R.layout.simple_spinner_item);

		appLanguagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		appLanguages.setAdapter(appLanguagesAdapter);
		appLanguages.setSelection(SetTheLanguages.getAppLangagePosition(MainActivity.this), true);
		General.AppLanguage = General.appLanguagesArray.get(appLanguages.getSelectedItemPosition());

		appLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				General.AppLanguage = General.appLanguagesArray.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		final View searchLanguagesView = inflater.inflate(R.layout.language_options, null);
		dialog.setView(appLanguagesView);
		Spinner searchLanguages = (Spinner) appLanguagesView .findViewById(R.id.search_language_options);
		ArrayAdapter<CharSequence> searchLanguagesAdapter = ArrayAdapter.createFromResource(this,
				R.array.search_languages_array, android.R.layout.simple_spinner_item);

		searchLanguagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		searchLanguages.setAdapter(searchLanguagesAdapter);
		searchLanguages.setSelection(SetTheLanguages.getSearchLangagePosition(), true);
		General.SearchLanguage = General.searchLanguagesArray.get(searchLanguages.getSelectedItemPosition());

		searchLanguages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				General.SearchLanguage = General.searchLanguagesArray.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		dialog.show();
	}

	public void inflateOrderOptions(){
		LayoutInflater inflater = getLayoutInflater();
		final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setView(inflater.inflate(R.layout.order_options, null))
				.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						final View dialogView = getLayoutInflater().inflate(R.layout.order_options, null);
						final RadioGroup group = dialogView.findViewById(R.id.OrderRadioGroup);
						int radioGroupId = radioGroup.getCheckedRadioButtonId();
						RadioButton myCheckedButton = (RadioButton)dialogView.findViewById(radioGroupId);
						int index = group.indexOfChild(myCheckedButton);

						General.orderType = General.orderArray.get(index);
						General.orderTypeAD = orderTypeAD;

						SharedPreferences settings = getSharedPreferences(General.ORDER_SETTINGS, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(General.ORDER_TYPE_SETTINGS, General.orderType);
						editor.putString(General.ORDERAD_SETTING, General.orderTypeAD);
						editor.commit();

						refreshList(filter);
						listView.scrollToPosition(0);
					}
				})
				.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				})
				.setTitle(R.string.SelectOrder)
				.create();

		dialog.setOnKeyListener(new Dialog.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode,
								 KeyEvent event) {
				// Do Nothing
				return true;
			}
		});

		dialog.show();

		Spinner orderTypeSpinner = (Spinner) dialog.findViewById(R.id.orderTypeAD);
		ArrayAdapter<CharSequence> orderTypeAdapter = ArrayAdapter.createFromResource(this,
				R.array.order_array, android.R.layout.simple_spinner_item);
		orderTypeSpinner.setAdapter(orderTypeAdapter);
		orderTypeSpinner.setSelection(SetTheLanguages.getAppLangagePosition(MainActivity.this), true);
		orderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				orderTypeAD = General.orderADArray.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		radioGroup = dialog.findViewById(R.id.OrderRadioGroup);
		//Log.d("Order Type", General.orderType);
		if(General.orderType == null){
			((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
			General.orderType = General.orderArray.get(0);
		} else {
			int index = General.orderArray.indexOf(General.orderType);
			((RadioButton)radioGroup.getChildAt(index)).setChecked(true);
		}

		if(General.orderTypeAD == null){
			orderTypeSpinner.setSelection(0, true);
			General.orderTypeAD = General.orderADArray.get(0);
			orderTypeAD = General.orderADArray.get(0);
		} else {
			int index = General.orderADArray.indexOf(General.orderTypeAD);
			orderTypeSpinner.setSelection(index, true);
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

	private void loadOrderSetting(){
		SharedPreferences settings = getSharedPreferences(General.ORDER_SETTINGS, 0);
		General.orderType = settings.getString(General.ORDER_TYPE_SETTINGS, null);
		General.orderTypeAD = settings.getString(General.ORDERAD_SETTING, null);

		if(General.orderType == null){
			General.orderType = General.orderArray.get(0);
		}

		if (General.orderTypeAD == null){
			General.orderTypeAD = General.orderADArray.get(0);
			orderTypeAD = General.orderADArray.get(0);
		}
	}

	private void orderArray(){
		int orderType = General.orderArray.indexOf(General.orderType);
		int orderADType = General.orderADArray.indexOf(General.orderTypeAD);

		switch(orderType){
			case 0:
				break;
			case 1:
				if(orderADType == 0){
					Collections.sort(rowItems, new Comparator<RowItemInterface>() {
						@Override
						public int compare(RowItemInterface o1, RowItemInterface o2) {
							return o1.getYear().compareTo(o2.getYear());
						}
					});
				} else {
					Collections.sort(rowItems, new Comparator<RowItemInterface>() {
						@Override
						public int compare(RowItemInterface o1, RowItemInterface o2) {
							return o2.getYear().compareTo(o1.getYear());
						}
					});
				}
				break;
			case 2:
				if(orderADType == 0){
					Collections.sort(rowItems, new Comparator<RowItemInterface>() {
						@Override
						public int compare(RowItemInterface o1, RowItemInterface o2) {
							if(o1.getRating().equals(getString(R.string.notavailable))){
								return -11;
							} else if (o2.getRating().equals(getString(R.string.notavailable))){
								return 1;
							} else {
								return Double.valueOf(o1.getRating()).compareTo(Double.valueOf(o2.getRating()));
							}
						}
					});
				} else {
					Collections.sort(rowItems, new Comparator<RowItemInterface>() {
						@Override
						public int compare(RowItemInterface o1, RowItemInterface o2) {
							if(o1.getRating().equals(getString(R.string.notavailable))){
								return 1;
							} else if (o2.getRating().equals(getString(R.string.notavailable))){
								return -1;
							} else {
								return Double.valueOf(o2.getRating()).compareTo(Double.valueOf(o1.getRating()));
							}
						}
					});
				}
				break;
			case 3:
				if(orderADType == 0){
					Collections.sort(rowItems, new Comparator<RowItemInterface>() {
						@Override
						public int compare(RowItemInterface o1, RowItemInterface o2) {
							return o1.getTitle().compareTo(o2.getTitle());
						}
					});
				} else {
					Collections.sort(rowItems, new Comparator<RowItemInterface>() {
						@Override
						public int compare(RowItemInterface o1, RowItemInterface o2) {
							return o2.getTitle().compareTo(o1.getTitle());
						}
					});
				}
				break;
		}
		adapter.notifyDataSetChanged();
	}

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
