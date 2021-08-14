package com.wyrnlab.jotdownthatmovie.View.Recyclerviews;

import android.content.Context;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wyrnlab.jotdownthatmovie.Model.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    protected static final int PENDING_REMOVAL_TIMEOUT = 3000;
    protected static RecyclerViewClickListener itemListener;
	Context context;
	AdapterCallback adapterCallback;
	public List<RowItemInterface> items;
    List<RowItemInterface> itemsPendingRemoval;
	int resourceId;
	MovieViewHolder holderAdapter;
    boolean undoOn = true;
    protected Handler handler = new Handler();
    HashMap<Integer, Runnable> pendingRunnables = new HashMap<>();
    View parentView;
    public Snackbar snackbar;

    public RecyclerViewAdapter(Context context, AdapterCallback adapterCallback, int resourceId,
                               List<RowItemInterface> items, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.adapterCallback = adapterCallback;
        this.resourceId = resourceId;
        this.items = items;
        this.itemListener = itemListener;
        this.itemsPendingRemoval = new ArrayList<RowItemInterface>();

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parentView = parent;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.resourceId, parent, false);
        this.holderAdapter = new MovieViewHolder(this.context, view);
        return this.holderAdapter;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        final RowItemInterface row = this.items.get(position);
        holder.setMovieRow(row, position, itemListener);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void update(List<RowItemInterface> newRows){
        items.clear();
        items.addAll(newRows);
        this.notifyDataSetChanged();
    }

    public void clear() {
        for(Runnable r : pendingRunnables.values()){
            if (r != null) handler.removeCallbacks(r);
        }

        for(RowItemInterface row : itemsPendingRemoval){
            adapterCallback.removeCallback((AudiovisualInterface) row.getObject());
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
        final RowItemInterface row = items.get(position);
        final Integer itemId = items.get(position).getId();
        final Integer itemPosition = items.indexOf(row);
        if (!itemsPendingRemoval.contains(row)) {
            itemsPendingRemoval.add(row);

            // let's create, store and post a runnable to remove the itemId
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    adapterCallback.removeCallback((AudiovisualInterface) row.getObject());
                }
            };

            items.remove(row);
            notifyItemRemoved(itemPosition);
            adapterCallback.swipeCallback((AudiovisualInterface) row.getObject());

            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(row.getId(), pendingRemovalRunnable);
            this.snackbar = Snackbar.make(this.parentView, row.getTitle() + " " + context.getResources().getString(R.string.removed), PENDING_REMOVAL_TIMEOUT);
            this.snackbar.setAction(context.getString(R.string.button_undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            items.add(itemPosition, row);
                            adapterCallback.undoCallback((AudiovisualInterface) row.getObject());

                            Runnable pendingRemovalRunnable = pendingRunnables.get(itemId);
                            pendingRunnables.remove(itemId);
                            if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                            itemsPendingRemoval.remove(row);
                            // this will rebind the row in "normal" state
                            notifyDataSetChanged();
                        }
                    })
                    .show();
        }
    }

    public void remove(int position) {
        final RowItemInterface item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            notifyItemRemoved(position);
        }

        adapterCallback.removeCallback((AudiovisualInterface) item.getObject());
    }

    public boolean isPendingRemoval(int position) {
        RowItemInterface item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    @Override
    public void onViewRecycled(MovieViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }
}