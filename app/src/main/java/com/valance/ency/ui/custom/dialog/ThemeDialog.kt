package com.valance.ency.ui.custom.dialog

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.valance.ency.R
import com.valance.ency.data.Theme
import com.valance.ency.extension.dp
import com.valance.ency.extension.textSizeDp
import com.valance.ency.ui.custom.cell.ThemeCell
import com.valance.ency.util.*

class ThemeDialog(context: Context) : BottomSheetDialog(context, R.style.BottomSheet) {

    init {
        window?.setDimAmount(0.33f)

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 10.dp, 0, 7.dp)
            background = DrawableUtil.rect(
                color = ThemeUtil.color(ThemeUtil.color_bg),
                corner = 15f.dp
            )
        }

        val header = TextView(context).apply {
            setTextColor(ThemeUtil.color(ThemeUtil.color_text))
            textSizeDp = 23f
            typeface = Font.Bold
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL

            text = Resource.string(R.string.Theme)
        }
        layout.addView(
            header, Layout.ezLinear(
                Layout.MATCH_PARENT, 46,
                20, 0, 20, 0
            )
        )

        val themes = Theme.values()
        themes.forEachIndexed { i, theme ->
            val cell = ThemeCell(context, theme, i != themes.lastIndex).apply {
                setOnClickListener {
                    ThemeUtil.setTheme(theme)
                    dismiss()
                }
            }
            layout.addView(
                cell, Layout.ezLinear(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT
                )
            )
        }

        setContentView(
            layout, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                10, 0, 10, 10
            )
        )
    }
}