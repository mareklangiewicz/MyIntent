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


interface IData<T> {
    var data: T
}

interface IChanges<T> {
    val changes: IPusher<T, Cancel>
        get() = throw UnsupportedOperationException()
}


interface IProgress : IData<Int> {

    val minimum: Int
        get() = 0

    val maximum: Int
        get() = 10000

    var indeterminate: Boolean // true means it should display something indicating that it is working and we don't know how far are we
        get() = false
        set(value) = throw UnsupportedOperationException()

}




val Boolean.yesno: String get() = if(this) "YES" else "NO"
