package pl.mareklangiewicz.myviews

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import pl.mareklangiewicz.myutils.*

/**
 * Android implementation of basic IViews
 * TODO NOW: test it all!
 */
open class AView(val view: View) : IView {
    override var visible: Boolean
        get() = view.visibility == View.VISIBLE
        set(value) {
            view.visibility = if(value) View.VISIBLE else View.INVISIBLE
        }

    override var enabled: Boolean
        get() = view.isEnabled
        set(value) {
            view.isEnabled = value
        }

    override val clicks = Relay<AView>()
        get() {
            if(!view.hasOnClickListeners())
                view.setOnClickListener { field.push(this) }
            return field
        }
}

open class ATextView(private val tv: TextView) : AView(tv), ITextView {
    override var data by tv
}

open class AEditTextView(private val ed: EditText) : ATextView(ed), IEditTextView {

    override val changes = Relay<String>()

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        override fun afterTextChanged(s: Editable?) { s?.toString()?.let { changes.push(it) } }
    }

    init { ed.addTextChangedListener(watcher) }
}

open class AButtonView(private val button: Button) : ATextView(button), IButtonView

class AUrlImageView(private val imageView: ImageView) : AView(imageView), IUrlImageView {
    override var url = ""
        set(value) { //TODO LATER: handle invalid urls
            field = value
            if (value.isNotBlank()) imageView.loadUrl(value) else imageView.setImageDrawable(null)
        }
}

open class AProgressView(private val bar: ProgressBar) : AView(bar), IProgressView {
    override var data          = 0    ; set(value) { field = value; bar.progress        = value + minimum }
    override var maximum       = 10000; set(value) { field = value; bar.max             = value - minimum }
    override var minimum       = 0    ; set(value) { field = value; bar.max             = maximum - value }
    override var indeterminate = false; set(value) { field = value; bar.isIndeterminate = value           }
    init {
        maximum = maximum // just to invoke setter to sync underlying ProgressBar
        data = data // just to invoke setter to sync underlying ProgressBar
        indeterminate = indeterminate // just to invoke setter to sync underlying ProgressBar
    }
}


