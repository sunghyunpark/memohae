package com.yssh.memohae.helper;

/**
 * Created by SungHyun on 2018-04-02.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
