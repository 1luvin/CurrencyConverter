package com.valance.ency.data

import android.content.Context
import android.content.SharedPreferences
import com.valance.ency.ui.main.MainActivity

object UserConfig {

    private val context: Context get() = MainActivity.getInstance().applicationContext

    private const val CURRENCIES: String = "CURRENCIES"
    private const val SEPARATOR: String = "|"

    private const val THEME: String = "THEME"

    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            "${context.packageName}_${this::class.java.simpleName}",
            Context.MODE_PRIVATE
        )

    var currencies: List<Currency>
        get() {
            val saved = prefs.getString(CURRENCIES, "")!!
            return if (saved.isEmpty()) {
                listOf()
            } else {
                saved.split(SEPARATOR).map { Currency.valueOf(it) }
            }
        }
        set(value) = prefs.edit().putString(CURRENCIES, value.joinToString(SEPARATOR)).apply()

    fun addCurrencies(currencies: List<Currency>) {
        this.currencies += currencies
    }

    fun removeCurrency(currency: Currency) {
        this.currencies -= currency
    }

    fun clearCurrencies() {
        this.currencies = listOf()
    }

    var theme: Theme
        get() = Theme.valueOf(prefs.getString(THEME, Theme.System.name)!!)
        set(value) = prefs.edit().putString(THEME, value.name).apply()
}