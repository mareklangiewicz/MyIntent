package pl.mareklangiewicz.myfragments

import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.*
import kotlinx.android.synthetic.main.mf_my_log_fragment.*
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter
import pl.mareklangiewicz.myutils.MyLogLevel
import pl.mareklangiewicz.myviews.IMyNavigation

/**
 * MyFragment showing MyAndroidLogger messages.
 */
class MyLogFragment : MyFragment() {

    private val adapter = MyAndroLogAdapter(log.history)

    internal var sub: Function1<Unit, Unit>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_log_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        my_log_recycler_view.adapter = adapter

        sub = log.history.changes.invoke { adapter.notifyDataSetChanged() }
        adapter.notifyDataSetChanged() // to make sure we are up to date

        //TODO SOMEDAY: some nice simple header with fragment title
        inflateMenu(R.menu.mf_my_log)

        updateCheckedItem()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            exitTransition = Slide(Gravity.START)
        }
    }

    override fun onDestroyView() {
        sub?.invoke(Unit)
        sub = null
        super.onDestroyView()
    }

    override fun onItemSelected(nav: IMyNavigation, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_level_error ->   log.history.level = MyLogLevel.ERROR
            R.id.log_level_warning -> log.history.level = MyLogLevel.WARN
            R.id.log_level_info ->    log.history.level = MyLogLevel.INFO
            R.id.log_level_debug ->   log.history.level = MyLogLevel.DEBUG
            R.id.log_level_verbose -> log.history.level = MyLogLevel.VERBOSE
            R.id.clear_log_history -> log.history.clear()
            R.id.log_some_assert ->   log.a("some assert")
            R.id.log_some_error ->    log.e("some error")
            R.id.log_some_warning ->  log.w("some warning")
            R.id.log_some_info ->     log.i("some info")
            R.id.log_some_debug ->    log.d("some debug")
            R.id.log_some_verbose ->  log.v("some verbose")
            else -> return super.onItemSelected(nav, item)
        }
        return true
    }

    private fun updateCheckedItem() {
        when (log.history.level) {
            MyLogLevel.ERROR, MyLogLevel.ASSERT -> setCheckedItem(R.id.log_level_error)
            MyLogLevel.WARN -> setCheckedItem(R.id.log_level_warning)
            MyLogLevel.INFO -> setCheckedItem(R.id.log_level_info)
            MyLogLevel.DEBUG -> setCheckedItem(R.id.log_level_debug)
            MyLogLevel.VERBOSE -> setCheckedItem(R.id.log_level_verbose)
        }
    }

}
