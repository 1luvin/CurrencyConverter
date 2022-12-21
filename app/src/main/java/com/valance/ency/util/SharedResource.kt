package com.valance.ency.util

import android.graphics.Paint
import com.valance.ency.extension.dp
import com.valance.ency.ui.main.MainActivity

object SharedResource {

    val dividerPaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp
    }

    init {
        ThemeUtil.colors.observe(MainActivity.getInstance()) {
            dividerPaint.color = ThemeUtil.color(ThemeUtil.color_bg2)
        }
    }
}