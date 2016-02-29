import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.design.widget.Snackbar

/**
 * Created by Marek Langiewicz on 29.01.16.
 * Some utilities
 */

operator fun TextView.setValue(obj: Any?, property: Any?, arg: CharSequence) { text = arg }

operator fun TextView.getValue(obj: Any?, property: Any?): String = text.toString()

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(it) }
}

operator fun ViewGroup.get(pos: Int): View = getChildAt(pos)

val ViewGroup.views: List<View>
    get() = (0 until childCount).map { this[it] }


