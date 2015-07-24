/**
 * Created by marek on 24.06.15.
 */

package com.noveogroup.android.log;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class is not thread-safe. In particular you should use it only from UI thread
 * if you use features: setSnackView; setInvalidateView; getLogHistory;
 */
public class MyHandler extends PatternHandler {

    /**
     * User can swith it on for all MyHandlers to additionally print all messages
     * using System.out.println(...). It's a hack, but it can be useful in unit tests.
     */
    static public boolean SystemOutPrintLn = false;

    static public final String SNACK_TAG = "[SNACK]";
    static public final String SHORT_TAG = "[SHORT]";

    static private final int HISTORY_LEN = 40;

    private final LogHistory history = new LogHistory(HISTORY_LEN);

    private View mSnackView;
    private View mInvalidateView;
    private RecyclerView.Adapter mAdapter;



    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyHandler from UI thread only - if you want to use this feature.
     */
    public void setSnackView(@Nullable View snackView) {
        mSnackView = snackView;
    }

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyHandler from UI thread only - if you want to use this feature.
     */
    public void setInvalidateView(@Nullable View invalidateView) {
        mInvalidateView = invalidateView;
    }

    /**
     * WARNING: remember to set it back to null if the adapter is not used anymore - to avoid memory leaks
     * WARNING: use MyHandler from UI thread only - if you want to use this feature.
     */
    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    /**
     * WARNING: use MyHandler from one thread only (like UI thread) - if you want to use this feature.
     */
    public LogHistory getLogHistory() { return history; }

    void setHistoryFilterLevel(Logger.Level level) {
        history.setFilterLevel(level);
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
        if(mInvalidateView != null)
            mInvalidateView.invalidate();
    }

    /**
     * Creates new {@link MyHandler}.
     *
     * @param level          the level.
     * @param tagPattern     the tag pattern.
     * @param messagePattern the message pattern.
     */
    public MyHandler(Logger.Level level, String tagPattern, String messagePattern) {
        super(level, tagPattern, messagePattern);
    }

    @Override
    public void print(String loggerName, Logger.Level level, Throwable throwable, String message) throws IllegalArgumentException {

        if(message.startsWith(SNACK_TAG)) {
            message = message.substring(SNACK_TAG.length());
            int duration = Snackbar.LENGTH_LONG;
            if(message.startsWith(SHORT_TAG)) {
                message = message.substring(SHORT_TAG.length());
                duration = Snackbar.LENGTH_SHORT;
            }
            if(mSnackView != null)
                Snackbar.make(mSnackView, message, duration).show();
        }

        history.add(loggerName, level, message);

        if(mInvalidateView != null)
            mInvalidateView.invalidate();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();

        super.print(loggerName, level, throwable, message);

        if(SystemOutPrintLn) {
            message = String.format("%s     %s", Utils.getCaller().toString(), message);
            System.out.println(message);
        }
    }
}

