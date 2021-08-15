package com.wyrnlab.jotdownthatmovie.View.Recyclerviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wyrnlab.jotdownthatmovie.ExternalLibraries.lazylist.ImageLoader;
import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.Model.RowItemInterface;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.Utils.ImageHandler;
import com.wyrnlab.jotdownthatmovie.Utils.SetTheLanguages;

import androidx.recyclerview.widget.RecyclerView;

public class MoviePersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,View.OnCreateContextMenuListener {

    ImageView imageView;
    TextView txtTitle;
    TextView txtDesc;
    TextView txtYear;
    ImageView icon;
    ImageView iconCrewCast;
    Context context;
    ImageLoader imageLoader;
    RowItemInterface item;
    RecyclerViewClickListener itemListener;
    int position;
    View itemView;

    public MoviePersonViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;
        this.txtDesc = (TextView) itemView.findViewById(R.id.desc);
        this.txtYear = (TextView) itemView.findViewById(R.id.year);
        this.txtTitle = (TextView) itemView.findViewById(R.id.title);
        this.imageView = (ImageView) itemView.findViewById(R.id.icon);
        this.icon = (ImageView) itemView.findViewById(R.id.iconType);
        this.iconCrewCast = (ImageView) itemView.findViewById(R.id.imageCrewCast);
        this.imageLoader=new ImageLoader(context, false);
        this.itemView = itemView;
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setMovieRow(RowItemInterface rowItem, int position, RecyclerViewClickListener itemListener) {
        this.item = rowItem;
        this.imageView.setTag(rowItem.getImageId());
        this.txtDesc.setText(rowItem.getDesc());
        this.txtYear.setText(rowItem.getYear());
        this.txtTitle.setText(rowItem.getTitle());
        this.itemListener = itemListener;
        this.position = position;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        final Rect bounds = new Rect();
        final Paint paint = new Paint();
        paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.standard_14));
        paint.getTextBounds(rowItem.getTitle(), 0, rowItem.getTitle().length(), bounds);

        final int numLines = (int) Math.ceil((float) bounds.width() / (width - context.getResources().getDimensionPixelSize(R.dimen.standard_100) - context.getResources().getDimensionPixelSize(R.dimen.text_view_height)));

        Log.d(rowItem.getTitle(), String.valueOf(numLines));
        Log.d(String.valueOf(bounds.width()), String.valueOf((width - context.getResources().getDimensionPixelSize(R.dimen.standard_100) - context.getResources().getDimensionPixelSize(R.dimen.text_view_height))));
        if(numLines == 1){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.txtTitle.getLayoutParams();
            params.height = context.getResources().getDimensionPixelSize(R.dimen.standard_30);
            this.txtTitle.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.txtTitle.getLayoutParams();
            params.height = context.getResources().getDimensionPixelSize(R.dimen.standard_40);
            this.txtTitle.setLayoutParams(params);
        }

        if(rowItem.getType() == null || rowItem.getType().equalsIgnoreCase(General.MOVIE_TYPE)){
            this.icon.setImageResource(R.drawable.video_camera);
        } else if(rowItem.getType() == null || rowItem.getType().equalsIgnoreCase(General.TVSHOW_TYPE)) {
            this.icon.setImageResource(R.drawable.tv);
        } else if (rowItem.getType() == null || rowItem.getType().equalsIgnoreCase(General.PERSON_TYPE)) {
            this.icon.setImageResource(R.drawable.person);
        }

        if(rowItem.getRelatedToPersonType().equalsIgnoreCase(General.CREW_TYPE)){
            this.iconCrewCast.setImageResource(R.drawable.crew);
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
