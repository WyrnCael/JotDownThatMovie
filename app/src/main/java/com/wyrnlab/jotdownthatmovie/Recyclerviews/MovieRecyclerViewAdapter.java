package com.wyrnlab.jotdownthatmovie.Recyclerviews;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.images.ImageHandler;
import com.wyrnlab.jotdownthatmovie.search.RowItem;

import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieViewHolder> {

	Context context;
	List<RowItem> items;
	int resourceId;
	MovieViewHolder holder;

    public MovieRecyclerViewAdapter(Context context, int resourceId,
                                    List<RowItem> items) {
        this.context = context;
        this.resourceId = resourceId;
        this.items = items;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.resourceId, parent, false);
        this.holder = new MovieViewHolder(this.context, view);
        return this.holder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int i) {
        RowItem row = this.items.get(i);

        holder.setMovieRow(row);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void update(List<RowItem> newRows){
        items.clear();
        items.addAll(newRows);
        this.notifyDataSetChanged();
    }

    public void clear() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void clearCache(){
        holder.clearCache();
    }
   
}