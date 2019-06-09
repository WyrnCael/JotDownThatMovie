package com.wyrnlab.jotdownthatmovie.Recyclerviews;

import android.view.ContextMenu;
import android.view.View;

public interface RecyclerViewClickListener {
    public void recyclerViewListClicked(View v, int position);
    public void recyclerViewListLongClicked(View v, int position);
    public void recylerViewCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, int position);
}
