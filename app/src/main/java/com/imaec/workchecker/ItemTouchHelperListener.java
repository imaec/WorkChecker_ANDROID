package com.imaec.workchecker;

/**
 * Created by imaec on 2018-05-24.
 */

public interface ItemTouchHelperListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemLeftSwipe(int position);
    void onItemRightSwipe(int position);
    void onItemSwipe(float dX);
}
