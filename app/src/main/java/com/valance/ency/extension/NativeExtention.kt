package com.valance.ency.extension

import com.valance.ency.ui.main.MainActivity
import kotlin.math.roundToInt

private val density: Float get() = MainActivity.instance.resources.displayMetrics.density

/*
    Int
 */

val Int.dp get(): Int {
    return if (this == 0) {
        0
    } else {
        (this * density).roundToInt()
    }
}

/*
    Float
 */

val Float.dp get(): Float {
    return if (this == 0f) {
        0f
    } else {
        this * density
    }
}

val Float.px get(): Float {
    return if (this == 0f) {
        0f
    } else {
        this / density
    }
}

/*
    Boolean
 */

val Boolean.asFloat get(): Float = if (this) 1f else 0f