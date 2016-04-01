package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.view.View
import pl.mareklangiewicz.myfragments.MyWebFragment

/**
 * Created by Marek Langiewicz on 13.10.15.
 */
class MIHelpFragment : MyWebFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        manager?.name = getString(R.string.mi_help)

        if(url == "") url = "file:///android_asset/mi.html"

        settings.userAgentString = settings.userAgentString + " " + getString(R.string.mi_user_agent_suffix)
        settings.javaScriptEnabled = true

        super.onViewCreated(view, savedInstanceState)
    }
}
