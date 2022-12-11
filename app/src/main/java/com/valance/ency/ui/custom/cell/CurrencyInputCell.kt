package com.valance.ency.ui.custom.cell

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.text.*
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.contains
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.ItemTouchHelper
import com.valance.ency.R
import com.valance.ency.data.Currency
import com.valance.ency.extension.asFloat
import com.valance.ency.extension.dp
import com.valance.ency.extension.hideKeyboard
import com.valance.ency.extension.showKeyboard
import com.valance.ency.util.*
import com.valance.ency.util.Layout

@SuppressLint("ViewConstructor")
class CurrencyInputCell(context: Context) : FrameLayout(context) {

    companion object {
        val cornerRadius: Float = 10f.dp
    }

    var currency: Currency? = null
        set(value) {
            if (value != null) {
                imageView.setImageResource(value.flagRes)
                textView.text = value.code
            } else {
                imageView.setImageDrawable(null)
                textView.text = null
            }
            field = value
        }

    var onNewAmount: ((Currency, Float) -> Unit)? = null
    var onNoAmount: (() -> Unit)? = null

    private val imageView: ImageView
    private val textView: TextView
    private val editText: EditText
    private var dragView: ImageView? = null

    private val indentDp: Int = 20
    private val imageSizeDp: Int = 40
    private val dragSizeDp: Int = 30

    private val colorBg: Int = Theme.color(Theme.color_bg)
    private val colorBg2: Int = Theme.color(Theme.color_bg2)

    private val inputFilters = arrayOf(object : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            if (currency == null) return ""

            var futureText = dest.replaceRange(dstart, dend, source.subSequence(start, end))
            return if (futureText.matches(currency!!.inputRegex)) {
                if (futureText.contains(',')) {
                    futureText = futureText.replace(Regex(","), ".")
                }
                onNewAmount?.let { it(currency!!, futureText.toString().toFloat()) }
                return null
            } else if (futureText.isEmpty()) {
                onNoAmount?.invoke()
                ""
            } else {
                ""
            }
        }
    })

    init {
        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        addView(
            imageView, Layout.ezFrame(
                imageSizeDp, imageSizeDp,
                Gravity.CENTER_VERTICAL,
                indentDp, 0, 0, 0
            )
        )

        textView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 20f
            typeface = Font.Medium
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.CENTER_VERTICAL
        }
        addView(
            textView, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
                indentDp + imageSizeDp + 5, 0, indentDp, 0
            )
        )

        editText = object : EditText(context) {
            override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
                if (
                    keyCode == KeyEvent.KEYCODE_BACK
                    && (event?.action == KeyEvent.ACTION_UP)
                ) {
                    hideKeyboard()
                    clearFocus()
                }

                return super.onKeyPreIme(keyCode, event)
            }
        }.apply {
            background = DrawableUtil.rect(
                color = Theme.color(Theme.color_bg2),
                corner = 10f.dp
            )
            setPadding(10.dp, 0, 10.dp, 0)

            setHintTextColor(Theme.color(Theme.color_text2))
            hint = "0"

            setTextColor(Theme.color(Theme.color_text))
            textSize = 20f
            typeface = Font.Normal
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.START or Gravity.CENTER_VERTICAL

            keyListener = DigitsKeyListener.getInstance("0123456789,.")
            imeOptions = EditorInfo.IME_ACTION_DONE
            filters = inputFilters
            setSelectAllOnFocus(true)

            setOnFocusChangeListener { _, focused ->
                if (focused) {
                    setValue(destroyTriad(text))
                    selectAll()
                    showKeyboard()
                } else {
                    setValue(createTriad(text))
                }
            }

            setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener if (
                    actionId == EditorInfo.IME_ACTION_DONE
                ) {
                    hideKeyboard()
                    clearFocus()
                    true
                } else false
            }
        }
        addView(
            editText, Layout.ezFrame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.END or Gravity.CENTER_VERTICAL,
                0, 0, indentDp, 0
            )
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(60.dp, MeasureSpec.EXACTLY),
        )

        var w = textView.paint.measureText(Currency.KRW.code).toInt()
        textView.measure(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )

        w =
            measuredWidth - (textView.marginLeft + textView.measuredWidth + indentDp.dp + editText.marginRight)
        editText.measure(
            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((measuredHeight * 7 / 10f).toInt(), MeasureSpec.EXACTLY)
        )
    }

    fun setValue(value: CharSequence) {
        if (value == editText.text) return

        editText.apply {
            filters = arrayOf(object : InputFilter {
                override fun filter(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Spanned?,
                    p4: Int,
                    p5: Int
                ): CharSequence? {
                    return null
                }
            })
            setText(value)
            filters = inputFilters
        }
    }

    fun createTriad(value: CharSequence): CharSequence {
        if (value.isEmpty()) return value

        val i = value.indexOfAny(charArrayOf('.', ','))
        var sign = ""
        var decimal = ""
        val t = if (i == -1) { // не найдено точки или запятой
            value
        } else {
            sign = value[i].toString()
            decimal = value.substring(i + 1)
            value.replaceRange(i, value.length, "")
        }

        var v = t.reversed().chunked(3).joinToString(" ").reversed()
        v += "$sign$decimal"

        return v
    }

    private fun destroyTriad(value: CharSequence): CharSequence {
        val r = Regex(" ")
        if (value.contains(r)) {
            return value.replace(r, "")
        }

        return value
    }

    fun setAppearanceForState(state: Int, flag: Boolean) {
        if (state == ItemTouchHelper.ACTION_STATE_DRAG && flag && dragView == null) {
            val d = Resource.drawable(R.drawable.drag).apply {
                setTint(Theme.color(Theme.color_text2))
            }
            dragView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageDrawable(d)
                alpha = 0f
            }
            addView(
                dragView, Layout.ezFrame(
                    dragSizeDp, dragSizeDp,
                    Gravity.END or Gravity.CENTER_VERTICAL,
                    0, 0, indentDp, 0
                )
            )
        }

        ValueAnimator.ofFloat((!flag).asFloat, flag.asFloat).apply {
            duration = 170

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (flag) {
                        editText.isEnabled = false
                    }
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (!flag) {
                        editText.isEnabled = true

                        dragView?.let {
                            if (contains(it)) removeView(it)
                            dragView = null
                        }
                    }
                }
            })

            addUpdateListener {
                val v = it.animatedValue as Float

                when (state) {
                    ItemTouchHelper.ACTION_STATE_DRAG -> {
                        val c = ColorUtil.mixColors(colorBg, colorBg2, v)
                        setBackgroundColor(c)
                        editText.alpha = 1 - v
                        dragView?.alpha = v
                    }
                    ItemTouchHelper.ACTION_STATE_SWIPE -> {
                        background = DrawableUtil.rect(
                            color = colorBg,
                            corner = cornerRadius * v
                        )
                    }
                }
            }

            start()
        }
    }
}