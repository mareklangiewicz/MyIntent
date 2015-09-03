package pl.mareklangiewicz.myloggers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.noveogroup.android.log.MyLogger;

/**
 * Created by marek on 22.07.15.
 */
public final class MyLogRecyclerView extends RecyclerView {

    private MyLogAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

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
        mLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public void onItemsChanged(RecyclerView recyclerView) {
                super.onItemsChanged(recyclerView);
                scrollToPosition(0);
            }
        };
*/

        mLayoutManager = new LinearLayoutManager(getContext());

        mLayoutManager.setReverseLayout(true);
        setLayoutManager(mLayoutManager);

        mAdapter = new MyLogAdapter();
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
