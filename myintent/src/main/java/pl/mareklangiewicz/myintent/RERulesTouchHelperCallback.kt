package pl.mareklangiewicz.myintent

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Marek Langiewicz on 22.10.15.
 */
class RERulesTouchHelperCallback(private val adapter: RERulesAdapter)
: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START or ItemTouchHelper.END) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
            = adapter.move(viewHolder.adapterPosition, target.adapterPosition)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = adapter.remove(viewHolder.adapterPosition)
}
