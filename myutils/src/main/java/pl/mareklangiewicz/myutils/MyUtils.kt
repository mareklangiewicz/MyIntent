package pl.mareklangiewicz.myutils

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.design.widget.Snackbar
import android.database.sqlite.SQLiteDatabase

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

val ViewGroup.views: List<View> get() = (0 until childCount).map { this[it] }


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
