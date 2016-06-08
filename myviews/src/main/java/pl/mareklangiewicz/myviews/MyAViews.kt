package pl.mareklangiewicz.myviews

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import pl.mareklangiewicz.myutils.Relay
import pl.mareklangiewicz.myutils.getValue
import pl.mareklangiewicz.myutils.loadUrl
import pl.mareklangiewicz.myutils.setValue

/**
 * Android implementation of basic IViews
 * TODO NOW: test it all!
 */
open class AView<V: View>(val view: V) : IView {
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

    override val clicks = Relay<AView<V>>()
        get() {
            if(!view.hasOnClickListeners())
                view.setOnClickListener { field.push(this) }
            return field
        }
}

open class ATextView<V: TextView>(tview: V) : AView<V>(tview), ITextView {
    override var data by view
}

open class AEditTextView(etview: EditText) : ATextView<EditText>(etview), IEditTextView {

    override val changes = Relay<String>()

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        override fun afterTextChanged(s: Editable?) { s?.toString()?.let { changes.push(it) } }
    }

    init { view.addTextChangedListener(watcher) }
}

open class AButtonView(button: Button) : ATextView<Button>(button), IButtonView

// here we ignore that CheckBox in Android extends TextView.
open class ACheckBoxView(cbox: CheckBox) : AView<CheckBox>(cbox), ICheckBoxView {
    override var data by view
}

// and here we expose the text as a "label" property (the "data" property is still a Boolean)
open class ALabCheckBoxView(cbox: CheckBox) : ACheckBoxView(cbox), ILabCheckBoxView {
    override val label = ATextView(cbox)
}


open class ALabTextView(view: ViewGroup, tvlabel: TextView, tvtext: TextView) : AView<ViewGroup>(view), ILabTextView {
    override val label = ATextView(tvlabel)
    val text = ATextView(tvtext) // text.data will be the same as data
    override var data by tvtext
}







open class AUrlImageView(iview: ImageView) : AView<ImageView>(iview), IUrlImageView {
    override var url = ""
        set(value) { //TODO LATER: handle invalid urls
            field = value
            if (value.isNotBlank()) view.loadUrl(value) else view.setImageDrawable(null)
        }
}

open class AProgressView(bar: ProgressBar) : AView<ProgressBar>(bar), IProgressView {
    override var data          = 0    ; set(value) { field = value; view.progress        = value + minimum }
    override var maximum       = 10000; set(value) { field = value; view.max             = value - minimum }
    override var minimum       = 0    ; set(value) { field = value; view.max             = maximum - value }
    override var indeterminate = false; set(value) { field = value; view.isIndeterminate = value           }
    init {
        maximum = maximum // just to invoke setter to sync underlying ProgressBar
        data = data // just to invoke setter to sync underlying ProgressBar
        indeterminate = indeterminate // just to invoke setter to sync underlying ProgressBar
    }
}

