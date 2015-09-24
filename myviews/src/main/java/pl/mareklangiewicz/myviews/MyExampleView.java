package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public final class MyExampleView extends TextView {

    static private Paint sPaint;
    private @NonNull Rect mArea = new Rect();

    static {
        sPaint = new Paint();
        sPaint.setColor(Color.BLACK);
        sPaint.setAntiAlias(true);
    }

    public MyExampleView(Context context) {
        super(context);
    }

    public MyExampleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyExampleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(mArea);
        canvas.drawLine(mArea.centerX(), mArea.top, mArea.left, mArea.centerY(), sPaint);
        canvas.drawLine(mArea.centerX(), mArea.top, mArea.right, mArea.centerY(), sPaint);
        canvas.drawLine(mArea.centerX(), mArea.bottom, mArea.left, mArea.centerY(), sPaint);
        canvas.drawLine(mArea.centerX(), mArea.bottom, mArea.right, mArea.centerY(), sPaint);
        super.onDraw(canvas);
    }

}
