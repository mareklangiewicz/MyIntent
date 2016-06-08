package pl.mareklangiewicz.myviews

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 04.06.16.
 * Simple wrapper on android RecyclerView
 */

open class MyRecyclerView<I, V: AView<*>>(rview: RecyclerView,
        protected val create: () -> V,
        protected val bind: V.(I) -> Unit,
        protected var lst: ILst<I> = Lst<I>()
) : AView<RecyclerView>(rview), ILstView<I> {

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
            lst = value
            adapter.notifyDataSetChanged()
        }


    override val changes = Relay<ILst.IChange<I>>()

    inner class VH(val aview: V) : RecyclerView.ViewHolder(aview.view)

    inner class Adapter() : RecyclerView.Adapter<VH>()  {
        override fun getItemCount() = lst.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(create())
        override fun onBindViewHolder(holder: VH, position: Int) = holder.aview.bind(lst[position])
    }

}