package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mi_cmd_list_fragment.*
import pl.mareklangiewicz.myfragments.MyFragment

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
open class MICmdListFragment : MyFragment() {

    private val mAdapter = CmdAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) // just for logging
        return inflater.inflate(R.layout.mi_cmd_list_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mi_cmd_recycler_view.setHasFixedSize(true)
        mi_cmd_recycler_view.layoutManager = LinearLayoutManager(activity)
        mi_cmd_recycler_view.adapter = mAdapter
    }

    fun setImageRes(@DrawableRes res: Int) {
        mAdapter.imageRes = res
    }

    var commands: List<String>?
        get() {
            return mAdapter.commands
        }
        set(commands) {
            mAdapter.commands = commands
        }
}

