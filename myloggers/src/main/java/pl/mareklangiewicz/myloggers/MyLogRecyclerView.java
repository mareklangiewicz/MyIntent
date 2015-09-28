package pl.mareklangiewicz.myloggers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.noveogroup.android.log.MyLogger;

/**
 * Created by Marek Langiewicz on 22.07.15.
 * Recycler view lis that displays log messages
 */
public final class MyLogRecyclerView extends RecyclerView {

    private final MyLogAdapter mAdapter = new MyLogAdapter();

    public MyLogRecyclerView(Context context) {
        super(context);
        init();
    }

    public MyLogRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyLogRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

/*
        LinearLayoutManager manager = new LinearLayoutManager(getContext()) {
            @Override
            public void onItemsChanged(RecyclerView recyclerView) {
                super.onItemsChanged(recyclerView);
                scrollToPosition(0);
            }
        };
*/

        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        manager.setReverseLayout(true);
        setLayoutManager(manager);

        setAdapter(mAdapter);

    }

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyLogger object from UI thread only - if you want to use this view with it.
     */
    public void setLog(@Nullable MyLogger log) {
        mAdapter.setLog(log);
    }

}
