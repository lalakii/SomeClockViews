package cn.lalaki.desk.view

import android.graphics.Region
import java.util.Date

/** Created by huteri on 24/10/2017. */
class ArcSlice {
    lateinit var startTime: Date
    lateinit var endTime: Date
    val region by lazy { Region() }
    var color = 0
}
