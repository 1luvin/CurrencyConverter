package com.valance.ency.ui.converter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valance.ency.data.Currency
import com.valance.ency.data.UserConfig
import com.valance.ency.ui.custom.cell.CurrencyInputCell
import java.util.*
import kotlin.collections.ArrayList

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    private val currencies: ArrayList<Currency> = ArrayList()
    private var values: ArrayList<Float> = ArrayList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            (itemView as CurrencyInputCell).apply {
                onNewAmount = { currency, amount ->
                    values.apply {
                        clear()
                        addAll(currencies.map { currency.toUSD(amount) * it.rateToUSD })
                    }
                    updateItems()
                }
                onNoAmount = {
                    values.clear()
                    updateItems()
                }
            }
        }

        private fun updateItems() {
            for (i in currencies.indices) {
                if (i == adapterPosition) continue
                notifyItemChanged(i, Unit)
            }
        }

        fun bind(currency: Currency) {
            (itemView as CurrencyInputCell).apply {
                this.currency = currency
                if (values.isNotEmpty()) {
                    val value = values[adapterPosition]
                    if (currency in Currency.notFractional) {
                        setValue(createTriad("%.0f".format(value)))
                    } else {
                        setValue(createTriad("%.2f".format(value)))
                    }
                } else {
                    setValue("")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CurrencyInputCell(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount(): Int = currencies.size

    fun setCurrencies(currencies: List<Currency>) {
        if (values.isNotEmpty()) {
            val currency = currencies.first()
            val value = values.first()
            values = currencies.mapTo(ArrayList()) { currency.toUSD(value) * it.rateToUSD }
        }

        val startIndex = this.currencies.size
        var count = 0
        currencies
            .filter { it !in this.currencies }
            .forEach {
                this.currencies.add(it)
                count++
            }

        notifyItemRangeInserted(startIndex, count)
    }

    fun removeCurrencyAt(index: Int) {
        UserConfig.removeCurrency(currencies[index])
        currencies.removeAt(index)
        if (values.isNotEmpty()) {
            values.removeAt(index)
        }
        notifyItemRemoved(index)
    }

    fun moveCurrency(from: Int, to: Int) {
        Collections.swap(currencies, from, to)
        if (values.isNotEmpty()) {
            Collections.swap(values, from, to)
        }
        notifyItemMoved(from, to)
        UserConfig.setCurrencies(currencies)
    }
}