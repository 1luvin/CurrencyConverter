package com.valance.ency.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.valance.ency.R
import com.valance.ency.util.Resource

enum class Theme(
    @StringRes private val nameRes: Int,
    @DrawableRes val iconRes: Int
) {

    System(
        R.string.theme_System,
        R.drawable.theme_system
    ),
    Light(
        R.string.theme_Light,
        R.drawable.theme_light
    ),
    Dark(
        R.string.theme_Dark,
        R.drawable.theme_dark
    ),
    ;

    val themeName: String get() = Resource.string(nameRes)

    val isActive: Boolean get() = this == UserConfig.theme
}