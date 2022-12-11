package com.valance.ency.util

import android.graphics.Paint
import com.valance.ency.R
import com.valance.ency.extension.dp

object SharedResource {

    val dividerPaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp
        color = Theme.color(Theme.color_bg2)
    }
}