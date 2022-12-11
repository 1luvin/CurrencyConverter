package com.valance.ency.ui.base

import android.content.Context
import android.view.View
import com.valance.ency.ui.main.MainActivity

abstract class BaseFragment(protected val context: Context) {

    private var view: View? = null

    fun view(): View {
        if (view == null) {
            view = createView()
        }
        return view!!
    }

    abstract fun createView(): View
    fun destroyView() { view = null }

    protected fun pushFragment(fragment: BaseFragment) {
        MainActivity.instance.pushFragment(fragment)
    }
    open fun finish() {
        MainActivity.instance.popFragment()
    }

    open fun onResume() {}
    open fun onFullyResumed() {}

    open fun onFinish() {}
    open fun onFullyFinished() {}
}