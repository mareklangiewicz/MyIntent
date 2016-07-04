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
 * Android implementation of basic IXiews
 * TODO NOW: test it all!
 */
open class AXiew<V: View>(val view: V) : IXiew {
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

    override val clicks = Relay<AXiew<V>>()
        get() {
            if(!view.hasOnClickListeners())
                view.setOnClickListener { field.push(this) }
            return field
        }
}

abstract class ADiew<V: View, T>(view: V) : AXiew<V>(view), IDiew<T>

open class ATiew<V: TextView>(tview: V) : ADiew<V, String>(tview), ITiew {
    override var data by view
}

open class AEditTiew(etview: EditText) : ATiew<EditText>(etview), IEditTiew {

    override val changes = Relay<String>()

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        override fun afterTextChanged(s: Editable?) { s?.toString()?.let { changes.push(it) } }
    }

    init { view.addTextChangedListener(watcher) }
}

open class AButtonTiew(button: Button) : ATiew<Button>(button), IButtonTiew

open class ASimpleCheckBoxDiew(cbox: CheckBox) : ADiew<CheckBox, Boolean>(cbox), ICheckBoxDiew {

    var label by view

    override var data: Boolean
        get() = view.isChecked
        set(value) {view.isChecked = value}
}


open class ATextToTextDiew(view: ViewGroup, tvfirst: TextView, tvsecond: TextView) : AXiew<ViewGroup>(view), ITextToTextDiew {

    private val atlabel = ATiew(tvfirst)
    private val atcontent = ATiew(tvsecond)

    var label by tvfirst
    var content by tvsecond

    override var data: Pair<String, String>
        get() = atlabel.data to atcontent.data
        set(value) {
            atlabel.data = value.first
            atcontent.data = value.second
        }
}


open class ATextToBoolCheckBoxDiew(cbox: CheckBox) : ADiew<CheckBox, Pair<String, Boolean>>(cbox), ITextToBoolDiew {

    var label by view

    var checked: Boolean
        get() = view.isChecked
        set(value) {view.isChecked = value}

    override var data: Pair<String, Boolean>
        get() = label to checked
        set(value) {
            label = value.first
            checked = value.second
        }
}






open class AUrlImageXiew(iview: ImageView) : AXiew<ImageView>(iview), IUrlImageXiew {
    override var url = ""
        set(value) { //TODO LATER: handle invalid urls
            field = value
            if (value.isNotBlank()) view.loadUrl(value) else view.setImageDrawable(null)
        }
}

open class AProgressDiew(bar: ProgressBar) : ADiew<ProgressBar, Int>(bar), IProgressDiew {
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

