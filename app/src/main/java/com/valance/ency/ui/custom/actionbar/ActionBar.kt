package com.valance.ency.ui.custom.actionbar

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.valance.ency.R
import com.valance.ency.extension.dp
import com.valance.ency.extension.textSizeDp
import com.valance.ency.ui.custom.view.AnimatingTextView
import com.valance.ency.util.*

@SuppressLint("ViewConstructor")
class ActionBar(
    context: Context,
    title: String,
    private val subtitle: String? = null,
    private val onBack: (() -> Unit)? = null,
) : FrameLayout(context) {

    private var imageView: ImageView? = null
    private val textView: TextView
    private var textView2: AnimatingTextView? = null

    private val indentDp: Int = 20
    private val imageSizeDp: Int = 34
    private val imageTextIndentDp: Int = 15

    private var state: State? = null
    private var dotAnimator: ValueAnimator? = null
    private var dotCount: Int = 0

    init {
        val indent = indentDp.dp
        setPadding(indent, 0, indent, 0)

        onBack?.let {
            val d = Resource.drawable(R.drawable.back).apply {
                setTint(Theme.color(Theme.color_text2))
            }
            imageView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER
                background = DrawableUtil.circle(
                    color = Theme.color(Theme.color_bg2)
                )
                setImageDrawable(d)

                setOnClickListener {
                    it()
                }
            }
            addView(
                imageView, Layout.ezFrame(
                    imageSizeDp, imageSizeDp,
                    Gravity.CENTER_VERTICAL
                )
            )
        }

        textView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSizeDp = 30f
            typeface = Font.Bold
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            text = title
        }
        addView(
            textView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL,
                if (onBack != null) imageSizeDp + imageTextIndentDp else 0, 0, 0, 0
            )
        )

        subtitle?.let {
            createAddSubtitle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(90.dp, MeasureSpec.EXACTLY),
        )
    }

    private fun createAddSubtitle() {
        if (textView2 != null) return

        textView.updateLayoutParams<FrameLayout.LayoutParams> {
            bottomMargin = 10.dp
        }

        textView2 = AnimatingTextView(context).apply {
            textSizeDp = 17f
            typeface = Font.Medium
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            subtitle?.let {
                setTextColor(Theme.color(Theme.color_text2))
                text = it
            }
        }
        addView(
            textView2, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL,
                if (onBack != null) imageSizeDp + imageTextIndentDp else 0, 20, indentDp, 0
            )
        )
    }

    private fun animateSubtitleLoading() {
        if (dotAnimator != null && dotAnimator!!.isRunning) return

        dotCount = 0
        dotAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            repeatCount = ValueAnimator.INFINITE

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationRepeat(animation: Animator?) {
                    textView2?.let {
                        dotCount++
                        if (dotCount == 4) dotCount = 0
                        var t = it.text
                        val i = t.indexOf('.')
                        if (i != -1) {
                            t = t.subSequence(0, i)
                        }
                        it.text = "$t${".".repeat(dotCount)}"
                    }
                }
            })

            start()
        }
    }

    private fun cancelDotAnimator() {
        dotAnimator?.let {
            it.cancel()
            dotAnimator = null
        }
    }

    fun setState(state: State, animated: Boolean) {
        if (state == this.state) return

        createAddSubtitle()

        val textColor: Int
        val text: String

        when (state) {
            State.WAITING_FOR_NETWORK -> {
                textColor = Theme.color(Theme.color_negative)
                text = Resource.string(R.string.state_WaitingForNetwork)
            }
            State.UPDATING_RATES -> {
                textColor = Theme.color(Theme.color_neutral)
                text = Resource.string(R.string.state_UpdatingRates)
            }
            State.RATES_UPDATED -> {
                cancelDotAnimator()
                textColor = Theme.color(Theme.color_positive)
                text = Resource.string(R.string.state_RatesUpdated)
            }
        }

        if (animated) {
            textView2?.animate(
                newText = text,
                newColor = textColor
            )
        } else {
            textView2?.apply {
                setTextColor(textColor)
                this.text = text
            }
            if (state != State.RATES_UPDATED) animateSubtitleLoading()
        }

        this.state = state
    }

    enum class State {
        WAITING_FOR_NETWORK,
        UPDATING_RATES,
        RATES_UPDATED
    }
}