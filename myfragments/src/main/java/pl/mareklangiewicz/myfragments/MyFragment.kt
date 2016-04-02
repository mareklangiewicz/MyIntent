package pl.mareklangiewicz.myfragments


import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.str
import pl.mareklangiewicz.myviews.IMyUIManager


/**
 * This is my base class for common fragments.
 * I make some decisions here so I don't have to take care of those details
 * in every fragment class. If you need to make other choices - don't use it.
 * WARNING: I invoke: setRetainInstance(true) here in onCreate.
 * So remember to deal with this type of fragment lifecycle correctly, especially:
 * - don't keep references to old views throghout whole life time of fragment
 * - don't add this fragment transactions to back stack
 * or invoke setRetainInstance(false) after MyFragment.onCreate.
 */
open class MyFragment : Fragment(), DrawerLayout.DrawerListener {


    private val V = true
    private val VV = false

//    private val V = BuildConfig.VERBOSE
//    private val VV = BuildConfig.VERY_VERBOSE
//
//    FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
//    Now it is always 'release' in libraries.. see:
//    https://code.google.com/p/android/issues/detail?id=52962
//    http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
//    http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication



    protected val log = MY_DEFAULT_ANDRO_LOGGER


    // it will be changed to true at 'onViewCreated' callback and set back to false at 'onDestroyView'
    // kotlin extensions are working correctly when it is true.
    protected var isViewAvailable = false

    protected val manager: IMyUIManager? get() = activity as? IMyUIManager

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }
    override fun onDrawerOpened(drawerView: View) { }
    override fun onDrawerClosed(drawerView: View) { }
    override fun onDrawerStateChanged(newState: Int) { }

    @CallSuper override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        if (VV) log.v("${javaClass.simpleName}.onInflate context=${context.str} attrs=${attrs.str}  state=${savedInstanceState.str}")
        super.onInflate(context, attrs, savedInstanceState)
    }

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
        if (V) log.v("${javaClass.simpleName}.onCreate state=${savedInstanceState.str} args=${arguments.str}")
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @CallSuper override fun onAttach(ctx: Context) {
        if (VV) log.v("${javaClass.simpleName}.onAttach context=${ctx.str}")
        super.onAttach(ctx)
    }

    @CallSuper override fun onActivityCreated(savedInstanceState: Bundle?) {
        if (VV) log.v("${javaClass.simpleName}.onActivityCreated state=${savedInstanceState.str}")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (VV) log.v("${javaClass.simpleName}.onCreateView inflater=${inflater.str} container=${container.str} state=${savedInstanceState.str}")
        return null
    }

    @CallSuper override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isViewAvailable = true
        if (VV) log.v("${javaClass.simpleName}.onViewCreated view=${view.str} state=${savedInstanceState.str}")
        super.onViewCreated(view, savedInstanceState)
    }

    @CallSuper override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (VV) log.v("${javaClass.simpleName}.onViewStateRestored state=${savedInstanceState.str}")
        super.onViewStateRestored(savedInstanceState)
    }

    @CallSuper override fun onStart() {
        if (V) log.v("${javaClass.simpleName}.onStart")
        super.onStart()
    }

    @CallSuper override fun onResume() {
        if (V) log.v("${javaClass.simpleName}.onResume")
        super.onResume()
    }

    @CallSuper override fun onPause() {
        if (V) log.v("${javaClass.simpleName}.onPause")
        super.onPause()
    }

    @CallSuper override fun onSaveInstanceState(outState: Bundle) {
        if (VV) log.v("${javaClass.simpleName}.onSaveInstateState outState=${outState.str}")
        super.onSaveInstanceState(outState)
    }

    @CallSuper override fun onStop() {
        if (V) log.v("${javaClass.simpleName}.onStop")
        super.onStop()
    }

    @CallSuper override fun onDestroyView() {
        if (VV) log.v("${javaClass.simpleName}.onDestroyView")
        super.onDestroyView()
        isViewAvailable = false
    }

    @CallSuper override fun onDestroy() {
        if (V) log.v("${javaClass.simpleName}.onDestroy")
        super.onDestroy()
    }

    @CallSuper override fun onDetach() {
        if (VV) log.v("${javaClass.simpleName}.onDetach")
        super.onDetach()
    }
}
