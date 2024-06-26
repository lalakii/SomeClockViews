package cn.lalaki.desk.view.model;

import androidx.annotation.NonNull;

import java.util.Calendar;
@SuppressWarnings("unused")
public class StopwatchSavedItem {

    private int mSeconds;
    private int mMinutes;
    private Calendar mCalendar;

    public StopwatchSavedItem(Calendar mCalendar, int mSeconds, int mMinutes) {
        this.mSeconds = mSeconds;
        this.mMinutes = mMinutes;
        this.mCalendar = mCalendar;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void setSeconds(int mSeconds) {
        this.mSeconds = mSeconds;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public void setMinutes(int mMinutes) {
        this.mMinutes = mMinutes;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
    }

    @NonNull
    @Override
    public String toString() {
        return "StopwatchSavedItem{" +
                "mSeconds=" + mSeconds +
                ", mMinutes=" + mMinutes +
                ", mCalendar=" + mCalendar +
                '}';
    }
}
