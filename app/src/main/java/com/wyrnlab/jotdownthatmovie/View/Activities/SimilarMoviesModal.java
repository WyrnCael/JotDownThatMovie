package com.wyrnlab.jotdownthatmovie.View.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

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
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewAdapter;
import com.wyrnlab.jotdownthatmovie.View.Recyclerviews.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SimilarMoviesModal implements AsyncResponse, AdapterCallback, RecyclerViewClickListener {

    Activity activity;
    public AudiovisualInterface pelicula;
    public RecyclerView listView;
    List<RowItemInterface> rowItems;
    List<AudiovisualInterface> results;
    RecyclerViewAdapter adapter;
    int longClickPosition;
    Context context;
    public boolean closed = false;
    public PopupWindow popupWindow;

    public SimilarMoviesModal(AudiovisualInterface pelicula, Context context, Activity activity){
        this.pelicula = pelicula;
        this.context = context;
        this.activity = activity;
    }

    public void createView(){
        results = pelicula.getSimilars();
        rowItems = new ArrayList<RowItemInterface>();
        for (AudiovisualInterface movie : results) {
            rowItems.add(new RowItem(context, movie));
        }

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.similar_list, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthS = size.x;
        int heightS = size.y;

        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setElevation(20);
        popupWindow.setWidth(widthS-80);
        popupWindow.setHeight(heightS-180);

        Drawable dim = new ColorDrawable(Color.BLACK);
        ViewGroup root = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        dim.setBounds(0, 0, root.getWidth(), root.getHeight());
        dim.setAlpha((int) (255 * 0.75));

        ViewGroupOverlay overlay = root.getOverlay();
        overlay.add(dim);


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


        listView = (RecyclerView) popupView.findViewById(R.id.list);
        adapter = new RecyclerViewAdapter(context, this,
                R.layout.list_item, rowItems, SimilarMoviesModal.this);
        listView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        listView.setLayoutManager(linearLayoutManager);
        activity.registerForContextMenu(listView);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                closed = true;
                activity.getWindow().getDecorView().getRootView().getOverlay().clear();
            }
        });

        ImageButton closeButton = (ImageButton) popupView.findViewById(R.id.ib_close);
        closeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                closed = true;
                popupWindow.dismiss();
            }
        });
    }

    public void removeAndSaveItem(Intent data){
        adapter.remove(data.getIntExtra("Position", 0));
    }

    @Override
    public void swipeCallback(AudiovisualInterface item) {
    }

    @Override
    public void removeCallback(AudiovisualInterface item) {
        SaveAudiovisual.saveItem(context, this, item, item.getTipo());
    }

    @Override
    public void undoCallback(AudiovisualInterface item) {

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        AudiovisualInterface pelicula = (AudiovisualInterface) ((RowItem)rowItems.get(position)).getObject();

        Intent intent;
        if(pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)) {
            intent = new Intent(context, InfoMovieSearch.class);

        } else {
            intent = new Intent(context, InfoTVShowSearch.class);
        }
        intent.putExtra("Pelicula", pelicula);
        intent.putExtra("Type", pelicula.getTipo());
        intent.putExtra("Position", position);
        activity.startActivityForResult(intent, General.REQUEST_CODE_PELIBUSCADA);
    }

    @Override
    public void recyclerViewListLongClicked(View v, int position) {

    }

    @Override
    public void recylerViewCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position) {

    }

    @Override
    public void processFinish(Object result) {
        if(DAO.getInstance().insert(context, (AudiovisualInterface) result)){
            MyUtils.showSnacknar(activity.getWindow().getDecorView().getRootView(), ((AudiovisualInterface) result).getTitulo() + " " + context.getResources().getString(R.string.added));
        } else {
            MyUtils.showSnacknar(activity.getWindow().getDecorView().getRootView(), ((AudiovisualInterface) result).getTitulo() + " " + context.getResources().getString(R.string.alreadySaved));
        }
    }
}

