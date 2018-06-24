package com.imaec.workchecker;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by imaec on 2018-05-24.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private Context context;
    private ItemTouchHelperListener listener;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_FOOTER = 1;

    public ItemTouchHelperCallback(Context context, ItemTouchHelperListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getItemViewType() == VIEW_ITEM) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            return 0;
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == 16) {
            listener.onItemLeftSwipe(viewHolder.getAdapterPosition());
        } else {
            listener.onItemRightSwipe(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
//        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//
//        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            listener.onItemSwipe(dX);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
