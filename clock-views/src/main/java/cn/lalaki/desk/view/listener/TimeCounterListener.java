package cn.lalaki.desk.view.listener;

import cn.lalaki.desk.view.enumeration.TimeCounterState;

public interface TimeCounterListener {

    void onTimeCounterCompleted();

    void onTimeCounterStateChanged(TimeCounterState timeCounterState);

}
