package pl.mareklangiewicz.myviews

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 22.03.16.
 */
class MyScrollBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    val log = MY_DEFAULT_ANDRO_LOGGER

    private var min = 0
    private var max = 100
    private var scroll_min = -200
    private var scroll_max = 200
    private var property: String? = null
    private var animator: ObjectAnimator? = null

    constructor(prop: String?) {
        property = prop
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.mv_MyScrollBehavior)
        min = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_min, min)
        max = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_max, max)
        scroll_min = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_scroll_min, scroll_min)
        scroll_max = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_scroll_max, scroll_max)
        property = a.getString(R.styleable.mv_MyScrollBehavior_mv_behavior_property)
        a.recycle()
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout?,
                                     child: V?, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        if (property == null) {
            log.d("No property to scroll provided.")
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            log.d("This behavior requies at least lollipop mr1 device.")
            return false
        }

        if(nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL == 0) return false

        if (animator == null) animator = ObjectAnimator.ofInt(child, property, min, max)

        return true
    }

    @SuppressLint("NewApi")
    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout?,
                                child: V?, target: View?, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        if (animator == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
            return

        val progress = (dyConsumed+dyUnconsumed).toFloat().scale1d(scroll_min.toFloat(), scroll_max.toFloat(), 0f, 1f)
        animator!!.setCurrentFraction(progress)
    }
}
