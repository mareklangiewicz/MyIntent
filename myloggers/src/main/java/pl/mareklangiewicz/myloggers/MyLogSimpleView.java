package pl.mareklangiewicz.myloggers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.noveogroup.android.log.MyLogger;

/**
 * Created by Marek Langiewicz on 30.06.15.
 */
public final class MyLogSimpleView extends View {
    private final Paint mPaint = new Paint();
    private @Nullable MyLogger log;
    private int mLines = 16;

    public MyLogSimpleView(Context context) {
        super(context);
        init();
    }

    public MyLogSimpleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyLogSimpleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setTypeface(Typeface.MONOSPACE);
        mPaint.setARGB(255, 0, 0, 0);
    }

    public void setPaint(@NonNull Paint paint) {
        mPaint.set(paint);
        invalidate();
    }

    public int getLines() { return mLines; }

    public void setLines(int lines) {
        if(lines < 0 || lines > 200)
            throw new IllegalArgumentException();
        mLines = lines;
        invalidate();
    }

    /**
     * WARNING: remember to set it back to null if the view is not used anymore - to avoid memory leaks
     * WARNING: use MyLogger object from UI thread only - if you want to this view to use it.
     */
    public void setLog(@Nullable MyLogger log) {
        if(this.log != null)
            this.log.setInvalidateView(null);
        this.log = log;
        if(this.log != null)
            this.log.setInvalidateView(this);
        invalidate();
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if(log != null)
            log.drawHistoryOnCanvas(canvas, 4, canvas.getHeight() - 2, mPaint, mLines);
        super.onDraw(canvas);
    }
}
