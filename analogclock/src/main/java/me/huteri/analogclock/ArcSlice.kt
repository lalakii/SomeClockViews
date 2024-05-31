package me.huteri.analogclock
import android.graphics.Region
import java.util.Date

/** Created by huteri on 24/10/2017. */
class ArcSlice {
    lateinit var startTime: Date
    lateinit var endTime: Date
    lateinit var region: Region
    var color: Int = 0
}
