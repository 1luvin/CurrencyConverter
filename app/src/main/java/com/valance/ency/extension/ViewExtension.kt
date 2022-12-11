package com.valance.ency.extension

import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.valance.ency.ui.main.MainActivity

/*
    TextView
 */

var TextView.textSizeDp: Float
    get() = textSize.px
    set(value) = setTextSize(TypedValue.COMPLEX_UNIT_DIP, value)

/*
    EditText
 */

fun EditText.showKeyboard() {
    post {
        WindowCompat.getInsetsController(MainActivity.instance.window, this)
            .show(WindowInsetsCompat.Type.ime())
    }
}

fun EditText.hideKeyboard() {
    post {
        WindowCompat.getInsetsController(MainActivity.instance.window, this)
            .hide(WindowInsetsCompat.Type.ime())
    }
}