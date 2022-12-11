package com.valance.ency.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.*
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.valance.ency.data.UserConfig
import com.valance.ency.ui.base.BaseFragment
import com.valance.ency.ui.converter.ConverterFragment
import com.valance.ency.ui.currencies.SelectCurrenciesFragment
import com.valance.ency.util.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: MainActivity? = null
        val instance: MainActivity get() = Instance!!
    }

    val requestQueue: RequestQueue by lazy { Volley.newRequestQueue(this) }
    val lifecycleScope: CoroutineScope by lazy { MainScope() }

    private lateinit var fragmentFrame: FrameLayout
    private lateinit var fragmentStack: ArrayList<BaseFragment>

    /*
        Activity
     */

    init {
        Instance = this
    }

    override fun attachBaseContext(newBase: Context) {
        val configuration = Configuration().apply {
            setLocale(Locale.getDefault())
        }
        val context = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createView())

        initializeNavigation()
    }

    override fun onBackPressed() {
        val lastFragment = fragmentStack.last()
        if (fragmentStack.size > 1 && lastFragment !is SelectCurrenciesFragment) {
            lastFragment.finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun finish() {
        lifecycleScope.cancel()
        finishAffinity()
    }

    /*
        View
     */

    private fun createView(): View {
        fragmentFrame = FrameLayout(this).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
        }
        return fragmentFrame
    }

    /*
        Navigation
     */

    private fun initializeNavigation() {
        fragmentStack = ArrayList<BaseFragment>().apply {
            add(
                ConverterFragment(this@MainActivity).apply {
                    createView()
                }
            )
        }

        if (UserConfig.currencies.isEmpty()) {
            fragmentStack.add(
                SelectCurrenciesFragment(this)
            )
        }

        val lastFragment = fragmentStack.last()
        fragmentFrame.addView(lastFragment.view())
        lastFragment.apply {
            onResume()
            onFullyResumed()
        }
    }

    fun pushFragment(fragment: BaseFragment) {
        val fromFragment = fragmentStack.last()
        fragmentStack.add(fragment)
        val toFragment = fragmentStack.last()
        switchFragments(fromFragment, toFragment, forward = true)
    }

    fun popFragment() {
        if (fragmentStack.size == 1) return

        val fromFragment = fragmentStack.last()
        fragmentStack.removeLast()
        val toFragment = fragmentStack.last()
        switchFragments(fromFragment, toFragment, forward = false)
    }

    private fun switchFragments(
        fromFragment: BaseFragment,
        toFragment: BaseFragment,
        forward: Boolean
    ) {
        val fromView = fromFragment.view()
        val toView = toFragment.view()
        val f =
            if (fromFragment is SelectCurrenciesFragment || toFragment is SelectCurrenciesFragment) {
                !forward
            } else {
                forward
            }
        val diff = fragmentFrame.width / 7f * if (f) 1 else -1
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 150
            interpolator = DecelerateInterpolator()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    toView.apply {
                        alpha = 0f
                        translationX = diff
                    }
                    fragmentFrame.addView(toView)

                    fromFragment.onFinish()
                    toFragment.onResume()
                }

                override fun onAnimationEnd(animation: Animator?) {
                    fragmentFrame.removeView(fromView)

                    fromFragment.onFullyFinished()
                    toFragment.onFullyResumed()
                }
            })

            addUpdateListener {
                val v = it.animatedValue as Float

                fromView.apply {
                    alpha = 1 - v
                    translationX = -diff * v
                }

                toView.apply {
                    alpha = v
                    translationX = diff * (1 - v)
                }
            }

            start()
        }
    }

    /*
        Action
     */

    fun vibrate() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("Deprecation")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }.also {
            it.vibrate(VibrationEffect.createOneShot(40, 40))
        }
    }
}