package com.wyrnlab.jotdownthatmovie.View.Recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoaderStreaming;
import com.wyrnlab.jotdownthatmovie.Model.Streaming;
import com.wyrnlab.jotdownthatmovie.R;

import java.util.List;

import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

public class StreamingRecyclerViewAdapter extends RecyclerView.Adapter<StreamingRecyclerViewAdapter.ViewHolder> {

    private List<Streaming> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public StreamingRecyclerViewAdapter(Context context, List<Streaming> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        if(data.isEmpty()){
            Streaming empty = new Streaming();
            empty.setImageUrl("");
            empty.setPrice(context.getResources().getString(R.string.NoStreams));
            this.mData.add(empty);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.streaming_network, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Streaming streamingNetwork = mData.get(position);
        holder.myTextView.setText(streamingNetwork.getPrice());
        final ImageLoaderStreaming imageLoader = new ImageLoaderStreaming(context);
        imageLoader.DisplayImage(streamingNetwork.getImageUrl(), holder.imageButton);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton imageButton;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textViewStreamingNetwork);
            imageButton = itemView.findViewById(R.id.imageButtonStreamingNetwork);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null &&
                    (mData.get(getAdapterPosition()).price == null
                            || !mData.get(getAdapterPosition()).price.equalsIgnoreCase(context.getResources().getString(R.string.NoStreams)))) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    public Streaming getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}