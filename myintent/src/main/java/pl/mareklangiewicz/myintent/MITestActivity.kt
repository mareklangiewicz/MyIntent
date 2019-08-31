package pl.mareklangiewicz.myintent

import android.os.Bundle
import pl.mareklangiewicz.myactivities.MyTestActivity
import pl.mareklangiewicz.myutils.MyCommand

/**
 * Created by Marek Langiewicz on 05.05.16.
 * NOTE: in canary build type: menu option: start example activity will fail. this is correct.
 * it is because there is no "canary" build type in myactivities module.
 */
class MITestActivity : MyTestActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // We add three custom commands to test if leak canary tool is working correctly:

        val cmd1 = "cmd:start custom action leak data activity"
        val item1 = gnav!!.menuObj!!.add(0, 121, 0, cmd1)
        item1.titleCondensed = cmd1

        val cmd2 = "cmd:start custom action leak data fragment"
        val item2 = gnav!!.menuObj!!.add(0, 122, 0, cmd2)
        item2.titleCondensed = cmd2

        val cmd3 = "cmd:start custom action leak data nothing"
        val item3 = gnav!!.menuObj!!.add(0, 123, 0, cmd3)
        item3.titleCondensed = cmd3
    }

    override fun onCommandCustom(command: MyCommand) {
        when(command["action"]) {
            "leak" -> when(command["data"]) {
                "activity" -> Leak.obj = this
                "fragment" -> Leak.obj = fgmt
                "nothing" -> Leak.obj = null
            }
            else -> super.onCommandCustom(command)
        }
    }

    object Leak {
        var obj: Any? = null
    }
}