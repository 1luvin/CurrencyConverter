package com.valance.ency.data

import android.content.Context
import android.content.SharedPreferences
import com.valance.ency.ui.main.MainActivity

object UserConfig {

    private val context: Context get() = MainActivity.instance.applicationContext

    private val CURRENCIES: String get() = "CURRENCIES"
    private val SEPARATOR: String get() = "|"

    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            "${context.packageName}_${this::class.java.simpleName}",
            Context.MODE_PRIVATE
        )

    val currencies: List<Currency>
        get() {
            val saved = prefs.getString(CURRENCIES, "")!!
            return if (saved.isEmpty()) {
                listOf()
            } else {
                saved.split(SEPARATOR).map { Currency.valueOf(it) }
            }
        }

    fun setCurrencies(currencies: List<Currency>) {
        prefs.edit().putString(CURRENCIES, currencies.joinToString(SEPARATOR)).apply()
    }

    fun addCurrencies(currencies: List<Currency>) {
        setCurrencies(this.currencies + currencies)
    }

    fun removeCurrency(currency: Currency) {
        setCurrencies(currencies - currency)
    }

    fun clearCurrencies() {
        setCurrencies(listOf())
    }
}