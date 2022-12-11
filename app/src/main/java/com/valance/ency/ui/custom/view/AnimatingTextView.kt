package com.valance.ency.ui.custom.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.widget.TextView

class AnimatingTextView(context: Context) : TextView(context) {

    private var colorChanged: Boolean = false

    fun animate(newText: CharSequence, newColor: Int? = null) {
        val l1 = text.length
        val l2 = newText.length

        ValueAnimator.ofInt(0, l1, l1 + l2).apply {
            duration = 200

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    colorChanged = false
                }
            })

            addUpdateListener {
                val v = it.animatedValue as Int
                if (v <= l1) {
                    text = text.dropLast(1)
                    alpha = 1 - v / l1.toFloat()
                } else {
                    if (!colorChanged && newColor != null) {
                        setTextColor(newColor)
                        colorChanged = true
                    }
                    text = newText.take(v - l1)
                    alpha = (v - l1) / l2.toFloat()
                }
            }

            start()
        }
    }
}