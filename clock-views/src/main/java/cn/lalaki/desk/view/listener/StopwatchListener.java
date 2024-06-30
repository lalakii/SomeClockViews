package cn.lalaki.desk.view.listener;

import cn.lalaki.desk.view.enumeration.StopwatchState;
import cn.lalaki.desk.view.model.StopwatchSavedItem;

public interface StopwatchListener {

    void onStopwatchStateChanged(StopwatchState stopwatchState);

    void onStopwatchSaveValue(StopwatchSavedItem stopwatchSavedItem);
}
