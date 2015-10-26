package pl.mareklangiewicz.myintent;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Marek Langiewicz on 22.10.15.
 */
public class RERulesTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    private final RERulesAdapter mAdapter;

    public RERulesTouchHelperCallback(RERulesAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
        mAdapter = adapter;
    }

    @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int pos1 = viewHolder.getAdapterPosition();
        int pos2 = target.getAdapterPosition();
        return mAdapter.move(pos1, pos2);
    }

    @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        mAdapter.remove(pos);
    }

}
