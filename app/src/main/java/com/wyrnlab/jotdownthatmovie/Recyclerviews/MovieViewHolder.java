package com.wyrnlab.jotdownthatmovie.Recyclerviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView imageView;
    TextView txtTitle;
    TextView txtDesc;
    ImageView icon;
    Context context;
    ImageLoader imageLoader;
    RowItem item;

    public MovieViewHolder(Context context, View itemView) {
        super(itemView);

        this.context = context;
        this.txtDesc = (TextView) itemView.findViewById(R.id.desc);
        this.txtTitle = (TextView) itemView.findViewById(R.id.title);
        this.imageView = (ImageView) itemView.findViewById(R.id.icon);
        this.icon = (ImageView) itemView.findViewById(R.id.iconType);
        this.imageLoader=new ImageLoader(context);

        itemView.setOnClickListener(this);
    }

    public void setMovieRow(RowItem rowItem) {
        this.item = rowItem;
        this.imageView.setTag(rowItem.getImageId());
        this.txtDesc.setText(rowItem.getDesc());
        this.txtTitle.setText(rowItem.getTitle());

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
    }

    @Override
    public void onClick(View v) {
        if(this.item.getSource() == General.DB_SOURCE){
            sendIntentDB();
        } else {
            sendIntentNET();
        }
    }

    private void sendIntentDB(){
        AudiovisualInterface pelicula = (AudiovisualInterface) (item).getObject();

        Intent intent;
        if(pelicula.getTipo() == null || pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)){
            intent =  new Intent(context, InfoMovieDatabase.class);
        } else {
            intent =  new Intent(context, InfoTVShowDatabase.class);
        }
        intent.putExtra("Pelicula", pelicula);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_A);
    }

    private void sendIntentNET(){
        AudiovisualInterface pelicula = (AudiovisualInterface) (item).getObject();

        Intent intent;
        if(pelicula.getTipo().equalsIgnoreCase(General.MOVIE_TYPE)) {
            intent = new Intent(context, InfoMovieSearch.class);

        } else {
            intent = new Intent(context, InfoTVShowSearch.class);
        }
        intent.putExtra("Pelicula", pelicula);
        intent.putExtra("Type", pelicula.getTipo());
        ((Activity) context).startActivityForResult(intent, General.REQUEST_CODE_PELIBUSCADA);
    }

    public void clearCache(){
        imageLoader.clearCache();
    }
}
