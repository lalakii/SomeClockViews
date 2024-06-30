package cn.lalaki.demo;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.lalaki.demo12315.R;
import cn.lalaki.desk.view.AnalogClockView;
import cn.lalaki.desk.view.Clock;
import cn.lalaki.desk.view.enumeration.ClockType;
import cn.lalaki.desk.view.CustomAnalogClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import cn.lalaki.desk.view.ArcSlice;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    // arbelkilani/Clock-view analogClock
    Clock analogClock = findViewById(R.id.analog_clock);
    analogClock.setClockHandScale(0.6f);
    analogClock.setShowDegrees(true);
    analogClock.setShowSecondsNeedle(true);
    analogClock.setShowHoursValues(true);
    analogClock.setClockType(ClockType.analogical);

    // arbelkilani/Clock-view digitalClock
    Clock digitalClock = findViewById(R.id.digital_clock);
    digitalClock.setClockType(ClockType.numeric);
    digitalClock.setNumericShowSeconds(true);
    digitalClock.setValuesFontType(Typeface.createFromAsset(getAssets(), "arcade_not_free.ttf"));
    // rosenpin/custom-analog-clock-view
    CustomAnalogClock customAnalogClock = findViewById(R.id.custom_analog_clock);
    customAnalogClock.setAutoUpdate(true);
    AssetManager assets = getAssets();
    Resources resources = getResources();
    try {
      Drawable face =
          Drawable.createFromResourceStream(resources, null, assets.open("s7_face.png"), null);
      Drawable hours =
          Drawable.createFromResourceStream(resources, null, assets.open("s7_h.png"), null);
      Drawable minutes =
          Drawable.createFromResourceStream(resources, null, assets.open("s7_m.png"), null);
      customAnalogClock
          .init(customAnalogClock.getContext(), face, hours, minutes, 0, false, true)
          .withScale(3.2f);
    } catch (IOException ignored) {
    }

    // huteri/analogClock
    AnalogClockView analogClockView = findViewById(R.id.analog_clock_huteri);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MINUTE, 0);
    ArcSlice arcSlice = new ArcSlice();
    calendar.set(Calendar.HOUR_OF_DAY, 9);
    arcSlice.setStartTime(calendar.getTime());
    calendar.set(Calendar.HOUR_OF_DAY, 12);
    arcSlice.setEndTime(calendar.getTime());
    arcSlice.setColor(Color.parseColor("#ea4335"));
    ArcSlice arcSlice2 = new ArcSlice();
    calendar.set(Calendar.HOUR_OF_DAY, 12);
    arcSlice2.setStartTime(calendar.getTime());
    calendar.set(Calendar.HOUR_OF_DAY, 17);
    arcSlice2.setEndTime(calendar.getTime());
    arcSlice2.setColor(Color.parseColor("#fbbc05"));
    ArcSlice arcSlice3 = new ArcSlice();
    calendar.set(Calendar.HOUR_OF_DAY, 17);
    arcSlice3.setStartTime(calendar.getTime());
    calendar.set(Calendar.HOUR_OF_DAY, 21);
    arcSlice3.setEndTime(calendar.getTime());
    arcSlice3.setColor(Color.parseColor("#4285f4"));
    analogClockView.setList(
        new ArrayList<>() {
          {
            add(arcSlice);
            add(arcSlice2);
            add(arcSlice3);
          }
        });
  }
}
