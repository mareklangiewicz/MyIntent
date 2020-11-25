package pl.mareklangiewicz.myviews

import pl.mareklangiewicz.pue.Cancel
import pl.mareklangiewicz.myutils.IChanges
import pl.mareklangiewicz.myutils.IData
import pl.mareklangiewicz.myutils.IEnabled
import pl.mareklangiewicz.myutils.ILst
import pl.mareklangiewicz.pue.IPusher
import pl.mareklangiewicz.myutils.IVisible

/**
 * Created by Marek Langiewicz on 27.05.16.
 *
 * Interfaces for common views. It is supposed to be implemented on different platforms
 * (at least android and javascript), so we will have a lot of similar SomethingView classes.
 *
 * I'll try some crazy renaming to distinquish these view abstractions (and their implementations)
 * from particular raw platform classes: Rename most popular base views: View, DataView and TextView:
 * View -> Xiew; DataView -> Diew; TextView -> Tiew
 * This is against any reasonable coding convention, but.. I'll give it a try anyway. :-)
 * TODO LATER: Move this IXiews and implementations (AXiews; JSXiews?; IDEXiews?) to separate library)
 */


interface IClicks<out T> {
    val clicks: IPusher<T, Cancel>
        get() = throw UnsupportedOperationException()
}

/** Some "moving" data - like progress bar etc. */
interface IMovData : IData<Int> {

    @Suppress("UNUSED_PARAMETER")
    var min: Int
        get() = 0
        set(value) = throw UnsupportedOperationException()

    @Suppress("UNUSED_PARAMETER")
    var max: Int
        get() = 10000
        set(value) = throw UnsupportedOperationException()

    @Suppress("UNUSED_PARAMETER")
    var fuzzy: Boolean // true means it should display something indicating that it is working and we don't know how far are we
        get() = false
        set(value) = throw UnsupportedOperationException()

}



interface IXiew : IEnabled, IVisible, IClicks<IXiew>

interface IDiew<T> : IData<T>, IXiew

interface ITiew : IDiew<String>

interface IMovDiew : IDiew<Int>, IMovData

interface IEdtTiew : ITiew, IChanges<String>

interface IBtnTiew : ITiew

interface IChkDiew : IDiew<Boolean>


interface Txt2TxtDiew : IDiew<Pair<String, String>>
interface Txt2ChkDiew : IDiew<Pair<String, Boolean>>



interface IUrlImageXiew : IXiew {
    var url: String // displays image available at specified url
}



// TODO NOW: uzyc tego w MyIntent i MyHub
interface ILstDiew<I> : IDiew<ILst<I>>
