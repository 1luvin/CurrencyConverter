package com.valance.ency.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.valance.ency.R
import com.valance.ency.ui.main.MainActivity
import com.valance.ency.util.Resource
import org.jsoup.Jsoup

enum class Currency(
    @DrawableRes val flagRes: Int,
    @StringRes val fullNameRes: Int
) {

    USD(
        R.drawable.flag_usd,
        R.string.currency_usd
    ),
    EUR(
        R.drawable.flag_eur,
        R.string.currency_eur
    ),
    GBP(
        R.drawable.flag_gbp,
        R.string.currency_gbp
    ),
    JPY(
        R.drawable.flag_jpy,
        R.string.currency_jpy
    ),
    INR(
        R.drawable.flag_inr,
        R.string.currency_inr
    ),
    CHF(
        R.drawable.flag_chf,
        R.string.currency_chf
    ),
    RUB(
        R.drawable.flag_rub,
        R.string.currency_rub
    ),
    CNY(
        R.drawable.flag_cny,
        R.string.currency_cny
    ),
    PLN(
        R.drawable.flag_pln,
        R.string.currency_pln
    ),
    IDR(
        R.drawable.flag_idr,
        R.string.currency_idr
    ),
    KRW(
        R.drawable.flag_krw,
        R.string.currency_krw
    ),
    ILS(
        R.drawable.flag_ils,
        R.string.currency_ils
    ),
    UAH(
        R.drawable.flag_uah,
        R.string.currency_uah
    ),
    BYN(
        R.drawable.flag_byn,
        R.string.currency_byn
    ),
    ;

    val code: String = name

    val fullName: String = Resource.string(fullNameRes)

    val inputRegex: Regex = Regex("(^[1-9][0-9]*[,.]?[0-9]{0,2}$)|(^[0]$)|(^[0][,.][0-9]{0,2}$)")

    var rateToUSD: Float = 1f

    fun toUSD(amount: Float): Float = if (this == USD) amount else amount / rateToUSD

    companion object {
        val size: Int get() = values().size

        fun collectRates(currencies: List<Currency>, onDone: () -> Unit) {
            currencies.forEachIndexed { index, currency ->
                val url =
                    "https://www.xe.com/currencyconverter/convert/?Amount=1&From=${USD.code}&To=${currency.code}"
                val request = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        val doc = Jsoup.parse(response)
                        var p = doc.select("p.result__BigRate-sc-1bsijpp-1.iGrAod")[0].ownText()
                        p = p.substring(0, p.indexOf(' '))
                        if (p.contains(",")) {
                            p = p.replace(",", "")
                        }
                        currency.rateToUSD = p.toFloat()

                        if (index == currencies.lastIndex) {
                            onDone()
                        }
                    },
                    { /* ignore */ }
                )
                MainActivity.getInstance().requestQueue.add(request)
            }
        }
    }
}