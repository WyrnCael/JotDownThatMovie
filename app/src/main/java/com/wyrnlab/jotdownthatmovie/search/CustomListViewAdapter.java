package com.wyrnlab.jotdownthatmovie.search;

import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;

import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.images.ImageHandler;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {
 
	Context context;
	List<RowItem> items;
    ViewHolder holder;
    ImageLoader imageLoader;
 
    public CustomListViewAdapter(Context context, int resourceId,
            List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
        this.items = items;
        this.imageLoader=new ImageLoader(context);
    }
     
    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDesc;
        ImageView icon;
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        
        RowItem rowItem = this.items.get(position);       
        
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.icon = (ImageView) convertView.findViewById(R.id.iconType);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        
        holder.imageView.setTag(rowItem.getImageId());
        holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());

        if(rowItem.getType() == null || rowItem.getType().equalsIgnoreCase("Movie")){
            holder.icon.setImageResource(R.drawable.video_camera);
        } else {
            holder.icon.setImageResource(R.drawable.tv);
        }

        if(rowItem.getImageId() instanceof  String)
            imageLoader.DisplayImage((String) rowItem.getImageId(), holder.imageView);
        else {
            holder.imageView.setImageBitmap(ImageHandler.getImage((byte[]) rowItem.getImageId()));
        }
         
        return convertView;
    }

    public void clearCache(){
        imageLoader.clearCache();
    }
   
}