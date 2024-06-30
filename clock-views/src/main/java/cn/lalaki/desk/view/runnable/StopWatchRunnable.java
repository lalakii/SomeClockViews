package cn.lalaki.desk.view.runnable;

import android.view.View;

@SuppressWarnings("unused")
public class StopWatchRunnable implements Runnable {

  private final View mView;

  public StopWatchRunnable(View view) {
    mView = view;
  }

  @Override
  public void run() {
    mView.invalidate();
    mView.postDelayed(this, 0);
  }
}
