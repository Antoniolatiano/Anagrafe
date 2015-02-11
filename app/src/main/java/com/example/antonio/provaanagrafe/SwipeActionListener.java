package com.example.antonio.provaanagrafe;

import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

/**
 * Created by Antonio on 06/02/2015.
 */
public class SwipeActionListener implements SwipeActionAdapter.SwipeActionListener {

    MainActivity context;
    SwipeActionAdapter mAdapter;

    public SwipeActionListener(MainActivity context, SwipeActionAdapter mAdapter) {
        this.context = context;
        this.mAdapter = mAdapter;
    }

    @Override
    public boolean hasActions(int i) {
        return true;
    }

    @Override
    public boolean shouldDismiss(int position, int direction) {
        return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
    }

    @Override
    public void onSwipe(int[] positionList, int[] directionList) {
        for (int i = 0; i < positionList.length; i++) {
            int direction = directionList[i];
            int position = positionList[i];
            switch (direction) {
                case SwipeDirections.DIRECTION_FAR_LEFT:
                    context.ModifyUser(position);
                    break;
                case SwipeDirections.DIRECTION_FAR_RIGHT:
                    context.DeleteUser(position);
                    break;
            }
        }
    }
}
