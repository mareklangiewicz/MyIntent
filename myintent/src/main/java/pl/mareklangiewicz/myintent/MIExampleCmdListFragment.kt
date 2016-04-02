package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.view.View

import java.util.ArrayList

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
class MIExampleCmdListFragment : MICmdListFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager?.name = "MI${if(BuildConfig.DEBUG) " D " else " "}${getString(R.string.mi_example_commands)}"
        imageRes = R.drawable.mi_ic_example_command_black_24dp
        refresh()
    }

    fun refresh() {
        var cmds = ArrayList<String>()
        MIContract.CmdExample.load(activity, cmds)
        commands = cmds
    }

}
