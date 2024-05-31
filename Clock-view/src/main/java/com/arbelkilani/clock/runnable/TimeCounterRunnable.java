package com.arbelkilani.clock.runnable;

import android.view.View;
@SuppressWarnings("unused")
public class TimeCounterRunnable implements Runnable {

    private final View mView;

    public TimeCounterRunnable(View view) {
        mView = view;
    }

    @Override
    public void run() {
        mView.invalidate();
        mView.postDelayed(this, 1000);
    }
}
