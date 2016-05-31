package pl.mareklangiewicz.myviews

import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 27.05.16.
 */


interface IClicks<T> {
    val clicks: IPusher<T, (Unit) -> Unit>
        get() = throw UnsupportedOperationException()
}

interface IView : IEnabled, IVisible, IClicks<IView>

interface IDataView<T> : IData<T>, IView

interface IProgressView : IProgress, IDataView<Int>

interface ITextView : IDataView<String>

interface IEditTextView : ITextView, IChanges<String>

interface IButtonView : ITextView

interface ICheckBoxView : IDataView<Boolean>

interface IUrlImageView : IView {
    var url: String // displays image available at specified url
}

interface IItemListView<I> : IDataView<List<I>>, IChanges<List<I>> {
    // TODO NOW: change this interface totally. use my own interfaces instead of kotlin.List; use special changes protocol and data type


}


// TODO NOW: zdefiniowac jeszcze ostroznie jakis ogolny widok na kolekcje - tak zeby bylo ladnie i zeby latwo uzyc w MyHub (i w MyIntent tez)
// TODO NOW: zaimplementowac androidowe implementacje tych interfejsow w pliku MyAViews
// TODO NOW: uzyc tego w MyHub
