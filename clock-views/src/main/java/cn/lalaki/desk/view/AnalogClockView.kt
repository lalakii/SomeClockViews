package cn.lalaki.desk.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Created by huteri on 19/09/2017.
 */
@Suppress("MemberVisibilityCanBePrivate")
class AnalogClockView(
    context: Context,
    attrs: AttributeSet,
) : View(context, attrs) {
    var list = arrayListOf<ArcSlice>()
        set(value) {
            list.clear()
            list.addAll(value)
            invalidate()
        }
    val minutes = RectF()
    val hours = RectF()
    val seconds = RectF()
    val rectF = RectF()
    val selectedSliceRectF = RectF()
    val refreshHandler = Handler(context.mainLooper)
    private var rectFSelected = RectF()
    private var refreshPerSecond =
        object : Runnable {
            override fun run() {
                postInvalidate()
                refreshHandler.postDelayed(this, 1000)
            }
        }
    private var radius = 400
    private var radiusSelected = radius + 25
    private var innerRadius = radius - 15
    var onSliceClickListener: SliceClickListener? = null
    var clockColor: Int = Color.GRAY
    var hourColor: Int = Color.DKGRAY
    var minuteColor: Int = Color.GRAY
    var secondColor: Int = Color.LTGRAY
    var scaleColor: Int = Color.WHITE
    var minuteThickness: Int = 10
    var minuteLength: Int = 300
    var secondThickness: Int = 5
    var secondLength: Int = 320
    var hourThickness: Int = 10
    var hourLength: Int = 170
    var clockThickness: Int = 15
    var indexSelected: Int = 0

    init {
        refreshPerSecond.run()
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        val min = width.coerceAtMost(height)

        radius = (min - paddingLeft - 90) / 2
        radiusSelected = radius + 25
        innerRadius = radius - clockThickness

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private val paint by lazy { Paint() }
    private val pathSelected by lazy { Path() }

    @Suppress("deprecation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val hour = Calendar.getInstance().get(Calendar.HOUR)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)
        val second = Calendar.getInstance().get(Calendar.SECOND)

        var degreeHour: Float = -180f
        var degreeMinute: Float = -180f
        var degreeSecond: Float = -180f

        //  if (hour > 12) {
        //   hour -= 12
        // }

        degreeHour += (hour * 30) + (minute.toFloat() / 60 * 30)
        degreeMinute += minute.toFloat() / 5 * 30
        degreeSecond += second.toFloat() / 5 * 30

        canvas.let {
            val centerX = width.div(2).toFloat()
            val centerY = height.div(2).toFloat()

            rectF.apply {
                set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            }

            selectedSliceRectF.apply {
                set(
                    centerX - radiusSelected,
                    centerY - radiusSelected,
                    centerX + radiusSelected,
                    centerY + radiusSelected,
                )
            }

            it.drawArc(
                rectF,
                0f,
                360f,
                false,
                paint.apply {
                    color = clockColor
                    strokeWidth = (clockThickness * 2).toFloat()
                    style = Paint.Style.STROKE
                },
            )

            for ((index, i) in list.withIndex()) {
                val startTime = i.startTime
                val endTime = i.endTime

                val calendar = Calendar.getInstance()
                calendar.time = startTime

                val startHour = calendar.get(Calendar.HOUR)
                val startMinute = calendar.get(Calendar.MINUTE)

                val diff = endTime.time - startTime.time

                val inMinutes = TimeUnit.MILLISECONDS.toMinutes(diff)

                val sweepAngle: Float = (inMinutes / 2).toFloat()

                var startAngle: Float = -90f

                //   if (startHour > 12) {
                //     startHour -= 12
                // }

                startAngle += (startHour * 30) + (startMinute.toFloat() / 60 * 30)

                pathSelected.reset()
                rectFSelected.apply {
                    set(
                        centerX - radiusSelected,
                        centerY - radiusSelected,
                        centerX + radiusSelected,
                        centerY + radiusSelected,
                    )
                }

                pathSelected.arcTo(rectFSelected, startAngle, sweepAngle)

                rectFSelected.apply {
                    set(centerX, centerY, centerX, centerY)
                }

                pathSelected.arcTo(rectFSelected, 0f, 0f)
                pathSelected.close()

                pathSelected.computeBounds(rectFSelected, true)

                i.region.set(
                    rectFSelected.left.toInt(),
                    rectFSelected.top.toInt(),
                    rectFSelected.right.toInt(),
                    rectFSelected.bottom.toInt(),
                )
                i.region.setPath(pathSelected, i.region)

                rectFSelected.apply {
                    set(
                        centerX - (radius + 15),
                        centerY - (radius + 15),
                        centerX + (radius + 15),
                        centerY + (radius + 15),
                    )
                }
                dPath.reset()
                dPath.arcTo(rectFSelected, startAngle, sweepAngle)

                rectFSelected.apply {
                    set(
                        centerX - innerRadius,
                        centerY - innerRadius,
                        centerX + innerRadius,
                        centerY + innerRadius,
                    )
                }

                dPath.arcTo(rectFSelected, startAngle + sweepAngle, -sweepAngle)
                dPath.close()

                it.drawPath(
                    dPath,
                    paint.apply {
                        color = i.color
                        style = Paint.Style.FILL
                    },
                )

                if (index == indexSelected) {
                    it.drawArc(
                        selectedSliceRectF,
                        startAngle,
                        sweepAngle,
                        false,
                        paint.apply {
                            color = i.color
                            strokeWidth = 10f
                            style = Paint.Style.STROKE
                        },
                    )
                }
            }

            it.save()

            rectF.apply {
                top += 15
            }

            for (i in 1..30) {
                it.rotate(30f, centerX, centerY)
                it.drawRoundRect(
                    rectF.apply {
                        set(centerX - 3, top, centerX + 3, top + 30)
                    },
                    0f,
                    0f,
                    paint.apply {
                        color = scaleColor
                        style = Paint.Style.FILL
                    },
                )
            }

            it.restore()

            hours.apply {
                set(
                    centerX - hourThickness,
                    centerY - hourThickness,
                    centerX + hourThickness,
                    centerY + hourLength,
                )
            }

            it.save()

            it.rotate(degreeHour, centerX, centerY)

            it.drawRoundRect(
                hours,
                20f,
                20f,
                paint.apply {
                    color = hourColor
                    style = Paint.Style.FILL
                },
            )

            it.restore()

            minutes.apply {
                set(
                    centerX - minuteThickness,
                    centerY - minuteThickness,
                    centerX + minuteThickness,
                    centerY + minuteLength,
                )
            }

            it.save()

            it.rotate(degreeMinute, centerX, centerY)
            it.drawRoundRect(
                minutes,
                20f,
                20f,
                paint.apply {
                    color = minuteColor
                    style = Paint.Style.FILL
                },
            )

            it.restore()

            seconds.apply {
                set(
                    centerX - secondThickness,
                    centerY - secondThickness,
                    centerX + secondThickness,
                    centerY + secondLength,
                )
            }

            it.save()

            it.rotate(degreeSecond, centerX, centerY)
            it.drawRoundRect(
                seconds,
                20f,
                20f,
                paint.apply {
                    color = secondColor
                    style = Paint.Style.FILL
                },
            )

            it.restore()
        }
    }

    private val dPath by lazy { Path() }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val point = Point()
        point.x = event!!.x.toInt()
        point.y = event.y.toInt()

        var count = 0

        for (arcSlice in list) {
            if (arcSlice.region.contains(
                    point.x,
                    point.y,
                ) &&
                event.action == MotionEvent.ACTION_DOWN
            ) {
                indexSelected = count

                onSliceClickListener?.onSliceClick(indexSelected)
                break
            }

            count++
        }

        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP) {
            postInvalidate()
        }

        return true
    }

    interface SliceClickListener {
        fun onSliceClick(pos: Int)
    }
}
