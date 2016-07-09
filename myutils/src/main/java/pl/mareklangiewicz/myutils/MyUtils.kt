package pl.mareklangiewicz.myutils

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Rect
import android.net.Uri
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

/**
 * Created by Marek Langiewicz on 29.01.16.
 * Some utilities
 */


operator fun TextView.setValue(obj: Any?, property: Any?, arg: CharSequence) { text = arg }

operator fun TextView.getValue(obj: Any?, property: Any?): String = text.toString()

@Suppress("UNCHECKED_CAST")
fun <V : View> ViewGroup.inflate(@LayoutRes res: Int, attach: Boolean = false) = from(context).inflate(res, this, attach) as? V

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

val ViewGroup.views: List<View> get() = (0 until childCount).map { this[it] }

fun View?.overlaps(that: View?): Boolean {

    if (this === null || that === null)
        return false

    if (this.visibility != View.VISIBLE || that.visibility != View.VISIBLE)
        return false

    val w1 = this.measuredWidth
    if (w1 < 1)
        return false

    val w2 = that.measuredWidth
    if (w2 < 1)
        return false

    val h1 = this.measuredHeight
    if (h1 < 1)
        return false

    val h2 = that.measuredHeight
    if (h2 < 1)
        return false

    val pos1 = IntArray(2)
    val pos2 = IntArray(2)
    this.getLocationInWindow(pos1)
    that.getLocationInWindow(pos2)

    val r1 = Rect(pos1[0], pos1[1], pos1[0] + w1, pos1[1] + h1)
    val r2 = Rect(pos2[0], pos2[1], pos2[0] + w2, pos2[1] + h2)

    return Rect.intersects(r1, r2)
}

fun Cursor.kgetString(columnName: String): String = getString(getColumnIndexOrThrow(columnName))
fun Cursor.kgetInt(columnName: String): Int = getInt(getColumnIndexOrThrow(columnName))

fun Cursor.kgetStringOrNull(columnName: String): String? {
    val index = getColumnIndexOrThrow(columnName)
    return if (isNull(index)) null else getString(index)
}
fun Cursor.kgetIntOrNull(columnName: String): Int? {
    val index = getColumnIndexOrThrow(columnName)
    return if (isNull(index)) null else getInt(index)
}

// TODO SOMEDAY: similar Cursor extensions for other data types


fun ContentResolver.kclear(uri: Uri): Int = delete(uri, null, null)

inline fun ContentResolver.kinsert(uri: Uri, cvbuilder: ContentValues.() -> Unit): Uri = insert(uri, ContentValues().apply(cvbuilder))

inline fun ContentResolver.kquery(
        uri: Uri,
        projection: Array<out String>? = null,
        selection: String? = null,
        selectionArgs: Array<out String>? = null,
        sortOrder: String? = null,
        usecursor: (Cursor) -> Boolean
): Boolean
        = query(uri, projection, selection, selectionArgs, sortOrder)?.use(usecursor) ?: false


fun SQLiteDatabase.createTable(name: String, vararg columns: String) {
    val sql = StringBuilder("CREATE TABLE ")
    sql.append(name)
    sql.append(" ( ")
    for (i in columns.indices) {
        sql.append(columns[i])
        if (i < columns.size - 1)
            sql.append(" , ")
    }
    sql.append(" ) ")
    execSQL(sql.toString())
}

fun SQLiteDatabase.dropTable(name: String) = execSQL("DROP TABLE IF EXISTS " + name)

fun Activity.getInputMethodService() = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
// TODO LATER: similar methods for other services

fun Activity.hideKeyboard() = currentFocus?.windowToken?.let { getInputMethodService().hideSoftInputFromWindow(it, 0) } ?: false

fun <I> RecyclerView.notify(chg: LstChg<I>) = adapter?.run {
    when(chg) {
        is LstSet -> notifyItemChanged(chg.idx)
        is LstIns -> notifyItemInserted(chg.idx)
        is LstDel -> notifyItemRemoved(chg.idx)
        is LstAdd -> notifyItemInserted(itemCount - 1) // itemCount is already increased
        is LstMov -> notifyItemMoved(chg.src, chg.dst)
        else -> notifyDataSetChanged()
    }
}
