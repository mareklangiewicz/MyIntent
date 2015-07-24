package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class MyExampleView extends TextView {

    static private Paint mPaint;

    static {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    public MyExampleView(Context context) {
        super(context);
    }

    public MyExampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyExampleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect area = canvas.getClipBounds();
        canvas.drawLine(area.centerX(), area.top, area.left, area.centerY(), mPaint);
        canvas.drawLine(area.centerX(), area.top, area.right, area.centerY(), mPaint);
        canvas.drawLine(area.centerX(), area.bottom, area.left, area.centerY(), mPaint);
        canvas.drawLine(area.centerX(), area.bottom, area.right, area.centerY(), mPaint);
        super.onDraw(canvas);
    }

}
