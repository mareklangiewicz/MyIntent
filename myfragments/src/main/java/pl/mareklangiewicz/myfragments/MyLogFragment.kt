package pl.mareklangiewicz.myfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mf_my_log_fragment.*
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter
import pl.mareklangiewicz.myutils.*
import pl.mareklangiewicz.upue.*

/**
 * MyFragment showing MyAndroidLogger messages.
 */
@Suppress("unused")
open class MyLogFragment : MyFragment() {

    private val adapter = MyAndroLogAdapter(log.history)

    private val tocancel = Lst<Pushee<Cancel>>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_log_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        my_log_recycler_view.adapter = adapter

        val ctl1 = log.history.changes { adapter.notifyDataSetChanged() }
        tocancel.add(ctl1)

        adapter.notifyDataSetChanged() // to make sure we are up to date

        //TODO SOMEDAY: some nice simple header with fragment title
        manager?.lnav?.menuId = R.menu.mf_my_log

        val ctl2 = manager!!.lnav!!.items {
            when (it) {
                R.id.log_level_error ->   log.history.level = MyLogLevel.ERROR
                R.id.log_level_warning -> log.history.level = MyLogLevel.WARN
                R.id.log_level_info ->    log.history.level = MyLogLevel.INFO
                R.id.log_level_debug ->   log.history.level = MyLogLevel.DEBUG
                R.id.log_level_verbose -> log.history.level = MyLogLevel.VERBOSE
                R.id.clear_log_history -> log.history.clr()
                R.id.log_some_assert ->   log.a("some assert")
                R.id.log_some_error ->    log.e("some error")
                R.id.log_some_warning ->  log.w("some warning")
                R.id.log_some_info ->     log.i("some info")
                R.id.log_some_debug ->    log.d("some debug")
                R.id.log_some_verbose ->  log.v("some verbose")
            }
        }
        tocancel.add(ctl2)

        updateCheckedItem()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            exitTransition = Slide(Gravity.START)
//        }
    }

    override fun onDestroyView() {
        tocancel.forEach { it(Cancel) }
        tocancel.clr()
        my_log_recycler_view.adapter = null
        manager?.lnav?.menuId = -1
        super.onDestroyView()
    }

    private fun updateCheckedItem() {
        val id = when (log.history.level) {
            MyLogLevel.ERROR, MyLogLevel.ASSERT -> R.id.log_level_error
            MyLogLevel.WARN -> R.id.log_level_warning
            MyLogLevel.INFO -> R.id.log_level_info
            MyLogLevel.DEBUG -> R.id.log_level_debug
            MyLogLevel.VERBOSE -> R.id.log_level_verbose
        }
        manager?.lnav?.setCheckedItem(id, false)
    }

}
