package com.valance.ency.ui.custom.button

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import com.valance.ency.extension.asFloat
import com.valance.ency.extension.textSizeDp
import com.valance.ency.util.*

@SuppressLint("ViewConstructor")
class StateButton(
    context: Context,
    text: String,
    onClick: () -> Unit
) : TextView(context) {

    private var available: Boolean = false

    init {
        background = DrawableUtil.rect(
            color = Theme.color(Theme.color_bg2),
            corner = Float.MAX_VALUE
        )

        setTextColor(Theme.color(Theme.color_text2))
        textSizeDp = 18f
        typeface = Font.Bold
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
        gravity = Gravity.CENTER

        this.text = text
        isAllCaps = true

        setOnClickListener {
            onClick()
        }

        isEnabled = false
    }

    fun setState(available: Boolean, animated: Boolean) {
        if (available == this.available) return

        if (animated) {
            ValueAnimator.ofFloat((!available).asFloat, available.asFloat).apply {
                duration = 150

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        if (!available) {
                            isEnabled = false
                        }
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (available) {
                            isEnabled = true
                        }
                    }
                })

                addUpdateListener {
                    val v = it.animatedValue as Float
                    background = DrawableUtil.rect(
                        color = ColorUtil.mixColors(
                            color1 = Theme.color(Theme.color_bg2),
                            color2 = Theme.color(Theme.color_positive),
                            v
                        ),
                        corner = DrawableUtil.CORNER_MAX
                    )
                    setTextColor(
                        ColorUtil.mixColors(
                            color1 = Theme.color(Theme.color_text2),
                            color2 = Color.WHITE,
                            v
                        )
                    )
                }

                start()
            }
        } else {
            val v = available.asFloat
            background = DrawableUtil.rect(
                color = ColorUtil.mixColors(
                    color1 = Theme.color(Theme.color_bg2),
                    color2 = Theme.color(Theme.color_positive),
                    v
                ),
                corner = DrawableUtil.CORNER_MAX
            )
            setTextColor(
                ColorUtil.mixColors(
                    color1 = Theme.color(Theme.color_text2),
                    color2 = Color.WHITE,
                    v
                )
            )
            isEnabled = available
        }

        this.available = available
    }
}