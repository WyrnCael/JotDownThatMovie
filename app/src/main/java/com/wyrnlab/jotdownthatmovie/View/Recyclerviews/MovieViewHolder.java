package com.wyrnlab.jotdownthatmovie.View.Recyclerviews;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItem;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,View.OnCreateContextMenuListener {

    ImageView imageView;
    TextView txtTitle;
    TextView txtYear;
    TextView txtRating;
    ImageView icon;
    ImageView calendar;
    ImageView star;
    Context context;
    ImageLoader imageLoader;
    RowItemInterface item;
    RecyclerViewClickListener itemListener;
    int position;
    View itemView;

    public MovieViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;
        this.txtYear = (TextView) itemView.findViewById(R.id.year);
        this.txtRating = (TextView) itemView.findViewById(R.id.rating);
        this.txtTitle = (TextView) itemView.findViewById(R.id.title);
        this.imageView = (ImageView) itemView.findViewById(R.id.icon);
        this.icon = (ImageView) itemView.findViewById(R.id.iconType);
        this.calendar = (ImageView) itemView.findViewById(R.id.imageYear);
        this.star = (ImageView) itemView.findViewById(R.id.imageStar);
        this.imageLoader=new ImageLoader(context, false);
        this.itemView = itemView;
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setMovieRow(RowItemInterface rowItem, int position, RecyclerViewClickListener itemListener) {
        this.item = rowItem;
        this.imageView.setTag(rowItem.getImageId());
        this.txtYear.setText(rowItem.getYear());
        this.txtRating.setText(rowItem.getRating());
        this.txtTitle.setText(rowItem.getTitle());
        this.itemListener = itemListener;
        this.position = position;

        if(rowItem.getType() == null || rowItem.getType().equalsIgnoreCase(General.MOVIE_TYPE)){
            this.icon.setImageResource(R.drawable.video_camera);
        } else if(rowItem.getType() == null || rowItem.getType().equalsIgnoreCase(General.TVSHOW_TYPE)) {
            this.icon.setImageResource(R.drawable.tv);
        } else if (rowItem.getType() == null || rowItem.getType().equalsIgnoreCase(General.PERSON_TYPE)) {
            this.icon.setImageResource(R.drawable.person);
            this.calendar.setImageResource(R.drawable.known);
            this.txtYear.setText(rowItem.getDesc());
            this.star.setVisibility(View.GONE);
        }

        if(rowItem.getImageId() instanceof String)
            this.imageLoader.DisplayImage((String) rowItem.getImageId(), this.imageView);
        else if (rowItem.getImageId() != null){
            this.imageView.setImageBitmap(ImageHandler.getImage((byte[]) rowItem.getImageId()));
        } else {
            this.imageView.setImageResource(rowItem.getType().equalsIgnoreCase(General.PERSON_TYPE)? SetTheLanguages.getPersonImageStubResourceId() : SetTheLanguages.getImageStubResourceId());
        }

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemListener.recyclerViewListClicked(v, getLayoutPosition());
    }

    public void clearCache(){
        this.imageLoader.clearCache();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        itemListener.recylerViewCreateContextMenu(menu, v, menuInfo, getLayoutPosition());
    }


    @Override
    public boolean onLongClick(View v) {
        itemListener.recyclerViewListLongClicked(v, getLayoutPosition());
        return false;
    }
}
