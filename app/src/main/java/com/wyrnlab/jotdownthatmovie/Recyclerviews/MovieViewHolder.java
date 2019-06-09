package com.wyrnlab.jotdownthatmovie.Recyclerviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.mostrarPelicula.InfoMovieDatabase;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.mostrarPelicula.InfoMovieSearch;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.showTVShow.InfoTVShowDatabase;
import com.wyrnlab.jotdownthatmovie.Activities.ShowInfo.showTVShow.InfoTVShowSearch;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.api.search.AudiovisualInterface;
import com.wyrnlab.jotdownthatmovie.data.General;
import com.wyrnlab.jotdownthatmovie.images.ImageHandler;
import com.wyrnlab.jotdownthatmovie.search.RowItem;

import static com.wyrnlab.jotdownthatmovie.Activities.MainActivity.REQUEST_CODE_A;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,View.OnCreateContextMenuListener {

    ImageView imageView;
    TextView txtTitle;
    TextView txtDesc;
    ImageView icon;
    Context context;
    ImageLoader imageLoader;
    Button undoButton;
    RowItem item;
    RecyclerViewClickListener itemListener;
    int position;
    View itemView;

    public MovieViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;
        this.txtDesc = (TextView) itemView.findViewById(R.id.desc);
        this.txtTitle = (TextView) itemView.findViewById(R.id.title);
        this.imageView = (ImageView) itemView.findViewById(R.id.icon);
        this.icon = (ImageView) itemView.findViewById(R.id.iconType);
        this.imageLoader=new ImageLoader(context);
        undoButton = (Button) itemView.findViewById(R.id.undo_button);



        this.itemView = itemView;
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setMovieRow(RowItem rowItem, int position, RecyclerViewClickListener itemListener) {
        this.item = rowItem;
        this.imageView.setTag(rowItem.getImageId());
        this.txtDesc.setText(rowItem.getDesc());
        this.txtTitle.setText(rowItem.getTitle());
        this.itemListener = itemListener;
        this.position = position;

        if(rowItem.getType() == null || rowItem.getType().equalsIgnoreCase("Movie")){
            this.icon.setImageResource(R.drawable.video_camera);
        } else {
            this.icon.setImageResource(R.drawable.tv);
        }

        if(rowItem.getImageId() instanceof  String)
            imageLoader.DisplayImage((String) rowItem.getImageId(), this.imageView);
        else {
            this.imageView.setImageBitmap(ImageHandler.getImage((byte[]) rowItem.getImageId()));
        }

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemListener.recyclerViewListClicked(v, getAdapterPosition());
    }

    public void clearCache(){
        imageLoader.clearCache();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        itemListener.recylerViewCreateContextMenu(menu, v, menuInfo, getAdapterPosition());
    }


    @Override
    public boolean onLongClick(View v) {
        itemListener.recyclerViewListLongClicked(v, getAdapterPosition());
        return false;
    }
}
