package pl.mareklangiewicz.myfragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mf_my_example_fragment.*
import pl.mareklangiewicz.myutils.str


open class MyExampleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_example_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val text = StringBuilder()
            for (key in it.keySet()) text.append("$key: ${it.get(key).str}\n")
            my_example_view.text = text.toString()
        }
    }
}
