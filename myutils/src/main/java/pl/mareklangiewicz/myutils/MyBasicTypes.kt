package pl.mareklangiewicz.myutils

import pl.mareklangiewicz.upue.Cancel
import pl.mareklangiewicz.upue.Pusher


interface IEnabled {
    @Suppress("UNUSED_PARAMETER")
    var enabled: Boolean
        get() = true
        set(value) = throw UnsupportedOperationException()
}

interface IVisible {
    @Suppress("UNUSED_PARAMETER")
    var visible: Boolean
        get() = true
        set(value) = throw UnsupportedOperationException()
}


/**
 * Implementing this interface means to be able to somehow "present" given data.
 * Can be read only, but many times it will be write only (only setter will be implemented)
 * For example some IDataView can be able to display some summary of given (big) data structure,
 * but the view don't want to remember the whole data structure, so it does not support reading back.
 */
interface IData<T> {
    @Suppress("UNUSED_PARAMETER")
    var data: T
        get() = throw UnsupportedOperationException()
        set(value) = throw UnsupportedOperationException()
}

interface IChanges<out T> {
    val changes: Pusher<T, Cancel>
        get() = throw UnsupportedOperationException()
}




val Boolean.yesno: String get() = if(this) "YES" else "NO"
