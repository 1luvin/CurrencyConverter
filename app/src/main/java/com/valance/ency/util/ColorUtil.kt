package com.valance.ency.util

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils

object ColorUtil {

    fun lighten(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) value: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)

        if (hsv[2] + value < 1) {
            hsv[2] += value
        } else {
            hsv[2] = 1f
        }

        return Color.HSVToColor(hsv)
    }

    fun mixColors(
        @ColorInt color1: Int,
        @ColorInt color2: Int,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float
    ): Int {
        return ColorUtils.blendARGB(color1, color2, ratio)
    }
}