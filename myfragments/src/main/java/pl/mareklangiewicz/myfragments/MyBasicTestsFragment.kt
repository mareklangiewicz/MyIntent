package pl.mareklangiewicz.myfragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.mf_my_basic_tests_fragment.*


class MyBasicTestsFragment : MyFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        inflateHeader(R.layout.mf_my_basic_header)
        return inflater.inflate(R.layout.mf_my_basic_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mf_mbtf_et_name.setOnEditorActionListener(TextView.OnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (textView.text.toString().equals("Marek", ignoreCase = true)) {
                    textView.error = "You are not Marek! I am Marek!!"
                    log.e("[SNACK]You are not Marek! I am Marek!!")
                } else {
                    log.i(String.format("[SNACK]Hello %s!", textView.text))
                }
                return@OnEditorActionListener false //false so keyboard is hidden anyway
            }
            false
        })
    }

    override fun onDrawerSlide(view: View, slideOffset: Float) {
        if (view !== localNavigation) return
        mf_mbtf_pie1.rotation = slideOffset * 360
        mf_mbtf_pie3.to = 75 - slideOffset * 50
    }
}
