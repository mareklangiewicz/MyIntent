package pl.mareklangiewicz.myutils


interface IEnabled {
    var enabled: Boolean
        get() = true
        set(value) = throw UnsupportedOperationException()
}

interface IVisible {
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
    var data: T
        get() = throw UnsupportedOperationException()
        set(value) = throw UnsupportedOperationException()
}

interface IChanges<out T> {
    val changes: IPusher<T, Cancel>
        get() = throw UnsupportedOperationException()
}




val Boolean.yesno: String get() = if(this) "YES" else "NO"
