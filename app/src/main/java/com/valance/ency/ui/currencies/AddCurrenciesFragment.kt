package com.valance.ency.ui.currencies

import android.content.Context
import com.valance.ency.R
import com.valance.ency.ui.custom.actionbar.ActionBar
import com.valance.ency.util.Resource

class AddCurrenciesFragment(context: Context) : BaseCurrenciesFragment(context) {

    override val actionBar: ActionBar
        get() = ActionBar(
            context,
            title = Resource.string(R.string.AddCurrencies),
            onBack = {
                finish()
            }
        )

    override val requiredSelection: Int
        get() = 1
}