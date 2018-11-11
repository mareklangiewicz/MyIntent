package pl.mareklangiewicz.myfragments


import android.animation.ObjectAnimator
import android.animation.ObjectAnimator.ofArgb
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import androidx.annotation.ColorInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import kotlinx.android.synthetic.main.mf_my_pie_tests_fragment.*
import kotlinx.android.synthetic.main.mf_my_pie_tests_header.view.*
import pl.mareklangiewicz.myutils.*
import pl.mareklangiewicz.myviews.MyPie
import java.lang.String.format

//TODO SOMEDAY: create UI to change animation speed (in local menu header)

open class MyPieTestsFragment : MyFragment(), View.OnClickListener {

    private val tocancel = Lst<(Cancel) -> Unit>()

    private var randomize = "to"

    private lateinit var hanimator: ObjectAnimator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_pie_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manager?.lnav?.headerId = R.layout.mf_my_pie_tests_header
        manager?.lnav?.menuId = R.menu.mf_my_pie_tests

        mf_mptf_pie1.setOnClickListener(this)
        mf_mptf_pie2.setOnClickListener(this)
        mf_mptf_pie3.setOnClickListener(this)
        mf_mptf_pie4.setOnClickListener(this)

        manager?.lnav?.headerObj?.let {
            val min = it.mf_mpth_pie.minimum
            val max = it.mf_mpth_pie.maximum
            val pvh1 = PropertyValuesHolder.ofFloat("to", min, max)
            val pvh2 = PropertyValuesHolder.ofFloat("from", min, max - (max - min) / 2)
            hanimator = ObjectAnimator.ofPropertyValuesHolder(it.mf_mpth_pie, pvh1, pvh2)
            hanimator.interpolator = AccelerateInterpolator()
        }

        val ctl = manager!!.lnav!!.items {
            randomize = manager!!.lnav!!.menuObj!!.findItem(it)?.toString() ?: randomize
        }
        tocancel.add(ctl)

        if (savedInstanceState == null)
            manager?.lnav?.setCheckedItem(R.id.mpt_randomize_to, true)
    }

    override fun onResume() {
        super.onResume()
        randomize = manager?.lnav?.firstCheckedItem?.run { title.toString() } ?: "to".apply { log.e("No item selected") }
    }

    override fun onDestroyView() {
        tocancel.forEach { it(Cancel) }
        tocancel.clr()
        manager?.lnav?.headerId = -1
        manager?.lnav?.menuId = -1
        super.onDestroyView()
    }

    override fun onClick(v: View) {

        if ( v !is MyPie) return

        if (randomize == "pieColor" || randomize == "ovalColor") {
            @ColorInt val value = RANDOM.nextColor(Color.rgb(0, 0, 0), Color.rgb(255, 255, 255))
            ofArgb(v, randomize, value).start()
            log.i(format("[SNACK]MyPie:%s: random color is ... %X %X %X", randomize, red(value), green(value), blue(value)))
        } else {
            var min = 0f
            var max = 100f
            when (randomize) {
                "from" -> {
                    min = v.minimum
                    max = v.to
                }
                "to" -> {
                    min = v.from
                    max = v.maximum
                }
                "minimum" -> max = v.from
                "maximum" -> min = v.to
            }

            val value = RANDOM.nextFloat(min, max)
            ObjectAnimator.ofFloat(v, randomize, value).start()
            log.i(String.format("[SNACK]MyPie:%s: random %.2f..%.2f is ... %.2f", randomize, min, max, value))
        }
    }


    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if (drawerView !== manager?.lnav) return
        hanimator.setCurrentFraction(slideOffset)
    }
    override fun onDrawerOpened(drawerView: View) { }
    override fun onDrawerClosed(drawerView: View) { }
    override fun onDrawerStateChanged(newState: Int) { }
}
