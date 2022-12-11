package com.valance.ency.util

import android.graphics.Typeface
import com.valance.ency.ui.main.MainActivity

object Font {

    private val fonts: HashMap<String, Typeface> = HashMap()

    private fun font(style: String): Typeface {
        if (!fonts.containsKey(style)) {
            fonts[style] = Typeface.createFromAsset(
                MainActivity.instance.assets, "font/montserrat_$style.ttf"
            )
        }

        return fonts[style]!!
    }

    val Normal: Typeface get() = font("normal")
    val Medium: Typeface get() = font("medium")
    val Bold: Typeface get() = font("bold")
}