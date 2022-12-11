package com.valance.ency.ui.converter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.valance.ency.R
import com.valance.ency.extension.dp
import com.valance.ency.ui.custom.cell.CurrencyInputCell
import com.valance.ency.ui.main.MainActivity
import com.valance.ency.util.ColorUtil
import com.valance.ency.util.Resource
import com.valance.ency.util.Theme

class CurrencySwipeCallback(
    private val adapter: CurrencyAdapter,
    private val onRemoved: () -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.RIGHT
) {

    private val colorNegative: Int = Theme.color(Theme.color_negative)
    private var swipeColor: Int = colorNegative

    private val swipeDrawable: Drawable = Resource.drawable(R.drawable.close)

    private var wasTaken: Boolean = false
    private var wasReleased: Boolean = false
    private var wasSwiped: Boolean = false
    private var releaseDX: Float = 0f
    private var wasVibrated: Boolean = false
    private var needChangeAppearance: Boolean = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.moveCurrency(viewHolder.adapterPosition, target.adapterPosition)
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.removeCurrencyAt(viewHolder.adapterPosition)
        onRemoved()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.25f
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        holder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        state: Int,
        isActive: Boolean
    ) {
        val view = holder.itemView as CurrencyInputCell

        if ((isActive != wasTaken && state == ItemTouchHelper.ACTION_STATE_DRAG) || (!isActive && needChangeAppearance && state == ItemTouchHelper.ACTION_STATE_SWIPE)) {
            wasTaken = !wasTaken
            needChangeAppearance = !needChangeAppearance
            view.setAppearanceForState(state, isActive)
        }

        if (state != ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(canvas, recyclerView, holder, dX, dY, state, isActive)
            return
        }

        val left = view.left.toFloat()
        val top = view.top.toFloat()
        val right = view.right.toFloat()
        val bottom = view.bottom.toFloat()

        val s = 30.dp
        val diff = (view.bottom - view.top - s) / 2
        val l = view.left + diff
        val t = view.top + diff
        swipeDrawable.setBounds(l, t, l + s, t + s)

        if (isActive && !wasReleased) {
            swipeColor = colorNegative
            swipeDrawable.alpha = 255

            wasSwiped = dX > right * getSwipeThreshold(holder)
            if (wasSwiped != wasVibrated) {
                wasVibrated = !wasVibrated
                MainActivity.instance.vibrate()
            }

            if ((dX == 0f) == needChangeAppearance) {
                needChangeAppearance = !needChangeAppearance
                view.setAppearanceForState(ItemTouchHelper.ACTION_STATE_SWIPE, needChangeAppearance)
            }
        } else if (wasReleased) {
            if (wasSwiped) {
                val r = (right - dX) / (right - releaseDX)
                swipeColor = ColorUtil.mixColors(colorNegative, Color.TRANSPARENT, 1 - r)
                swipeDrawable.alpha = (255 * r).toInt()
            }

            if (dX == 0f || dX == right) {
                wasReleased = false
            }
        } else {
            wasTaken = false
            wasReleased = true
            releaseDX = dX
            wasSwiped = releaseDX > right * getSwipeThreshold(holder)
            wasVibrated = false
        }

        canvas.apply {
            clipRect(left, top, left + dX + CurrencyInputCell.cornerRadius, bottom)
            drawColor(swipeColor)
            swipeDrawable.draw(this)
        }

        super.onChildDraw(canvas, recyclerView, holder, dX, dY, state, isActive)
    }
}