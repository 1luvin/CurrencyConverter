package com.valance.ency.ui.currencies

import android.content.Context
import com.valance.ency.R
import com.valance.ency.data.UserConfig
import com.valance.ency.ui.custom.actionbar.ActionBar
import com.valance.ency.util.Resource

class SelectCurrenciesFragment(context: Context) : BaseCurrenciesFragment(context) {

    override val actionBar: ActionBar
        get() = ActionBar(
            context,
            title = Resource.string(R.string.SelectCurrencies),
            subtitle = Resource.string(R.string.SelectCurrenciesHint)
        )

    override val requiredSelection: Int
        get() = 2

    override fun onResume() {
        UserConfig.clearCurrencies()
    }
}