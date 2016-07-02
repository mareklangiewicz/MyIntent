package pl.mareklangiewicz.myactivities

import android.animation.ObjectAnimator
import android.animation.ObjectAnimator.ofInt
import android.animation.ObjectAnimator.ofPropertyValuesHolder
import android.animation.PropertyValuesHolder
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.ma_my_test_global_header.view.*
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable
import pl.mareklangiewicz.myutils.*

/**
 * Test activity presenting most of My..... classes functionality
 */
open class MyTestActivity : MyActivity() {

    private val mldrawable = MyMagicLinesDrawable().apply {
        color = 0x30ffffff
        strokeWidth = 8f
    }

    private val mlanimator = ofInt(mldrawable, "level", 0, 10000).apply {
        duration = 1000
        interpolator = LinearInterpolator()
    }

    private lateinit var hpanimator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log.i("Hello world!")
        log.d("some boring debug message...")
        log.w("Warning!... just kidding...")

        gnav!!.menuId = R.menu.ma_my_test_global
        gnav!!.headerId = R.layout.ma_my_test_global_header

        gnav!!.headerObj!!.ma_mta_gh_v_underline.background = mldrawable

        hpanimator = ofPropertyValuesHolder(
                gnav!!.headerObj!!.ma_mta_gh_tv_home_page,
                PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0f, 1f),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -50f, -50f, 0f)
        ).apply { interpolator = LinearInterpolator() }

        gnav!!.items { // we ignore returned subscription - navigation will live as long as activity
            when(it) {
                R.id.action_whats_up -> log.i("[SNACK][SHORT]What's up mate?")
                R.id.action_settings -> log.w("[SNACK]TODO: some settings (or not)..")
                R.id.action_destroy_something -> log.a("[SNACK]BUM!")
            }
        }

        if (savedInstanceState == null)
            gnav!!.setCheckedItem(R.id.section_my_pie_tests, true)
    }

    @TargetApi(22)
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        super.onDrawerSlide(drawerView, slideOffset)
        if (drawerView === gnav && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            hpanimator.setCurrentFraction(slideOffset)
    }


    override fun onDrawerOpened(drawerView: View) {
        super.onDrawerOpened(drawerView)
        if (drawerView === gnav) mlanimator?.run { if (!isStarted) start() }
    }

    override fun onDrawerClosed(drawerView: View) {
        super.onDrawerClosed(drawerView)
        if (drawerView === gnav) {
            mlanimator?.cancel()
            mldrawable.level = 0
        }
    }

}
