package com.valance.ency.ui.custom.cell

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import com.valance.ency.R
import com.valance.ency.data.Currency
import com.valance.ency.extension.asFloat
import com.valance.ency.extension.dp
import com.valance.ency.extension.textSizeDp
import com.valance.ency.util.*

@SuppressLint("ViewConstructor")
class CurrencyCell(
    context: Context,
    val currency: Currency,
    private val needDivider: Boolean
) : FrameLayout(context) {

    private var onCheckedChange: ((Boolean) -> Unit)? = null
    fun onCheckedChange(l: ((Boolean) -> Unit)?) {
        onCheckedChange = l
    }

    private val imageView: ImageView
    private val textView: TextView

    private val textView2: TextView
    private val checkView: ImageView

    private val imageIndentDp: Int = 17
    private val imageSizeDp: Int = 40
    private val checkSizeDp: Int = 30
    private val textRightIndentDp: Int = 20

    var isChecked: Boolean = false
        private set
    private val checkScale: Float = 0.7f

    init {
        setWillNotDraw(!needDivider)
        setPadding(imageIndentDp.dp, 0, 0, 0)

        setOnClickListener {
            setChecked(checked = !isChecked)
            onCheckedChange?.invoke(isChecked)
        }

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(currency.flagRes)
        }
        addView(
            imageView, Layout.ezFrame(
                imageSizeDp, imageSizeDp,
                Gravity.CENTER_VERTICAL
            )
        )

        textView = TextView(context).apply {
            setTextColor(ThemeUtil.color(ThemeUtil.color_text))
            textSizeDp = 18f
            typeface = Font.Medium
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL

            text = currency.fullName
        }
        addView(
            textView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                imageSizeDp + imageIndentDp, 0, 0, 0
            )
        )

        textView2 = TextView(context).apply {
            setTextColor(ThemeUtil.color(ThemeUtil.color_text2))
            textSizeDp = 18f
            typeface = Font.Normal
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL

            text = currency.code
        }
        addView(
            textView2, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
                Gravity.END,
                0, 0, textRightIndentDp, 0
            )
        )

        checkView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.done)
            imageTintList = ColorStateList.valueOf(ThemeUtil.color(ThemeUtil.color_positive))

            alpha = 0f
            translationX = checkSizeDp.dp.toFloat()
        }
        addView(
            checkView, Layout.ezFrame(
                checkSizeDp, checkSizeDp,
                Gravity.END or Gravity.CENTER_VERTICAL,
                0, 0, 16, 0
            )
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((60 + if (needDivider) 1 else 0).dp, MeasureSpec.EXACTLY)
        )

        textView2.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )

        val w =
            measuredWidth - (paddingLeft + textView.marginLeft + 15.dp + textView2.measuredWidth + textRightIndentDp.dp)
        textView.measure(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
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

    private fun setChecked(checked: Boolean) {
        if (checked == isChecked) return

        ValueAnimator.ofFloat((!checked).asFloat, checked.asFloat).apply {
            duration = 150
            addUpdateListener {
                val v = it.animatedValue as Float
                textView2.apply {
                    alpha = 1 - v
                    scaleX = checkScale + (1 - checkScale) * (1 - v)
                    scaleY = scaleX
                }
                checkView.apply {
                    alpha = v
                    scaleX = checkScale + (1 - checkScale) * v
                    scaleY = scaleX
                    translationX = checkSizeDp.dp.toFloat() * (1 - v)
                }
            }
            start()
        }

        isChecked = checked
    }
}