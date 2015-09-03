package com.noveogroup.android.log;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by marek on 24.06.15.
 */

// Flagged as UiThread to be on the safe side - we will change this requirement if needed.
@UiThread
public final class MyLogger extends AbstractLogger {

    /**
     * Default logger for use in UI thread
     */
    static public final MyLogger sMyDefaultUILogger = new MyLogger("ML");


    @NonNull
    private final MyHandler mHandler;

    public MyLogger() {
        this(Utils.getCallerClassName());
    }

    public MyLogger(String name) {
        this(name, Level.VERBOSE, "%logger", "%s");
    }

    public MyLogger(String name, Logger.Level level, String tagPattern, String messagePattern) {
        super(name);
        mHandler = new MyHandler(level, tagPattern, messagePattern);
    }

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyLogger from UI thread only - if you want to use this feature.
     */
    public void setSnackView(@Nullable View snackView) {
        mHandler.setSnackView(snackView);
    }

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyLogger from UI thread only - if you want to use this feature.
     */
    public void setInvalidateView(@Nullable View invalidateView) {
        mHandler.setInvalidateView(invalidateView);
    }

    /**
     * WARNING: remember to set it back to null if the adapter is not used anymore - to avoid memory leaks
     * WARNING: use MyLogger from UI thread only - if you want to use this feature.
     */
    public void setAdapter(@Nullable RecyclerView.Adapter adapter) {
        mHandler.setAdapter(adapter);
    }

    public LogHistory getLogHistory() { return mHandler.getLogHistory(); }

    public void setHistoryFilterLevel(Logger.Level level) { mHandler.setHistoryFilterLevel(level); }

    @Override
    public boolean isEnabled(Level level) {
        return mHandler.isEnabled(level);
    }

    @Override
    public void print(@NonNull Level level, @NonNull String message, @Nullable Throwable throwable) {
        mHandler.print(getName(), level, throwable, message);
    }

    @Override
    public void print(@NonNull Level level, @Nullable Throwable throwable, @NonNull String messageFormat, @NonNull Object... args) {
        mHandler.print(getName(), level, throwable, messageFormat, args);
    }

    static private final char LEVEL_CHARS[] = { 'X', 'X', 'V', 'D', 'I', 'W', 'E', 'A' };
    static public char getLevelChar(@NonNull Logger.Level level) { return LEVEL_CHARS[level.intValue()]; }

    public static final int COLOR_VERBOSE = 0xFFB0B0B0;
    public static final int COLOR_DEBUG = 0xFF606060;
    public static final int COLOR_INFO = 0xFF000000;
    public static final int COLOR_WARNING  = 0xFF0000A0;
    public static final int COLOR_ERROR  = 0xFFA00000;

    static public int getLevelColor(@NonNull Logger.Level level) {
        switch(level) {
            case ERROR: return COLOR_ERROR;
            case WARN: return COLOR_WARNING;
            case INFO: return COLOR_INFO;
            case DEBUG: return COLOR_DEBUG;
            case VERBOSE: return COLOR_VERBOSE;
            default: throw new IllegalArgumentException();
        }
    }

    public void drawHistoryOnCanvas(@NonNull Canvas canvas, int x, int y, @NonNull Paint paint, int lines) {
        int minY = canvas.getClipBounds().top;
        LogHistory history = getLogHistory();
        int N = history.getFilteredSize();
        if(N > lines)
            N = lines;
        for(int i = 0; i < N; ++i) {

            long id = history.getFilteredId(i);
            long time = history.getFilteredTime(i);
            Logger.Level level = history.getFilteredLevel(i);
            String message = history.getFilteredMessage(i);
            message = String.format("%03d %c: %tT: %s", id, getLevelChar(level), time, message);

            paint.setColor(getLevelColor(level));

            canvas.drawText(message, x, y, paint);

            y -= paint.getTextSize() + 2;
            if(y < minY)
                break;
            paint.setAlpha(paint.getAlpha() * 7 / 8);
        }

    }



}
