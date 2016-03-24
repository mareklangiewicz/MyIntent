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


fun Cursor.getString(columnName: String): String = getString(getColumnIndexOrThrow(columnName))
fun Cursor.getInt(columnName: String): Int = getInt(getColumnIndexOrThrow(columnName))

fun Cursor.getStringOrNull(columnName: String): String? {
    val index = getColumnIndexOrThrow(columnName)
    return if (isNull(index)) null else getString(index)
}
fun Cursor.getIntOrNull(columnName: String): Int? {
    val index = getColumnIndexOrThrow(columnName)
    return if (isNull(index)) null else getInt(index)
}

// TODO SOMEDAY: similar Cursor extensions for other data types


fun ContentResolver.clear(uri: Uri): Int = delete(uri, null, null)

inline fun ContentResolver.insert(uri: Uri, cvbuilder: ContentValues.() -> Unit): Uri = insert(uri, ContentValues().apply(cvbuilder))

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
