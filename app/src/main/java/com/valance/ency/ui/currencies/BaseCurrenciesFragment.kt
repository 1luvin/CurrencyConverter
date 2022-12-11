package com.valance.ency.ui.currencies

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.children
import com.valance.ency.R
import com.valance.ency.data.Currency
import com.valance.ency.data.UserConfig
import com.valance.ency.extension.dp
import com.valance.ency.ui.base.BaseFragment
import com.valance.ency.ui.custom.button.StateButton
import com.valance.ency.ui.custom.actionbar.ActionBar
import com.valance.ency.ui.custom.cell.CurrencyCell
import com.valance.ency.util.*

abstract class BaseCurrenciesFragment(context: Context) : BaseFragment(context) {

    private val currencies: List<Currency> =
        Currency.values().filter { it !in UserConfig.currencies }

    private lateinit var rootLayout: LinearLayout
    protected abstract val actionBar: ActionBar
    private lateinit var scroll: ScrollView
    private lateinit var layout: LinearLayout
    private lateinit var doneButton: StateButton

    protected abstract val requiredSelection: Int

    private var selectedCount: Int = 0
        set(value) {
            doneButton.setState(available = value >= requiredSelection, animated = true)
            field = value
        }

    /*
        View
     */

    override fun createView(): View {
        layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            currencies.forEachIndexed { index, currency ->
                val cell = CurrencyCell(
                    context,
                    currency = currency,
                    needDivider = index != currencies.lastIndex
                ).apply {
                    onCheckedChange {
                        val nextCount = if (it) {
                            selectedCount + 1
                        } else {
                            selectedCount - 1
                        }

                        val s = requiredSelection - 1
                        if (selectedCount == s && nextCount > s) {
                            doneButton.setState(true, animated = true)
                        } else if (selectedCount > s && nextCount == s) {
                            doneButton.setState(false, animated = true)
                        }

                        selectedCount = nextCount
                    }
                }
                addView(
                    cell, Layout.ezLinear(
                        Layout.MATCH_PARENT, Layout.WRAP_CONTENT
                    )
                )
            }
        }
        scroll = ScrollView(context).apply {
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            setPadding(0, 0, 0, 60.dp)
            clipToPadding = false

            addView(
                layout, ViewGroup.LayoutParams(
                    Layout.MATCH_PARENT, Layout.MATCH_PARENT
                )
            )
        }

        doneButton = StateButton(
            context,
            text = Resource.string(R.string.Done),
            onClick = {
                addSelectedCurrencies()
            }
        )

        rootLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL

            addView(
                actionBar, Layout.ezLinear(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT
                )
            )

            addView(
                scroll, Layout.ezLinear(
                    Layout.MATCH_PARENT, 0,
                    weight = 1f
                )
            )

            addView(
                doneButton, Layout.ezLinear(
                    Layout.MATCH_PARENT, 56,
                    20, 15, 20, 15
                )
            )
        }

        return rootLayout
    }

    /*
        Action
     */

    private fun addSelectedCurrencies() {
        val currencies = layout.children
            .map { it as CurrencyCell }
            .filter { it.isChecked }
            .mapTo(mutableListOf()) { it.currency }

        UserConfig.addCurrencies(currencies)

        finish()
    }
}