package com.valance.ency.ui.converter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valance.ency.R
import com.valance.ency.data.Currency
import com.valance.ency.data.UserConfig
import com.valance.ency.extension.dp
import com.valance.ency.ui.base.BaseFragment
import com.valance.ency.ui.currencies.AddCurrenciesFragment
import com.valance.ency.ui.currencies.SelectCurrenciesFragment
import com.valance.ency.ui.custom.actionbar.ActionBar
import com.valance.ency.ui.custom.button.StateButton
import com.valance.ency.ui.custom.dialog.ThemeDialog
import com.valance.ency.ui.main.MainActivity
import com.valance.ency.util.Layout
import com.valance.ency.util.NetworkUtil
import com.valance.ency.util.Resource
import kotlinx.coroutines.*

class ConverterFragment(context: Context) : BaseFragment(context) {

    private lateinit var rootLayout: LinearLayout
    private lateinit var actionBar: ActionBar
    private lateinit var currencyRecyclerView: RecyclerView
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var addCurrencyButton: StateButton

    private var ratesJob: Job? = null
    private var themeDialog: ThemeDialog? = null

    /*
        Fragment
     */

    override fun onResume() {
        updateRates()
    }

    override fun onFinish() {
        ratesJob?.cancel()
    }

    /*
        View
     */

    override fun createView(): View {
        actionBar = ActionBar(
            context,
            title = Resource.string(R.string.Converter)
        ).apply {
            setActionButton(R.drawable.theme) {
                showThemeDialog()
            }
        }

        currencyRecyclerView = RecyclerView(context).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(true)
            setPadding(0, 0, 0, 60.dp)
            clipToPadding = false
            itemAnimator?.addDuration = 150

            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            currencyAdapter = CurrencyAdapter()
            adapter = currencyAdapter

            val swipeHandler = CurrencySwipeCallback(
                currencyAdapter,
                onRemoved = {
                    when (currencyAdapter.itemCount) {
                        0 -> pushFragment(SelectCurrenciesFragment(context))
                        Currency.size - 1 -> addCurrencyButton.setState(true, animated = true)
                    }
                }
            )
            ItemTouchHelper(swipeHandler).attachToRecyclerView(this)
        }

        addCurrencyButton = StateButton(
            context,
            text = Resource.string(R.string.AddCurrencies),
            onClick = {
                pushFragment(AddCurrenciesFragment(context))
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
                currencyRecyclerView, Layout.ezLinear(
                    Layout.MATCH_PARENT, 0,
                    weight = 1f
                )
            )

            addView(
                addCurrencyButton, Layout.ezLinear(
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

    private fun updateRates() {
        val neededCurrencies =
            UserConfig.currencies.filter { it != Currency.USD && it.rateToUSD == 1f }
        if (neededCurrencies.isEmpty()) {
            actionBar.setState(ActionBar.State.RATES_UPDATED, animated = false)
            currencyAdapter.setCurrencies(UserConfig.currencies)
            addCurrencyButton.setState(true, animated = false)
            return
        }

        actionBar.setState(ActionBar.State.UPDATING_RATES, animated = false)
        addCurrencyButton.setState(false, animated = false)
        ratesJob = MainActivity.getInstance().lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                if (NetworkUtil.isConnected) {
                    Currency.collectRates(
                        currencies = neededCurrencies,
                        onDone = {
                            actionBar.setState(ActionBar.State.RATES_UPDATED, animated = true)
                            currencyAdapter.setCurrencies(UserConfig.currencies)

                            if (currencyAdapter.itemCount < Currency.size) {
                                addCurrencyButton.setState(true, animated = true)
                            }
                        }
                    )
                    cancel()
                } else {
                    actionBar.setState(ActionBar.State.WAITING_FOR_NETWORK, animated = false)
                }
                delay(50)
            }
        }
    }

    private fun showThemeDialog() {
        if (themeDialog != null) return

        themeDialog = ThemeDialog(context).apply {
            setOnDismissListener {
                themeDialog = null
            }
        }.also {
            it.show()
        }
    }
}