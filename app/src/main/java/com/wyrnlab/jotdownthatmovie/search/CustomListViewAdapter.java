package com.wyrnlab.jotdownthatmovie.search;

import java.util.List;

import com.fedorvlasov.lazylist.ImageLoader;

import com.wyrnlab.jotdownthatmovie.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
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
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        
        holder.imageView.setTag(rowItem.getImageId());
        holder.txtDesc.setText(rowItem.getDesc());
        holder.txtTitle.setText(rowItem.getTitle());
        
       imageLoader.DisplayImage(rowItem.getImageId(), holder.imageView);
         
        return convertView;
    }   
   
}