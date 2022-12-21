package com.valance.ency.ui.custom.cell

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.valance.ency.R
import com.valance.ency.data.Theme
import com.valance.ency.extension.dp
import com.valance.ency.extension.textSizeDp
import com.valance.ency.util.*

@SuppressLint("ViewConstructor")
class ThemeCell(
    context: Context,
    theme: Theme,
    private val needDivider: Boolean
) : FrameLayout(context) {

    private val imageView: ImageView
    private val textView: TextView
    private var checkView: ImageView? = null

    private val imageIndentDp: Int = 15
    private val imageSizeDp: Int = 40
    private val checkSizeDp: Int = 30

    init {
        setWillNotDraw(!needDivider)
        setPadding(imageIndentDp.dp, 0, imageIndentDp.dp, 0)

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(theme.iconRes)
            imageTintList = ColorStateList.valueOf(ThemeUtil.color(ThemeUtil.color_text2))
        }
        addView(
            imageView, Layout.ezFrame(
                imageSizeDp, imageSizeDp,
                Gravity.CENTER_VERTICAL
            )
        )

        val rightMarginDp: Int
        if (theme.isActive) {
            checkView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(R.drawable.done)
                imageTintList = ColorStateList.valueOf(ThemeUtil.color(ThemeUtil.color_positive))
            }
            addView(
                checkView, Layout.ezFrame(
                    checkSizeDp, checkSizeDp,
                    Gravity.END or Gravity.CENTER_VERTICAL
                )
            )

            rightMarginDp = 10 + checkSizeDp
        } else {
            rightMarginDp = imageIndentDp
        }

        textView = TextView(context).apply {
            setTextColor(ThemeUtil.color(ThemeUtil.color_text))
            textSizeDp = 18f
            typeface = Font.Medium
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL

            text = theme.themeName
        }
        addView(
            textView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                imageSizeDp + imageIndentDp, 0, rightMarginDp, 0
            )
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((60 + if (needDivider) 1 else 0).dp, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (needDivider) {
            canvas.drawLine(
                textView.left.toFloat(),
                height.toFloat(),
                width.toFloat(),
                height.toFloat(),
                SharedResource.dividerPaint
            )
        }
    }
}