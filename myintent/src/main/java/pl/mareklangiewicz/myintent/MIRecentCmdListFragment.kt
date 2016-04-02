package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.view.View

import java.util.ArrayList

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
class MIRecentCmdListFragment : MICmdListFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager?.name = "MI${if(BuildConfig.DEBUG) " D " else " "}${getString(R.string.mi_recent_commands)}"
        imageRes = R.drawable.mi_ic_recent_command_black_24dp
        refresh()
    }

    fun refresh() {
        var cmds = ArrayList<String>()
        MIContract.CmdRecent.load(activity, cmds, 128)
        commands = cmds
    }
}
