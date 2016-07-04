package pl.mareklangiewicz.myviews

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 04.06.16.
 * Simple wrapper on android RecyclerView
 */

abstract class RecyclerDiew<I, V: AXiew<*>>(val rview: RecyclerView) : ADiew<RecyclerView, ILst<I>>(rview), ILstDiew<I>, IArr<V> {

    abstract fun create(): V
    abstract fun bind(view: V, item: I)

    protected var lst: ILst<I> = Lst()
    protected val adapter = Adapter()

    init {
        rview.adapter = adapter
        if(rview.layoutManager == null)
            rview.layoutManager = LinearLayoutManager(rview.context)
    }

    override var data: ILst<I> = object : ILst<I> by lst {
        override fun set(idx: Int, item: I) {
            super.set(idx, item)
            adapter.notifyItemChanged(idx)
            changes.push(ILst.Modify(idx, item))
        }

        override fun ins(idx: Int, item: I) {
            super.ins(idx, item)
            adapter.notifyItemInserted(idx)
            changes.push(ILst.Insert(idx, item))
        }

        override fun del(idx: Int): I {
            val item = super.del(idx)
            adapter.notifyItemRemoved(idx)
            changes.push(ILst.Delete(idx))
            return item
        }

        override fun mov(src: Int, dst: Int) {
            // override to avoid notifying adapter about insertion and deletion separately
            lst.mov(src, dst)
            adapter.notifyItemMoved(src, dst)
            changes.push(ILst.Move(src, dst))
        }
    }
        set(value) {
            lst = Lst.from(value)
            adapter.notifyDataSetChanged()
        }

    override val changes = Relay<ILst.IChange<I>>()

    inner class VH(val aview: V) : RecyclerView.ViewHolder(aview.view)

    inner class Adapter() : RecyclerView.Adapter<VH>()  {
        override fun getItemCount() = lst.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(create())
        override fun onBindViewHolder(holder: VH, position: Int) = bind(holder.aview, lst[position])
    }

    /** Warning: It can throw NPE in certain cases e.g. when RecyclerView is changing. See findViewHolderForAdapterPosition docs. */
    @Suppress("UNCHECKED_CAST")
    override fun get(idx: Int): V = (rview.findViewHolderForAdapterPosition(idx) as RecyclerDiew<I, V>.VH).aview
}