package com.arbelkilani.clock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.arbelkilani.clock.enumeration.BorderStyle;
import com.arbelkilani.clock.enumeration.analogical.DegreesStep;
import com.arbelkilani.clock.enumeration.analogical.DegreeType;
import com.arbelkilani.clock.enumeration.ClockType;
import com.arbelkilani.clock.enumeration.analogical.ValueDisposition;
import com.arbelkilani.clock.enumeration.analogical.ValueStep;
import com.arbelkilani.clock.enumeration.analogical.ValueType;
import com.arbelkilani.clock.enumeration.StopwatchState;
import com.arbelkilani.clock.enumeration.TimeCounterState;
import com.arbelkilani.clock.enumeration.numeric.NumericFormat;
import com.arbelkilani.clock.global.ClockViewSaveState;
import com.arbelkilani.clock.global.ClockUtils;
import com.arbelkilani.clock.listener.ClockListener;
import com.arbelkilani.clock.listener.StopwatchListener;
import com.arbelkilani.clock.listener.TimeCounterListener;
import com.arbelkilani.clock.model.theme.AnalogicalTheme;
import com.arbelkilani.clock.model.theme.NumericTheme;
import com.arbelkilani.clock.model.StopwatchSavedItem;
import com.arbelkilani.clock.model.theme.StopwatchTheme;
import com.arbelkilani.clock.model.theme.TimeCounterTheme;
import com.arbelkilani.clock.runnable.ClockRunnable;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class Clock extends View {
    private static final int DEFAULT_PRIMARY_COLOR = Color.BLACK;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;
    private static final boolean DEFAULT_STATE = false;
    private static final float DEFAULT_BORDER_THICKNESS = 0.015f;
    private static final int DEFAULT_BORDER_RADIUS = 20;
    private static final int DEFAULT_MIN_RADIUS_ = 10;
    private static final int DEFAULT_MAX_RADIUS = 90;
    private static final int FULL_ANGLE = 360;
    private static final int REGULAR_ANGLE = 90;
    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;
    private static final float DEFAULT_MINUTES_BORDER_FACTOR = 0.4f;
    private static final float DEFAULT_SECONDS_BORDER_FACTOR = 0.9f;
    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;
    private static final float DEFAULT_NEEDLE_STROKE_WIDTH = 0.015f;
    private static final float NEEDLE_LENGTH_FACTOR = 0.9f;
    private static final float DEFAULT_NEEDLE_START_SPACE = 0.05f;
    private static final float DEFAULT_HOURS_VALUES_TEXT_SIZE = 0.08f;
    private static final int QUARTER_DEGREE_STEPS = 90;
    private static final float MINUTES_TEXT_SIZE = 0.050f;

    public void reset() {
        valuesFont = null;
        valueTypeFace = null;
        clockBackground = null;
    }

    // Typed Array
    private ClockType clockType;

    private Drawable clockBackground;
    private float scale = 0.7f;

    private boolean showCenter;
    private int centerInnerColor;
    private int centerOuterColor;
    private boolean showBorder;
    private int borderColor;
    private BorderStyle borderStyle;
    private int borderRadiusRx = DEFAULT_BORDER_RADIUS;
    private int borderRadiusRy = DEFAULT_BORDER_RADIUS;

    private boolean showSecondsNeedle;
    private int needleHoursColor;
    private int needleMinutesColor;
    private int needleSecondsColor;
    private boolean showProgress;
    private int progressColor;
    private boolean showMinutesProgress;
    private int minutesProgressColor;
    private float minutesProgressFactor;
    private boolean showSecondsProgress;
    private int secondsProgressColor;
    private float secondsProgressFactor;

    private boolean showDegrees;
    private int degreesColor;
    private DegreeType degreesType;
    private DegreesStep degreesStep;

    private Typeface valuesFont;
    private int valuesColor;
    private boolean showHoursValues;
    private boolean showMinutesValues;
    private float minutesValuesFactor;
    private ValueStep valueStep;
    private ValueType valueType;
    private ValueDisposition valueDisposition;

    private NumericFormat numericFormat;

    private boolean numericShowSeconds;

    // Attributes
    private int size, centerX, centerY, radius;
    private float defaultThickness;
    private RectF defaultRectF;
    private ClockRunnable mClockRunnable;
    private ClockListener mClockListener;
    private Calendar mCalendar;
    private Handler mHandler;

    // Stopwatch
    private long mStartTime, mMillisecondsTime, mUpdateTime, mTimeBuffer = 0L;
    private int mMilliseconds, mSeconds, mMinutes;
    private StopwatchState mStopwatchState = StopwatchState.stopped;
    private StopwatchListener mStopwatchListener;
    // Time counter
    private TimeCounterState mTimeCounterState = TimeCounterState.stopped;
    private TimeCounterListener mTimeCounterListener;
    private long mTimeCounterValue = 0;
    private long mInitialTimeCounter;
    private Typeface valueTypeFace;


    public Clock(Context context) {
        super(context);
        setSaveEnabled(true);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();
        size = Math.min(widthWithoutPadding, heightWithoutPadding);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    public void setClockHandScale(float scale) {
        this.scale = scale;
    }

    private void init(Context context, AttributeSet attrs) {
        mClockRunnable = new ClockRunnable(this);
        mHandler = new Handler(context.getMainLooper());
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Clock, 0, 0);
        try {
            this.clockType = ClockType.fromId(typedArray.getInt(R.styleable.Clock_clock_type, ClockType.analogical.getId()));

            this.clockBackground = typedArray.getDrawable(R.styleable.Clock_clock_background);

            this.showCenter = typedArray.getBoolean(R.styleable.Clock_show_center, DEFAULT_STATE);
            this.centerInnerColor = typedArray.getColor(R.styleable.Clock_center_inner_color, DEFAULT_PRIMARY_COLOR);
            this.centerOuterColor = typedArray.getColor(R.styleable.Clock_center_outer_color, DEFAULT_SECONDARY_COLOR);

            this.showBorder = typedArray.getBoolean(R.styleable.Clock_show_border, DEFAULT_STATE);
            this.borderColor = typedArray.getColor(R.styleable.Clock_border_color, DEFAULT_PRIMARY_COLOR);
            this.borderStyle = BorderStyle.fromId(typedArray.getInt(R.styleable.Clock_border_style, BorderStyle.rectangle.getId()));
            int typedBorderRadiusX = typedArray.getInt(R.styleable.Clock_border_radius_rx, DEFAULT_BORDER_RADIUS);

            if (typedBorderRadiusX > DEFAULT_MIN_RADIUS_ && typedBorderRadiusX < DEFAULT_MAX_RADIUS) {
                this.borderRadiusRx = typedBorderRadiusX;
            } else {
                throw new IllegalArgumentException("border_radius_rx should be in ]" + DEFAULT_MIN_RADIUS_ + "," + DEFAULT_MAX_RADIUS + "[");
            }

            int typedBorderRadiusY = typedArray.getInt(R.styleable.Clock_border_radius_ry, DEFAULT_BORDER_RADIUS);
            if (typedBorderRadiusY > DEFAULT_MIN_RADIUS_ && typedBorderRadiusY < DEFAULT_MAX_RADIUS) {
                this.borderRadiusRy = typedBorderRadiusY;
            } else {
                throw new IllegalArgumentException("border_radius_ry should be in ]" + DEFAULT_MIN_RADIUS_ + "," + DEFAULT_MAX_RADIUS + "[");
            }

            this.showSecondsNeedle = typedArray.getBoolean(R.styleable.Clock_show_seconds_needle, DEFAULT_STATE);
            this.needleHoursColor = typedArray.getColor(R.styleable.Clock_hours_needle_color, DEFAULT_PRIMARY_COLOR);
            this.needleMinutesColor = typedArray.getColor(R.styleable.Clock_minutes_needle_color, DEFAULT_PRIMARY_COLOR);
            this.needleSecondsColor = typedArray.getColor(R.styleable.Clock_seconds_needle_color, DEFAULT_SECONDARY_COLOR);

            this.showProgress = typedArray.getBoolean(R.styleable.Clock_show_progress, DEFAULT_STATE);
            this.progressColor = typedArray.getColor(R.styleable.Clock_progress_color, DEFAULT_SECONDARY_COLOR);

            this.showMinutesProgress = typedArray.getBoolean(R.styleable.Clock_show_minutes_progress, DEFAULT_STATE);
            this.minutesProgressColor = typedArray.getColor(R.styleable.Clock_minutes_progress_color, DEFAULT_PRIMARY_COLOR);
            this.minutesProgressFactor = typedArray.getFloat(R.styleable.Clock_minutes_progress_factor, DEFAULT_MINUTES_BORDER_FACTOR);

            this.showSecondsProgress = typedArray.getBoolean(R.styleable.Clock_show_seconds_progress, DEFAULT_STATE);
            this.secondsProgressFactor = typedArray.getFloat(R.styleable.Clock_seconds_progress_factor, DEFAULT_SECONDS_BORDER_FACTOR);
            this.secondsProgressColor = typedArray.getColor(R.styleable.Clock_seconds_progress_color, DEFAULT_PRIMARY_COLOR);

            this.showDegrees = typedArray.getBoolean(R.styleable.Clock_show_degree, DEFAULT_STATE);
            this.degreesColor = typedArray.getColor(R.styleable.Clock_degree_color, DEFAULT_PRIMARY_COLOR);
            this.degreesType = DegreeType.fromId(typedArray.getInt(R.styleable.Clock_degree_type, DegreeType.line.getId()));
            this.degreesStep = DegreesStep.fromId(typedArray.getInt(R.styleable.Clock_degree_step, DegreesStep.full.getId()));

            this.valuesColor = typedArray.getColor(R.styleable.Clock_values_color, DEFAULT_PRIMARY_COLOR);
            this.showHoursValues = typedArray.getBoolean(R.styleable.Clock_show_hours_values, DEFAULT_STATE);
            this.showMinutesValues = typedArray.getBoolean(R.styleable.Clock_show_minutes_value, DEFAULT_STATE);
            this.minutesValuesFactor = typedArray.getFloat(R.styleable.Clock_minutes_values_factor, DEFAULT_MINUTES_BORDER_FACTOR);
            this.valueStep = ValueStep.fromId(typedArray.getInt(R.styleable.Clock_clock_value_step, ValueStep.full.getId()));
            this.valueType = ValueType.fromId(typedArray.getInt(R.styleable.Clock_clock_value_type, ValueType.none.getId()));
            this.valueDisposition = ValueDisposition.fromId(typedArray.getInt(R.styleable.Clock_clock_value_disposition, ValueDisposition.regular.getId()));

            this.numericFormat = NumericFormat.fromId(typedArray.getInt(R.styleable.Clock_numeric_format, NumericFormat.hour_12.getId()));

            this.numericShowSeconds = typedArray.getBoolean(R.styleable.Clock_numeric_show_seconds, DEFAULT_STATE);

            typedArray.recycle();

        } catch (RuntimeException ex) {
            ex.fillInStackTrace();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        onPreDraw(canvas);

        mCalendar = Calendar.getInstance();

        if (mClockListener != null) {
            mClockListener.getCalendar(mCalendar);
        }

        switch (clockType) {

            case analogical:
                drawAnalogicalClock(canvas);
                break;

            case numeric:
                drawNumericClock(canvas);
                break;

            case stopwatch:
                drawStopWatch(canvas);
                break;

            case time_counter:
                drawTimeCounter(canvas);
                break;
        }
    }

    private void drawAnalogicalClock(Canvas canvas) {
        drawBackground(canvas);
        drawBorder(canvas);
        drawHoursValues(canvas);
        drawMinutesValues(canvas);
        drawDegrees(canvas);
        drawNeedles(canvas);
        drawCenter(canvas);
    }

    private void onPreDraw(Canvas canvas) {

        this.size = Math.min(getHeight(), getWidth());
        this.centerX = size / 2;
        this.centerY = size / 2;
        this.radius = (int) ((this.size * (1 - DEFAULT_BORDER_THICKNESS)) / 2);

        this.defaultThickness = this.size * DEFAULT_BORDER_THICKNESS;
        this.defaultRectF = new RectF(
                defaultThickness, defaultThickness,
                this.size - defaultThickness, this.size - defaultThickness);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(defaultThickness);
    }

    private void drawMinutesValues(Canvas canvas) {

        if (!showMinutesValues)
            return;
        Rect rect = new Rect();
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(minutesProgressColor);
        if(valueTypeFace!=null){
            textPaint.setTypeface(valueTypeFace);
        }
        textPaint.setTextSize(size * MINUTES_TEXT_SIZE);

        int rText = (int) (centerX - ((1 - minutesValuesFactor - (2 * DEFAULT_BORDER_THICKNESS) - MINUTES_TEXT_SIZE) * radius));

        for (int i = 0; i < FULL_ANGLE; i = i + QUARTER_DEGREE_STEPS) {

            int value = i / 6;
            String formatted = switch (valueType) {
                case arabic -> ClockUtils.toArabic(value);
                case roman -> ClockUtils.toRoman(value);
                default -> String.format(Locale.getDefault(), "%02d", value);
            };

            int textX = (int) (centerX + rText * Math.cos(Math.toRadians(REGULAR_ANGLE - i)));
            int textY = (int) (centerX - rText * Math.sin(Math.toRadians(REGULAR_ANGLE - i)));
            textPaint.getTextBounds(formatted, 0, formatted.length(), rect);
            canvas.drawText(formatted, textX - (float) rect.width() / formatted.length(), textY + (float) rect.height() / formatted.length(), textPaint);
        }
    }

    private void drawDegrees(Canvas canvas) {

        if (!showDegrees)
            return;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(size * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = centerX - (int) (size * (DEFAULT_BORDER_THICKNESS + 0.03f));
        int rEnd = centerX - (int) (size * (DEFAULT_BORDER_THICKNESS + 0.06f));

        for (int i = 0; i < FULL_ANGLE; i = i + degreesStep.getId()) {

            if ((i % REGULAR_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (centerX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (centerX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (centerX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (centerX - rEnd * Math.sin(Math.toRadians(i)));

            switch (degreesType) {

                case circle:
                    canvas.drawCircle(stopX, stopY, size * DEFAULT_DEGREE_STROKE_WIDTH, paint);
                    break;

                case square:
                    canvas.drawRect(startX, startY, startX + (size * DEFAULT_DEGREE_STROKE_WIDTH), startY + (size * DEFAULT_DEGREE_STROKE_WIDTH), paint);
                    break;

                default:
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    break;
            }
        }
    }

    private void drawTimeCounter(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(this.borderColor);
        paint.setAntiAlias(true);

        if (clockBackground != null) {

            Bitmap bitmap = ((BitmapDrawable) clockBackground).getBitmap();
            RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas tCanvas = new Canvas(output);

            if (showProgress) {
                tCanvas.drawCircle(centerX, centerY, radius, paint);
            } else {
                switch (borderStyle) {
                    case rectangle:
                        tCanvas.drawRect(defaultRectF, paint);
                        break;

                    case circle:
                        tCanvas.drawCircle(centerX, centerY, radius, paint);
                        break;

                    case rounded_rectangle:
                        float rx = radius - (radius * (100 - borderRadiusRx)) / 100f;
                        float ry = radius - (radius * (100 - borderRadiusRy)) / 100f;
                        tCanvas.drawRoundRect(defaultRectF, rx, ry, paint);
                        break;
                }
            }


            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            tCanvas.drawBitmap(bitmap, null, rectF, paint);
            canvas.drawBitmap(output, null, rectF, new Paint());
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size * DEFAULT_BORDER_THICKNESS);

        if (showProgress)
            canvas.drawCircle(centerX, centerY, radius, paint);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(size * 0.25f);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setColor(this.valuesColor);
        if (valueTypeFace != null) {
            textPaint.setTypeface(valueTypeFace);
        } else {
            textPaint.setTypeface(this.valuesFont);
        }
        Paint progressArcPaint = new Paint();
        progressArcPaint.setAntiAlias(true);
        progressArcPaint.setColor(this.progressColor);
        progressArcPaint.setStyle(Paint.Style.STROKE);
        progressArcPaint.setStrokeWidth(size * DEFAULT_BORDER_THICKNESS);
        progressArcPaint.setStrokeCap(Paint.Cap.ROUND);

        RectF rectF = new RectF(this.centerX - this.radius, this.centerY - this.radius, this.centerX + this.radius, this.centerY + this.radius);

        long timeCounterSweepAngle = (long) ((float) mTimeCounterValue / mInitialTimeCounter * FULL_ANGLE);
        if (showProgress)
            canvas.drawArc(rectF, -REGULAR_ANGLE, FULL_ANGLE - (FULL_ANGLE - timeCounterSweepAngle), false, progressArcPaint);

        String value = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mTimeCounterValue),
                TimeUnit.MILLISECONDS.toSeconds(mTimeCounterValue) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeCounterValue)));

        SpannableStringBuilder spannableString = new SpannableStringBuilder(value);
        StaticLayout layout = StaticLayout.Builder.obtain(spannableString, 0, spannableString.length(), textPaint, canvas.getWidth())
                .setAlignment(Layout.Alignment.ALIGN_CENTER).setIncludePad(true)
                .setLineSpacing(1,1).build();
        canvas.translate(this.centerX - (float) layout.getWidth() / 2, this.centerY - (float) layout.getHeight() / 2);

        layout.draw(canvas);
    }

    private void drawStopWatch(Canvas canvas) {


        if (showBorder) {
            drawCustomBorder(canvas);
        }

        if (clockBackground != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            Bitmap bitmap = ((BitmapDrawable) clockBackground).getBitmap();
            RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas tCanvas = new Canvas(output);
            switch (borderStyle) {
                case rectangle:
                    tCanvas.drawRect(defaultRectF, paint);
                    break;

                case circle:
                    tCanvas.drawCircle(centerX, centerY, radius, paint);
                    break;

                case rounded_rectangle:
                    float rx = radius - (radius * (100 - borderRadiusRx)) / 100f;
                    float ry = radius - (radius * (100 - borderRadiusRy)) / 100f;
                    tCanvas.drawRoundRect(defaultRectF, rx, ry, paint);
                    break;
            }

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            tCanvas.drawBitmap(bitmap, null, rectF, paint);
            canvas.drawBitmap(output, null, rectF, new Paint());
        }


        TextPaint textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(size * 0.25f);
        textPaint.setColor(this.valuesColor);
        if (valueTypeFace != null) {
            textPaint.setTypeface(valueTypeFace);
        } else {
            textPaint.setTypeface(this.valuesFont);
        }
        String stopwatchValue = String.format(Locale.getDefault(), "%02d", mMinutes) + ":" + String.format(Locale.getDefault(), "%02d", mSeconds);
        SpannableStringBuilder spannableString = new SpannableStringBuilder(stopwatchValue);
        StaticLayout layout = StaticLayout.Builder.obtain(spannableString, 0, spannableString.length(), textPaint, canvas.getWidth())
                .setAlignment(Layout.Alignment.ALIGN_CENTER).setIncludePad(true)
                .setLineSpacing(1,1).build();
        canvas.translate(centerX - (float) layout.getWidth() / 2, centerY - (float) layout.getHeight() / 2);

        layout.draw(canvas);
    }

    private void drawNumericClock(Canvas canvas) {

        if (showBorder) {
            drawCustomBorder(canvas);
        }

        if (clockBackground != null) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            Bitmap bitmap = ((BitmapDrawable) clockBackground).getBitmap();
            RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas tCanvas = new Canvas(output);
            switch (borderStyle) {
                case rectangle:
                    tCanvas.drawRect(defaultRectF, paint);
                    break;

                case circle:
                    tCanvas.drawCircle(centerX, centerY, radius, paint);
                    break;

                case rounded_rectangle:
                    float rx = radius - (radius * (100 - borderRadiusRx)) / 100f;
                    float ry = radius - (radius * (100 - borderRadiusRy)) / 100f;
                    tCanvas.drawRoundRect(defaultRectF, rx, ry, paint);
                    break;
            }

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            tCanvas.drawBitmap(bitmap, null, rectF, paint);
            canvas.drawBitmap(output, null, rectF, new Paint());
        }

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        if(valueTypeFace!=null){
            textPaint.setTypeface(valueTypeFace);
        }else{
            textPaint.setTypeface(valuesFont);
        }

        textPaint.setTextSize(size * 0.22f);
        textPaint.setColor(this.valuesColor);

        SpannableStringBuilder spannableString = new SpannableStringBuilder();

        int amPm = mCalendar.get(Calendar.AM_PM);
        String minute = String.format(Locale.getDefault(), "%02d", mCalendar.get(Calendar.MINUTE));
        String second = String.format(Locale.getDefault(), "%02d", mCalendar.get(Calendar.SECOND));

        if (this.numericShowSeconds) {
            if (this.numericFormat == NumericFormat.hour_12) {
                spannableString.append(String.format(Locale.getDefault(), "%02d", mCalendar.get(Calendar.HOUR)));
                spannableString.append(":");
                spannableString.append(minute);
                spannableString.append(".");
                spannableString.append(second);
                spannableString.append(amPm == Calendar.AM ? "AM" : "PM");
                spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent
            } else {
                spannableString.append(String.format(Locale.getDefault(), "%02d", mCalendar.get(Calendar.HOUR_OF_DAY)));
                spannableString.append(":");
                spannableString.append(minute);
                spannableString.append(".");
                spannableString.append(second);
            }
        } else {
            if (this.numericFormat == NumericFormat.hour_12) {
                spannableString.append(String.format(Locale.getDefault(), "%02d", mCalendar.get(Calendar.HOUR)));
                spannableString.append(":");
                spannableString.append(minute);
                spannableString.append(amPm == Calendar.AM ? "AM" : "PM");
                spannableString.setSpan(new RelativeSizeSpan(0.4f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent
            } else {
                spannableString.append(String.format(Locale.getDefault(), "%02d", mCalendar.get(Calendar.HOUR_OF_DAY)));
                spannableString.append(":");
                spannableString.append(minute);
            }
        }

        StaticLayout layout = StaticLayout.Builder.obtain(spannableString, 0, spannableString.length(), textPaint, canvas.getWidth())
                .setAlignment(Layout.Alignment.ALIGN_CENTER).setIncludePad(true)
                .setLineSpacing(1,1).build();
        canvas.translate(centerX - (float) layout.getWidth() / 2, centerY - (float) layout.getHeight() / 2);
        layout.draw(canvas);

    }

    private void drawCustomBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(defaultThickness);

        switch (borderStyle) {
            case rectangle:
                canvas.drawRect(defaultRectF, paint);
                break;

            case circle:
                canvas.drawCircle(centerX, centerY, radius, paint);
                break;

            case rounded_rectangle:
                float rx = radius - (radius * (100 - borderRadiusRx)) / 100f;
                float ry = radius - (radius * (100 - borderRadiusRy)) / 100f;
                canvas.drawRoundRect(defaultRectF, rx, ry, paint);
                break;
        }
    }

    private void drawHoursValues(Canvas canvas) {

        if (!showHoursValues)
            return;

        Rect rect = new Rect();

        TextPaint textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setColor(this.valuesColor);
        if(valueTypeFace!=null){
            textPaint.setTypeface(this.valueTypeFace);
        }else{
            textPaint.setTypeface(this.valuesFont);
        }
        textPaint.setTextSize(size * DEFAULT_HOURS_VALUES_TEXT_SIZE);

        float degreeSpace = 0f;
        if (showDegrees)
            degreeSpace = DEFAULT_DEGREE_STROKE_WIDTH + 0.06f;

        int rText = (int) (centerX - (size * DEFAULT_HOURS_VALUES_TEXT_SIZE) - (size * degreeSpace));

        for (int i = FULL_ANGLE; i > 0; i = i - valueStep.getId()) {

            int value = i / 30;
            String formatted = switch (valueType) {
                case roman -> ClockUtils.toRoman(value);
                case arabic -> ClockUtils.toArabic(value);
                default -> String.format(Locale.getDefault(), "%02d", value);
            };

            if (valueDisposition.getId() == 0) {
                if ((i % REGULAR_ANGLE) == 0) {
                    textPaint.setTextSize(size * DEFAULT_HOURS_VALUES_TEXT_SIZE);
                    textPaint.setAlpha(FULL_ALPHA);
                } else {
                    textPaint.setTextSize(size * (DEFAULT_HOURS_VALUES_TEXT_SIZE - 0.03f));
                    textPaint.setAlpha(CUSTOM_ALPHA);
                }
            } else {
                textPaint.setTextSize(size * DEFAULT_HOURS_VALUES_TEXT_SIZE);
                textPaint.setAlpha(FULL_ALPHA);
            }


            int textX = (int) (centerX + rText * Math.cos(Math.toRadians(REGULAR_ANGLE - i)));
            int textY = (int) (centerX - rText * Math.sin(Math.toRadians(REGULAR_ANGLE - i)));
            textPaint.getTextBounds(formatted, 0, formatted.length(), rect);
            canvas.drawText(formatted, textX - (float) rect.width() / formatted.length(), textY + (float) rect.height() / formatted.length(), textPaint);
        }

    }

    private void drawNeedles(final Canvas canvas) {

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(size * DEFAULT_NEEDLE_STROKE_WIDTH);

        float needleStartSpace = DEFAULT_NEEDLE_START_SPACE;

        float needleMaxLength = getNeedleMaxLength();
        //float needleMaxLength = (radius * NEEDLE_LENGTH_FACTOR) - (degreesSpace + borderThickness + hoursTextSize);

        // draw seconds needle
        float secondsDegree = mCalendar.get(Calendar.SECOND) * 6;

        double cos = Math.cos(Math.toRadians(-REGULAR_ANGLE + secondsDegree));
        float startSecondsX = (float) (centerX + (radius * needleStartSpace) * cos);
        float stopSecondsX = (float) (centerX + needleMaxLength * cos);
        double sin = Math.sin(Math.toRadians(-REGULAR_ANGLE + secondsDegree));
        float startSecondsY = ((float) (centerY + (radius * needleStartSpace) * sin));
        float stopSecondsY = ((float) (centerY + needleMaxLength * sin));

        // draw hours needle
        float hoursDegree = (mCalendar.get(Calendar.HOUR) + (mCalendar.get(Calendar.MINUTE) / 60f)) * 30; // 30 = 360 / 12

        double cos1 = Math.cos(Math.toRadians(-REGULAR_ANGLE + hoursDegree));
        float startHoursX = (float) (centerX + (radius * needleStartSpace) * cos1);
        float stopHoursX = (float) (centerX + (needleMaxLength * 0.6f) * cos1);
        double sin2 = Math.sin(Math.toRadians(-REGULAR_ANGLE + hoursDegree));
        float startHoursY = ((float) (centerY + (radius * needleStartSpace) * sin2));
        float stopHoursY = ((float) (centerY + (needleMaxLength * 0.6f) * sin2));

        // draw minutes needle
        float minutesDegree = (mCalendar.get(Calendar.MINUTE) + (mCalendar.get(Calendar.SECOND) / 60f)) * 6;

        double cos2 = Math.cos(Math.toRadians(-REGULAR_ANGLE + minutesDegree));
        float startMinutesX = (float) (centerX + (radius * needleStartSpace) * cos2);
        float stopMinutesX = (float) (centerX + (needleMaxLength * 0.8f) * cos2);
        double sin1 = Math.sin(Math.toRadians(-REGULAR_ANGLE + minutesDegree));
        float startMinutesY = ((float) (centerY + ((radius - (size * DEFAULT_BORDER_THICKNESS)) * needleStartSpace) * sin1));
        float stopMinutesY = ((float) (centerY + (needleMaxLength * 0.8f) * sin1));

        drawProgressBorder(canvas, hoursDegree, minutesDegree, secondsDegree);

        // hours needle
        paint.setColor(needleHoursColor);
        canvas.drawLine(startHoursX, startHoursY, stopHoursX, stopHoursY, paint);

        // minutes needle
        paint.setColor(needleMinutesColor);
        canvas.drawLine(startMinutesX, startMinutesY, stopMinutesX, stopMinutesY, paint);

        // seconds needle
        paint.setStrokeWidth(size * 0.008f);
        paint.setColor(needleSecondsColor);
        if (showSecondsNeedle) {
            canvas.drawLine(startSecondsX, startSecondsY, stopSecondsX, stopSecondsY, paint);
        }

    }

    private float getNeedleMaxLength() {
        float borderThickness = 0f;
        float hoursTextSize = 0f;
        float degreesSpace = 0f;
        return (radius * NEEDLE_LENGTH_FACTOR) * scale;
    }

    private void drawProgressBorder(final Canvas canvas, final float hoursDegree, float minutesDegree, float secondsDegree) {

        float minuteProgressSpace = (radius - DEFAULT_BORDER_THICKNESS) * minutesProgressFactor;
        RectF minutesRectF = new RectF(centerX - minuteProgressSpace, centerY - minuteProgressSpace, centerX + minuteProgressSpace, centerY + minuteProgressSpace);

        final RectF hoursRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float secondsProgressSpace = (radius - DEFAULT_BORDER_THICKNESS) * secondsProgressFactor;
        final RectF secondsRectF = new RectF(centerX - secondsProgressSpace, centerY - secondsProgressSpace, centerX + secondsProgressSpace, centerX + secondsProgressSpace);

        final Paint progressArcPaint = new Paint();
        progressArcPaint.setAntiAlias(true);
        progressArcPaint.setStyle(Paint.Style.STROKE);
        progressArcPaint.setStrokeWidth(size * DEFAULT_BORDER_THICKNESS);
        progressArcPaint.setStrokeCap(Paint.Cap.ROUND);

        progressArcPaint.setColor(borderColor);

        if (showSecondsProgress) {
            progressArcPaint.setColor(secondsProgressColor);
            if (!showProgress)
                canvas.drawArc(hoursRectF, -REGULAR_ANGLE, secondsDegree, false, progressArcPaint);
            else
                canvas.drawArc(secondsRectF, -REGULAR_ANGLE, secondsDegree, false, progressArcPaint);
        }

        if (showMinutesProgress) {
            progressArcPaint.setColor(borderColor);
            canvas.drawCircle(centerX, centerY, minutesRectF.width() / 2, progressArcPaint);

            progressArcPaint.setColor(minutesProgressColor);
            canvas.drawArc(minutesRectF, -REGULAR_ANGLE, minutesDegree, false, progressArcPaint);
        }

        if (showProgress) {
            progressArcPaint.setColor(progressColor);
            canvas.drawArc(hoursRectF, -REGULAR_ANGLE, hoursDegree, false, progressArcPaint);
        }


    }

    private void drawBorder(Canvas canvas) {

        if (!showBorder)
            return;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(this.borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size * DEFAULT_BORDER_THICKNESS);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawBackground(Canvas canvas) {

        if (clockBackground == null)
            return;

        Bitmap bitmap = ((BitmapDrawable) clockBackground).getBitmap();
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas tCanvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tCanvas.drawCircle(centerX, centerY, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        tCanvas.drawBitmap(bitmap, null, rectF, paint);

        canvas.drawBitmap(output, null, rectF, new Paint());

    }

    private void drawCenter(Canvas canvas) {

        if (!showCenter)
            return;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(centerInnerColor);
        canvas.drawCircle(centerX, centerY, size * 0.015f, paint); // center

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(centerOuterColor);
        paint.setStrokeWidth(size * 0.008f);
        canvas.drawCircle(centerX, centerY, size * 0.02f, paint); // border

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mClockRunnable != null)
            mClockRunnable.run();
    }

    public void setClockListener(ClockListener clockListener) {
        mClockListener = clockListener;
    }

    public void setStopwatchListener(StopwatchListener stopwatchListener) {
        this.mStopwatchListener = stopwatchListener;
    }

    public void runStopwatch() {

        mStartTime = SystemClock.uptimeMillis();
        mHandler.postDelayed(stopwatchRunnable, 0);

        mStopwatchState = StopwatchState.running;
        if (mStopwatchListener != null)
            mStopwatchListener.onStopwatchStateChanged(StopwatchState.running);
    }

    Runnable stopwatchRunnable = new Runnable() {
        @Override
        public void run() {

            mMillisecondsTime = SystemClock.uptimeMillis() - mStartTime;
            mUpdateTime = mTimeBuffer + mMillisecondsTime;

            mSeconds = (int) (mUpdateTime / 1000);
            mMinutes = mSeconds / 60;
            mSeconds = mSeconds % 60;
            mMilliseconds = (int) (mUpdateTime % 1000);

            mHandler.postDelayed(this, 0);
            postInvalidate();
        }
    };

    public void pauseStopwatch() {
        mTimeBuffer += mMillisecondsTime;
        removeStopwatchCallback();
        updateStopwatchState(StopwatchState.paused);
    }

    private void updateStopwatchState(StopwatchState stopwatchState) {
        mStopwatchState = stopwatchState;
        if (mStopwatchListener != null)
            mStopwatchListener.onStopwatchStateChanged(stopwatchState);
    }

    private void updateTimeCounterSate(TimeCounterState timeCounterState) {
        mTimeCounterState = timeCounterState;
        if (mTimeCounterListener != null)
            mTimeCounterListener.onTimeCounterStateChanged(timeCounterState);
    }

    public void resumeStopWatch() {
        mStartTime = SystemClock.uptimeMillis();

        stopwatchRunnable.run();
        updateStopwatchState(StopwatchState.running);
    }

    public StopwatchState getStopwatchState() {
        return mStopwatchState;
    }

    public void stopStopwatch() {
        mMillisecondsTime = 0L;
        mStartTime = 0L;
        mTimeBuffer = 0L;
        mUpdateTime = 0L;

        mSeconds = 0;
        mMinutes = 0;
        mMilliseconds = 0;

        removeStopwatchCallback();
        updateStopwatchState(StopwatchState.stopped);

        invalidate();
    }

    private void removeStopwatchCallback() {
        mHandler.removeCallbacks(stopwatchRunnable);
        new Thread(() -> mHandler.removeCallbacks(stopwatchRunnable));
    }

    public void saveStopwatch() {
        StopwatchSavedItem stopwatchSavedItem = new StopwatchSavedItem(Calendar.getInstance(), mSeconds, mMinutes);
        if (mStopwatchListener != null)
            mStopwatchListener.onStopwatchSaveValue(stopwatchSavedItem);
    }

    public void runTimeCounter(long timeCounterValue) {
        if (timeCounterValue == 0)
            return;

        mTimeCounterValue = timeCounterValue;
        mInitialTimeCounter = timeCounterValue;
        mHandler.postDelayed(timeCounterRunnable, 0);

        updateTimeCounterSate(TimeCounterState.running);
    }

    Runnable timeCounterRunnable = new Runnable() {
        @Override
        public void run() {

            mTimeCounterValue = mTimeCounterValue - 1000;
            mHandler.postDelayed(this, 1000);
            postInvalidate();

            if (mTimeCounterValue == 0L) {
                stopTimeCounter();
            }
        }
    };

    public TimeCounterState getTimeCounterState() {
        return mTimeCounterState;
    }

    public void resumeTimeCounter() {
        timeCounterRunnable.run();
        updateTimeCounterSate(TimeCounterState.running);
    }

    public void pauseTimeCounter() {
        mHandler.removeCallbacks(timeCounterRunnable);
        updateTimeCounterSate(TimeCounterState.paused);
    }

    public void stopTimeCounter() {

        mInitialTimeCounter = 0L;
        mTimeCounterValue = 0L;

        removeTimeCounterCallback();

        updateTimeCounterSate(TimeCounterState.stopped);
        if (mTimeCounterListener != null)
            mTimeCounterListener.onTimeCounterCompleted();

        invalidate();

    }

    private void removeTimeCounterCallback() {
        mHandler.removeCallbacks(timeCounterRunnable);
        new Thread(() -> mHandler.removeCallbacks(timeCounterRunnable));
    }

    public void setTimeCounterListener(TimeCounterListener timeCounterListener) {
        mTimeCounterListener = timeCounterListener;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {

        final Parcelable superState = super.onSaveInstanceState();
        final ClockViewSaveState clockViewSaveState = new ClockViewSaveState(superState);

        // stopwatch
        clockViewSaveState.mSeconds = this.mSeconds;
        clockViewSaveState.mMinutes = this.mMinutes;

        clockViewSaveState.mStartTime = this.mStartTime;
        clockViewSaveState.mTimeBuffer = this.mTimeBuffer;
        clockViewSaveState.mMillisecondsTime = this.mMillisecondsTime;
        clockViewSaveState.mStopwatchState = this.mStopwatchState;

        if (clockViewSaveState.mStopwatchState == StopwatchState.paused) {
            mTimeBuffer += mMillisecondsTime;
        }

        // time counter
        clockViewSaveState.mTimeCounterValue = this.mTimeCounterValue;
        clockViewSaveState.mInitialTimeCounter = this.mInitialTimeCounter;
        clockViewSaveState.mTimeCounterAt = SystemClock.uptimeMillis();
        clockViewSaveState.mTimeCounterState = this.mTimeCounterState;

        return clockViewSaveState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        final ClockViewSaveState clockViewSaveState = (ClockViewSaveState) state;
        super.onRestoreInstanceState(clockViewSaveState.getSuperState());

        // Stopwatch
        this.mSeconds = clockViewSaveState.mSeconds;
        this.mMinutes = clockViewSaveState.mMinutes;

        this.mStartTime = clockViewSaveState.mStartTime;
        this.mTimeBuffer = clockViewSaveState.mTimeBuffer;
        this.mMillisecondsTime = clockViewSaveState.mMillisecondsTime;
        updateStopwatchState(clockViewSaveState.mStopwatchState);

        if (this.mStopwatchState == StopwatchState.running) {
            removeStopwatchCallback();
            mHandler.postDelayed(stopwatchRunnable, 0);
        }

        // Time counter
        this.mInitialTimeCounter = clockViewSaveState.mInitialTimeCounter;
        this.mTimeCounterState = clockViewSaveState.mTimeCounterState;
        updateTimeCounterSate(clockViewSaveState.mTimeCounterState);
        if (mTimeCounterState == TimeCounterState.running) {
            this.mTimeCounterValue = clockViewSaveState.mTimeCounterValue - (SystemClock.uptimeMillis() - clockViewSaveState.mTimeCounterAt);
            removeTimeCounterCallback();
            mHandler.postDelayed(timeCounterRunnable, 0);
        }

        if (mTimeCounterState == TimeCounterState.paused) {
            this.mTimeCounterValue = clockViewSaveState.mTimeCounterValue;
        }

        invalidate();
    }

    /**
     * Ref : <a href="https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/">...</a>
     *
     * @param timezoneCountry Timezone
     */
    public void setTimezone(String timezoneCountry) {
        mCalendar = Calendar.getInstance(TimeZone.getTimeZone(timezoneCountry));
    }


    // Getters

    public ClockType getClockType() {
        return clockType;
    }


    // Setters

    public void setClockType(ClockType clockType) {
        this.clockType = clockType;
        invalidate();
    }

    public void setClockBackground(int clockBackground) {
        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), clockBackground, null);
    }

    public void showCenter(boolean showCenter) {
        this.showCenter = showCenter;
    }

    public void setCenterInnerColor(int centerInnerColor) {
        this.centerInnerColor = ResourcesCompat.getColor(getContext().getResources(), centerInnerColor, null);
    }

    public void setCenterOuterColor(int centerOuterColor) {
        this.centerOuterColor = ResourcesCompat.getColor(getContext().getResources(), centerOuterColor, null);
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = ResourcesCompat.getColor(getContext().getResources(), borderColor, null);
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    public void setBorderRadius(int borderRadiusRx, int borderRadiusRy) {
        this.borderRadiusRx = borderRadiusRx;
        this.borderRadiusRy = borderRadiusRy;
    }

    public void setShowSecondsNeedle(boolean showSecondsNeedle) {
        this.showSecondsNeedle = showSecondsNeedle;
    }

    public void setHoursNeedleColor(int needleHoursColor) {
        this.needleHoursColor = ResourcesCompat.getColor(getContext().getResources(), needleHoursColor, null);
    }

    public void setMinutesNeedleColor(int needleMinutesColor) {
        this.needleMinutesColor = ResourcesCompat.getColor(getContext().getResources(), needleMinutesColor, null);
    }

    public void setSecondsNeedleColor(int needleSecondsColor) {
        this.needleSecondsColor = ResourcesCompat.getColor(getContext().getResources(), needleSecondsColor, null);
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public void setProgressColor(int progressColor) {
        this.centerOuterColor = ResourcesCompat.getColor(getContext().getResources(), progressColor, null);
    }

    public void setShowMinutesProgress(boolean showMinutesProgress) {
        this.showMinutesProgress = showMinutesProgress;
    }

    public void setMinutesProgressColor(int minutesProgressColor) {
        try {
            this.minutesProgressColor = ContextCompat.getColor(getContext(), minutesProgressColor);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void setMinutesProgressFactor(float minutesProgressFactor) {
        this.minutesProgressFactor = minutesProgressFactor;
    }

    public void setShowSecondsProgress(boolean showSecondsProgress) {
        this.showSecondsProgress = showSecondsProgress;
    }

    public void setSecondsProgressFactor(float secondsProgressFactor) {
        this.secondsProgressFactor = secondsProgressFactor;
    }

    public void setSecondsProgressColor(int secondsProgressColor) {
        try {
            this.secondsProgressColor = ContextCompat.getColor(getContext(), secondsProgressColor);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public void setShowDegrees(boolean showDegrees) {
        this.showDegrees = showDegrees;
    }

    public void setDegreesColor(int degreesColor) {
        this.degreesColor = degreesColor;
    }

    public void setDegreesType(DegreeType clockDegreesType) {
        this.degreesType = clockDegreesType;
    }

    public void setDegreesStep(DegreesStep degreesStep) {
        this.degreesStep = degreesStep;
    }

    public void setValuesFont(int valuesFont) {
        this.valuesFont = ResourcesCompat.getFont(getContext(), valuesFont);
    }

    public void setValuesFontType(Typeface typeFace) {
        this.valueTypeFace = typeFace;
    }

    public void setValuesColor(int valuesColor) {
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), valuesColor, null);
    }

    public void setShowHoursValues(boolean showHoursValues) {
        this.showHoursValues = showHoursValues;
    }

    public void setShowMinutesValues(boolean showMinutesValues) {
        this.showMinutesValues = showMinutesValues;
    }

    public void setMinutesValuesFactor(float minutesValuesFactor) {
        this.minutesValuesFactor = minutesValuesFactor;
    }

    public void setValueStep(ValueStep valueStep) {
        this.valueStep = valueStep;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public void setValueDisposition(ValueDisposition valueDisposition) {
        this.valueDisposition = valueDisposition;
    }

    public void setNumericFormat(NumericFormat numericFormat) {
        this.numericFormat = numericFormat;
    }

    public void setNumericShowSeconds(boolean numericShowSeconds) {
        this.numericShowSeconds = numericShowSeconds;
    }

    // Themes
    public void setAnalogicalTheme(AnalogicalTheme analogicalTheme) {

        this.clockType = analogicalTheme.getClockType();

        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), analogicalTheme.getClockBackground(), null);

        this.showCenter = analogicalTheme.isShowCenter();
        this.centerInnerColor = analogicalTheme.getCenterInnerColor();
        this.centerOuterColor = analogicalTheme.getCenterOuterColor();

        this.showBorder = analogicalTheme.isShowBorder();
        this.borderColor = analogicalTheme.getBorderColor();

        this.showSecondsNeedle = analogicalTheme.isShowSecondsNeedle();
        this.needleHoursColor = analogicalTheme.getNeedleHoursColor();
        this.needleMinutesColor = analogicalTheme.getNeedleMinutesColor();
        this.needleSecondsColor = analogicalTheme.getNeedleSecondsColor();

        this.showProgress = analogicalTheme.isShowProgress();
        this.progressColor = analogicalTheme.getProgressColor();
        this.showMinutesProgress = analogicalTheme.isShowMinutesProgress();
        this.minutesProgressColor = analogicalTheme.getMinutesProgressColor();
        this.minutesProgressFactor = analogicalTheme.getMinutesProgressFactor();
        this.showSecondsProgress = analogicalTheme.isShowSecondsProgress();
        this.secondsProgressColor = analogicalTheme.getSecondsProgressColor();
        this.secondsProgressFactor = analogicalTheme.getSecondsProgressFactor();

        this.showDegrees = analogicalTheme.isShowDegrees();
        this.degreesColor = ResourcesCompat.getColor(getContext().getResources(), analogicalTheme.getDegreesColor(), null);
        this.degreesType = analogicalTheme.getDegreesType();
        this.degreesStep = analogicalTheme.getDegreesStep();

        this.valuesFont = ResourcesCompat.getFont(getContext(), analogicalTheme.getValuesFont());
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), analogicalTheme.getValuesColor(), null);
        this.showHoursValues = analogicalTheme.isShowHoursValues();
        this.showMinutesValues = analogicalTheme.isShowMinutesValues();
        this.minutesValuesFactor = analogicalTheme.getMinutesValuesFactor();
        this.valueStep = analogicalTheme.getValueStep();
        this.valueType = analogicalTheme.getValueType();
        this.valueDisposition = analogicalTheme.getValueDisposition();
    }

    public void setNumericTheme(NumericTheme numericTheme) {

        this.clockType = numericTheme.getClockType();

        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), numericTheme.getClockBackground(), null);

        this.valuesFont = ResourcesCompat.getFont(getContext(), numericTheme.getValuesFont());
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), numericTheme.getValuesColor(), null);

        this.showBorder = numericTheme.isShowBorder();
        this.borderColor = numericTheme.getBorderColor();
        this.borderRadiusRx = numericTheme.getBorderRadiusRx();
        this.borderRadiusRy = numericTheme.getBorderRadiusRy();

        this.numericFormat = numericTheme.getNumericFormat();
    }

    public void setStopwatchTheme(StopwatchTheme stopwatchTheme) {

        this.clockType = stopwatchTheme.getClockType();

        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), stopwatchTheme.getClockBackground(), null);

        this.valuesFont = ResourcesCompat.getFont(getContext(), stopwatchTheme.getValuesFont());
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), stopwatchTheme.getValuesColor(), null);

        this.showBorder = stopwatchTheme.isShowBorder();
        this.borderColor = stopwatchTheme.getBorderColor();
        this.borderRadiusRx = stopwatchTheme.getBorderRadiusRx();
        this.borderRadiusRy = stopwatchTheme.getBorderRadiusRy();
    }

    public void setTimeCounterTheme(TimeCounterTheme timeCounterTheme) {

        this.clockType = timeCounterTheme.getClockType();

        this.clockBackground = ResourcesCompat.getDrawable(getContext().getResources(), timeCounterTheme.getClockBackground(), null);

        this.valuesFont = ResourcesCompat.getFont(getContext(), timeCounterTheme.getValuesFont());
        this.valuesColor = ResourcesCompat.getColor(getContext().getResources(), timeCounterTheme.getValuesColor(), null);

        this.showProgress = timeCounterTheme.isShowProgress();
        this.progressColor = timeCounterTheme.getProgressColor();
        this.borderColor = timeCounterTheme.getBorderColor();
    }

}
