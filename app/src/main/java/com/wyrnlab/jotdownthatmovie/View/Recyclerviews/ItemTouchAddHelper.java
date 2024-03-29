package com.wyrnlab.jotdownthatmovie.View.Recyclerviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.wyrnlab.jotdownthatmovie.Model.General;
import com.wyrnlab.jotdownthatmovie.R;
import com.wyrnlab.jotdownthatmovie.View.Activities.SearchResultActivity;

public class ItemTouchAddHelper extends androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback {
    Drawable background;
    Drawable xMark;
    int xMarkMargin;
    boolean initiated;
    Context context;
    RecyclerViewAdapter adapter;

    public ItemTouchAddHelper(int dragDirs, int swipeDirs, Activity context, RecyclerViewAdapter adapter){
        super(dragDirs, swipeDirs);
        this.context = context;
        this.adapter = adapter;
    }

    private void init() {
        background = new ColorDrawable(context.getResources().getColor(R.color.verde));
        xMark = ContextCompat.getDrawable(context, R.drawable.ic_add_black_24dp);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
        initiated = true;
    }

    // not important, we don't want drag & drop
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        RecyclerViewAdapter testAdapter = (RecyclerViewAdapter)recyclerView.getAdapter();
        if ((testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) || testAdapter.items.get(position).getType() == General.PERSON_TYPE) {
            return 0;
        }

        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int swipedPosition = viewHolder.getAdapterPosition();
        boolean undoOn = adapter.isUndoOn();
        /*if (undoOn) {
            adapter.pendingRemoval(swipedPosition);
        } else {*/
            adapter.remove(swipedPosition);
        //}
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw red background
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = xMark.getIntrinsicWidth();
        int intrinsicHeight = xMark.getIntrinsicWidth();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


}
