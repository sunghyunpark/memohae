package com.yssh.memohae.helper;

/**
 * https://github.com/iPaulPro/Android-ItemTouchHelper-Demo
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
