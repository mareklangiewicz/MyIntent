package pl.mareklangiewicz.myviews

import pl.mareklangiewicz.myutils.*

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

interface IXiew : IEnabled, IVisible, IClicks<IXiew>

interface IDiew<T> : IData<T>, IXiew

interface IProgressDiew : IProgress, IDiew<Int>

interface ITiew : IDiew<String>

interface IEditTiew : ITiew, IChanges<String>

interface IButtonTiew : ITiew

interface ICheckBoxDiew : IDiew<Boolean>



interface ILabel : IXiew {
    val label: ITiew
}

interface ILabDiew<T> : IDiew<T>, ILabel
interface ILabTiew : ITiew, ILabDiew<String>
interface ILabEditTiew : IEditTiew, ILabTiew
interface ILabCheckBoxDiew : ICheckBoxDiew, ILabDiew<Boolean>




interface IUrlImageXiew : IXiew {
    var url: String // displays image available at specified url
}



interface ILstDiew<I> : IDiew<ILst<I>>, IChanges<ILst.IChange<I>>

// TODO NOW: uzyc tego w MyIntent i MyHub
