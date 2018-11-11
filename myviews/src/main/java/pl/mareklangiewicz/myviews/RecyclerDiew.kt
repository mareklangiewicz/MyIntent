package pl.mareklangiewicz.myviews

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 04.06.16.
 * Simple wrapper on android RecyclerView
 */

// TODO NOW: test it!!
abstract class RecyclerDiew<I, V: AXiew<*>>(rview: RecyclerView, data: ILst<I> ) : ADiew<RecyclerView, ILst<I>>(rview), ILstDiew<I>, IArr<V?> {

    constructor(rview: RecyclerView) : this(rview, LstWithChgPusher<I>()) {
        (data as LstWithChgPusher<I>).changes { notify(it) }
    }

    abstract fun create(): V
    abstract fun bind(view: V, item: I)

    override var data: ILst<I> = data
        /** User should notify RecyclerDiew after any change inside provided data through "notify" method */
        set(value) {
            field = value
            notify(LstOth())
        }

    protected val adapter = Adapter()

    init {
        view.adapter = adapter
        if(view.layoutManager == null)
            view.layoutManager = LinearLayoutManager(view.context)
    }

    fun notify(chg: LstChg<I>) = view.notify(chg)

    inner class VH(val aview: V) : RecyclerView.ViewHolder(aview.view)

    inner class Adapter() : RecyclerView.Adapter<VH>()  {
        override fun getItemCount() = data.len
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(create())
        override fun onBindViewHolder(holder: VH, position: Int) = bind(holder.aview, data[position])
    }

    /** Warning: It can return null in different cases e.g. when RecyclerView is changing. See findViewHolderForAdapterPosition docs. */
    @Suppress("UNCHECKED_CAST")
    override fun get(idx: Int): V? = (view.findViewHolderForAdapterPosition(idx) as? RecyclerDiew<I, V>.VH)?.aview
    override fun set(idx: Int, item: V?) { throw UnsupportedOperationException() }

    override fun clr() = data.clr()
    override val len: Int get() = data.len
}


