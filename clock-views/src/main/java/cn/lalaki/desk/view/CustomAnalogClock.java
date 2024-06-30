package cn.lalaki.desk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import cn.lalaki.desk.view.R;

/**
 * A widget that displays the time as a 12-at-the-top 24 hour analog clock. By default, it will show
 * the current time in the current timezone. The displayed time can be set using {@link
 * #setTime(long)} and and {@link #setTimezone(TimeZone)}.
 *
 * @author <a href="mailto:steve@staticfree.info">Steve Pomeroy</a>
 */
@SuppressWarnings("unused")
public class CustomAnalogClock extends View {

    public static boolean hours24;
    public static boolean hourOnTop;
    private final ArrayList<DialOverlay> mDialOverlay = new ArrayList<>();
    private Calendar mCalendar;
    private Drawable mFace;
    private int mDialWidth;
    private float sizeScale = 1f;
    private int radius;
    private int mDialHeight;
    private int mBottom;
    private int mTop;
    private int mLeft;
    private int mRight;
    private boolean mSizeChanged;
    private HandsOverlay mHandsOverlay;
    private boolean autoUpdate;
    private final Handler mHandler = new Handler(getContext().getMainLooper());

    public CustomAnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handleAttrs(context, attrs);
    }

    public CustomAnalogClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttrs(context, attrs);
    }

    public CustomAnalogClock(Context context) {
        super(context);
        init(context);
    }

    public CustomAnalogClock(Context context, boolean defaultWatchFace) {
        super(context);
        if (defaultWatchFace) {
            init(context);
        }
    }

    @SuppressWarnings("resource")
    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray =
                getContext().obtainStyledAttributes(attrs, R.styleable.CustomAnalogClock, 0, 0);
        if (typedArray.hasValue(R.styleable.CustomAnalogClock_default_watchface)) {
            if (!typedArray.getBoolean(R.styleable.CustomAnalogClock_default_watchface, true)) {
                typedArray.recycle();
                return;
            }
        }
        init(context);
        typedArray.recycle();
    }

    public void init(Context context) {
        init(
                context,
                R.drawable.default_face,
                R.drawable.default_hour_hand,
                R.drawable.default_minute_hand,
                0,
                false,
                false);
    }

    /**
     * Will set the scale of the view, for example 0.5f will draw the clock with half of its radius
     *
     * @param scale the scale to render the view in
     */
    public void withScale(float scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale must be bigger than 0");
        }
        this.sizeScale = scale;
        mHandsOverlay.withScale(sizeScale);
        invalidate();
    }

    public void setFace(int drawableRes) {
        setFace(getDrawable(drawableRes));
    }

    public Drawable getDrawable(@DrawableRes int id) {
        return ResourcesCompat.getDrawable(getResources(), id, null);
    }

    public void init(
            Context context,
            @DrawableRes int watchFace,
            @DrawableRes int hourHand,
            @DrawableRes int minuteHand,
            int alpha,
            boolean is24,
            boolean hourOnTop) {
        CustomAnalogClock.hours24 = is24;

        CustomAnalogClock.hourOnTop = hourOnTop;
        setFace(watchFace);
        Drawable hHand = getDrawable(hourHand);
        assert hHand != null;
        if (alpha > 0) {
            hHand.setAlpha(alpha);
        }

        Drawable mHand = getDrawable(minuteHand);

        mCalendar = Calendar.getInstance();

        mHandsOverlay = new HandsOverlay(hHand, mHand).withScale(sizeScale);
    }

    public CustomAnalogClock init(
            Context context,
            Drawable watchFace,
            Drawable hourHand,
            Drawable minuteHand,
            int alpha,
            boolean is24,
            boolean hourOnTop) {
        CustomAnalogClock.hours24 = is24;
        CustomAnalogClock.hourOnTop = hourOnTop;
        setFace(watchFace);
        if (alpha > 0) {
            hourHand.setAlpha(alpha);
        }
        mCalendar = Calendar.getInstance();
        mHandsOverlay = new HandsOverlay(hourHand, minuteHand).withScale(sizeScale);
        return this;
    }

    public void setFace(Drawable face) {
        mFace = face;
        mSizeChanged = true;
        mDialHeight = mFace.getIntrinsicHeight();
        mDialWidth = mFace.getIntrinsicWidth();
        radius = Math.max(mDialHeight, mDialWidth);

        invalidate();
    }

    /**
     * Sets the currently displayed time in {@link System#currentTimeMillis()} time.
     *
     * @param time the time to display on the clock
     */
    public void setTime(long time) {
        mCalendar.setTimeInMillis(time);

        invalidate();
    }

    /**
     * Sets the currently displayed time.
     *
     * @param calendar The time to display on the clock
     */
    public void setTime(Calendar calendar) {
        mCalendar = calendar;
        invalidate();
        if (autoUpdate) {
            mHandler.postDelayed(() -> setTime(Calendar.getInstance()), 5000);
        }
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        setTime(Calendar.getInstance());
    }

    /**
     * Sets the timezone to use when displaying the time.
     *
     * @param timezone 时区
     */
    public void setTimezone(TimeZone timezone) {
        mCalendar = Calendar.getInstance(timezone);
    }

    public void setHandsOverlay(HandsOverlay handsOverlay) {
        mHandsOverlay = handsOverlay;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mSizeChanged = true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        final boolean sizeChanged = mSizeChanged;
        mSizeChanged = false;
        final int availW = mRight - mLeft;
        final int availH = mBottom - mTop;
        final int cX = availW / 2;
        final int cY = availH / 2;
        final int w = (int) (mDialWidth * sizeScale);
        final int h = (int) (mDialHeight * sizeScale);
        boolean scaled = false;
        if (availW < w || availH < h) {
            scaled = true;
            final float scale = Math.min((float) availW / (float) w, (float) availH / (float) h);
            canvas.save();
            canvas.scale(scale, scale, cX, cY);
        }
        if (sizeChanged) {
            mFace.setBounds(cX - (w / 2), cY - (h / 2), cX + (w / 2), cY + (h / 2));
        }
        mFace.draw(canvas);
        for (final DialOverlay overlay : mDialOverlay) {
            overlay.onDraw(canvas, cX, cY, w, h, mCalendar, sizeChanged);
        }
        mHandsOverlay.onDraw(canvas, cX, cY, w, h, mCalendar, sizeChanged);
        if (scaled) {
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int finalRadius = (int) (radius * sizeScale);
        setMeasuredDimension(finalRadius, finalRadius);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) (mDialHeight * sizeScale);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) (mDialWidth * sizeScale);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRight = right;
        mLeft = left;
        mTop = top;
        mBottom = bottom;
    }

    public void addDialOverlay(DialOverlay dialOverlay) {
        mDialOverlay.add(dialOverlay);
    }

    public void removeDialOverlay(DialOverlay dialOverlay) {
        mDialOverlay.remove(dialOverlay);
    }

    public void clearDialOverlays() {
        mDialOverlay.clear();
    }
}
