package pl.mareklangiewicz.myfragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.mf_my_stupid_tests_fragment.*
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 10.09.15.
 * A Place to test some stupid ideas fast.
 * A kind of scratchpad.
 */


open class MyStupidTestsFragment : MyFragment(), DrawerLayout.DrawerListener {

    private val tocancel = Lst<(Cancel) -> Unit>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_stupid_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        my_stupid_log_simple_view.arr = log.history
        val ctl = log.history.changes { my_stupid_log_simple_view.invalidate() }
        tocancel.add(ctl)

        // I use here NavigationView directly (without my IMyUINavigation abstraction etc.) on purpose - just to test some stuff
        stupid_navigation_view.inflateMenu(R.menu.mf_my_stupid_tests)
        stupid_navigation_view.setNavigationItemSelectedListener {
            log.i(String.format("[SNACK]%s", it.title))
            true
        }

        if (savedInstanceState == null)
            stupid_navigation_view.setCheckedItem(R.id.stupid_item_2)

    }

    override fun onResume() {
        super.onResume()
        val animator = ObjectAnimator.ofFloat(stupid_warning, View.TRANSLATION_Y, 8f)
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = 5
        animator.interpolator = LinearInterpolator()
        animator.start()

    }

    override fun onDestroyView() {
        tocancel.forEach { it(Cancel) }
        tocancel.clr()
        super.onDestroyView()
    }
}
