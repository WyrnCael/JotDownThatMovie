package com.wyrnlab.jotdownthatmovie.Recyclerviews;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wyrnlab.jotdownthatmovie.Activities.MainActivity;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.data.General;
import com.wyrnlab.jotdownthatmovie.search.RowItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
	private static RecyclerViewClickListener itemListener;
	Context context;
	AdapterCallback adapterCallback;
	public List<RowItem> items;
    List<RowItem> itemsPendingRemoval;
	int resourceId;
	MovieViewHolder holderAdapter;
    boolean undoOn = true;
    private Handler handler = new Handler();
    HashMap<Integer, Runnable> pendingRunnables = new HashMap<>();

    public MovieRecyclerViewAdapter(Context context, int resourceId,
                                    List<RowItem> items, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.adapterCallback = (AdapterCallback) context;
        this.resourceId = resourceId;
        this.items = items;
        this.itemListener = itemListener;
        this.itemsPendingRemoval = new ArrayList<RowItem>();

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.resourceId, parent, false);
        this.holderAdapter = new MovieViewHolder(this.context, view);
        return this.holderAdapter;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final RowItem row = this.items.get(position);

        //holder.setMovieRow(row, i, itemListener);

        final Integer item = items.get(position).getId();


        if (itemsPendingRemoval.contains(row)) {
            // we need to show the "undo" state of the row
            holder.itemView.setOnClickListener(null);
            holder.itemView.setOnClickListener(null);
            holder.itemView.setBackgroundColor(context instanceof MainActivity ? Color.RED : context.getResources().getColor(R.color.verde));

            holder.imageView.setVisibility(View.GONE);
            holder.txtTitle.setVisibility(View.GONE);
            holder.txtDesc.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);

            holder.undoButton.setVisibility(View.VISIBLE);

            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(row);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(row));
                }
            });
        } else {
            holder.setMovieRow(row, position, itemListener);
            // we need to show the "normal" state
            holder.itemView.setBackgroundColor(Color.WHITE);

            holder.imageView.setVisibility(View.VISIBLE);
            holder.txtTitle.setVisibility(View.VISIBLE);
            holder.txtDesc.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);

            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);
        }
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
        for(Runnable r : pendingRunnables.values()){
           if (r != null) handler.removeCallbacks(r);
        }
        pendingRunnables.clear();
        itemsPendingRemoval.clear();

        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void clearCache(){
        holderAdapter.clearCache();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final RowItem item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    adapterCallback.swipeCallback(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item.getId(), pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        RowItem item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        RowItem item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    @Override
    public void onViewRecycled(MovieViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
}