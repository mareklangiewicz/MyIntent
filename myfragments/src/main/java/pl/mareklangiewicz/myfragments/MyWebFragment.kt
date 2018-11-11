package pl.mareklangiewicz.myfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import kotlinx.android.synthetic.main.mf_my_web_fragment.*

/**
 * Created by Marek Langiewicz on 13.10.15.
 */
open class MyWebFragment : MyFragment() {

    var url = ""
        set(value) {
            if (value == field)
                return
            field = value
            if(isViewAvailable)
                my_web_view.loadUrl(value)
        }

    // use it after onViewCreated (when isViewAvailable == true)
    val settings: WebSettings get() = my_web_view.settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { url = it.getString(ARG_URL).orEmpty() }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_web_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        my_web_view.loadUrl(url)
    }

    companion object {
        val ARG_URL = "url"
    }

}
