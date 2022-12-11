package com.valance.ency.util

import android.content.Context
import android.content.res.Configuration
import androidx.core.view.WindowCompat
import com.valance.ency.ui.main.MainActivity

object Theme {

    private val context: Context get() = MainActivity.instance

    private var colors: HashMap<String, Int> = HashMap()

    const val color_bg: String = "color_bg"
    const val color_bg2: String = "color_bg2"
    const val color_text: String = "color_text"
    const val color_text2: String = "color_text2"
    const val color_positive: String = "color_positive"
    const val color_neutral: String = "color_neutral"
    const val color_negative: String = "color_negative"

    init {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                colors.apply {
                    put(color_bg, 0xFFFFFFFF.toInt())
                    put(color_bg2, 0xFFDDDDDD.toInt())
                    put(color_text, 0xFF111726.toInt())
                    put(color_text2, 0xFF1B2642.toInt())
                    put(color_positive, 0xFF00B59F.toInt())
                    put(color_neutral, 0xFF1B2642.toInt())
                    put(color_negative, 0xFFFF4657.toInt())
                }

                val window = MainActivity.instance.window
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                colors.apply {
                    put(color_bg, 0xFF111726.toInt())
                    put(color_bg2, 0xFF1B2642.toInt())
                    put(color_text, 0xFFFFFFFF.toInt())
                    put(color_text2, 0xFFBBBBBB.toInt())
                    put(color_positive, 0xFF00B59F.toInt())
                    put(color_neutral, 0xFFBBBBBB.toInt())
                    put(color_negative, 0xFFFF4657.toInt())
                }
            }
        }
    }

    fun color(colorKey: String): Int = colors[colorKey] ?: 0xFF000000.toInt()
}