package com.valance.ency.util

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import androidx.core.view.WindowCompat
import androidx.lifecycle.MutableLiveData
import com.valance.ency.data.Theme
import com.valance.ency.data.UserConfig
import com.valance.ency.extension.forceRefresh
import com.valance.ency.ui.main.MainActivity

object ThemeUtil {

    private val context: Context get() = MainActivity.getInstance()

    const val color_bg: String = "color_bg"
    const val color_bg2: String = "color_bg2"
    const val color_text: String = "color_text"
    const val color_text2: String = "color_text2"
    const val color_positive: String = "color_positive"
    const val color_neutral: String = "color_neutral"
    const val color_negative: String = "color_negative"

    private val lightColors: HashMap<String, Int> = HashMap()
    private val darkColors: HashMap<String, Int> = HashMap()

    val colors: MutableLiveData<HashMap<String, Int>> = MutableLiveData()
    private fun getColors(): HashMap<String, Int> = colors.value!!
    private fun setColors(colors: HashMap<String, Int>) {
        this.colors.value = colors
    }

    init {
        lightColors.apply {
            put(color_bg, 0xFFFFFFFF.toInt())
            put(color_bg2, 0xFFDDDDDD.toInt())
            put(color_text, 0xFF111726.toInt())
            put(color_text2, 0xFF1B2642.toInt())
            put(color_positive, 0xFF00B59F.toInt())
            put(color_neutral, 0xFF1B2642.toInt())
            put(color_negative, 0xFFFF4657.toInt())
        }

        darkColors.apply {
            put(color_bg, 0xFF111726.toInt())
            put(color_bg2, 0xFF1B2642.toInt())
            put(color_text, 0xFFFFFFFF.toInt())
            put(color_text2, 0xFFBBBBBB.toInt())
            put(color_positive, 0xFF00B59F.toInt())
            put(color_neutral, 0xFFBBBBBB.toInt())
            put(color_negative, 0xFFFF4657.toInt())
        }

        setTheme(UserConfig.theme, animated = false)
    }

    fun color(colorKey: String): Int = getColors()[colorKey] ?: 0xFF000000.toInt()

    fun setTheme(theme: Theme, animated: Boolean = true) {
        if (animated) {
            val fromColors = getColors()
            val toColors: HashMap<String, Int>

            if (theme == Theme.Light || (theme == Theme.System && isSystemThemeLight)) {
                toColors = lightColors.toMap(HashMap())
                setLightStatusBars(true)
            } else {
                toColors = darkColors.toMap(HashMap())
                setLightStatusBars(false)
            }

            val keys = getColors().keys

            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 350

                addUpdateListener {
                    val v = it.animatedValue as Float
                    keys.forEach { k ->
                        getColors()[k] = ColorUtil.mixColors(fromColors[k]!!, toColors[k]!!, v)
                        setStatusBarColor(getColors()[color_bg]!!)
                        colors.forceRefresh()
                    }
                }

                start()
            }
        } else {
            if (theme == Theme.Light || (theme == Theme.System && isSystemThemeLight)) {
                setColors(lightColors.toMap(HashMap()))
                setLightStatusBars(true)
            } else {
                setColors(darkColors.toMap(HashMap()))
                setLightStatusBars(false)
            }
            setStatusBarColor(color(color_bg))
        }

        UserConfig.theme = theme
    }

    private val isSystemThemeLight: Boolean
        get() {
            return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    true
                }
                else -> false
            }
        }

    private fun setLightStatusBars(flag: Boolean) {
        val window = MainActivity.getInstance().window
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = flag
    }

    private fun setStatusBarColor(color: Int) {
        MainActivity.getInstance().window.statusBarColor = color
    }
}