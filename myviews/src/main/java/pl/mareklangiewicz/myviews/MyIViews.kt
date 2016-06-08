package pl.mareklangiewicz.myviews

import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 27.05.16.
 */


interface IClicks<T> {
    val clicks: IPusher<T, Cancel>
        get() = throw UnsupportedOperationException()
}

interface IView : IEnabled, IVisible, IClicks<IView>

interface IDataView<T> : IData<T>, IView

interface IProgressView : IProgress, IDataView<Int>

interface ITextView : IDataView<String>

interface IEditTextView : ITextView, IChanges<String>

interface IButtonView : ITextView

interface ICheckBoxView : IDataView<Boolean>



interface ILabel : IView {
    val label: ITextView
}

interface ILabDataView<T> : IDataView<T>, ILabel
interface ILabTextView : ITextView, ILabDataView<String>
interface ILabEditTextView : IEditTextView, ILabTextView
interface ILabCheckBoxView : ICheckBoxView, ILabDataView<Boolean>




interface IUrlImageView : IView {
    var url: String // displays image available at specified url
}



interface ILstView<I> : IDataView<ILst<I>>, IChanges<ILst.IChange<I>>

// TODO NOW: uzyc tego w MyIntent i MyHub
