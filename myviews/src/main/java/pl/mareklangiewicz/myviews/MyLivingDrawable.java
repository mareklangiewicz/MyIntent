package pl.mareklangiewicz.myviews;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale1d;

/**
 * Created by marek on 16.09.15.
 */
public class MyLivingDrawable extends Drawable {

    protected float mRotateFrom = 0f;
    protected float mRotateTo = 0f;

    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();

    public MyLivingDrawable() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeWidth(0);
        mPaint.setAntiAlias(true);
    }

    public float getRotateFrom() { return mRotateFrom; }

    public MyLivingDrawable setRotateFrom(float from) {
        mRotateFrom = from;
        invalidateSelf();
        return this;
    }

    public float getRotateTo() { return mRotateTo; }

    public MyLivingDrawable setRotateTo(float to) {
        mRotateTo = to;
        invalidateSelf();
        return this;
    }

    public @ColorInt int getColor() {
        return mPaint.getColor();
    }

    public MyLivingDrawable setColor(@ColorInt int color) {
        if (color != mPaint.getColor()) {
            mPaint.setColor(color);
            invalidateSelf();
        }
        return this;
    }

    public float getStrokeWidth() {
        return mPaint.getStrokeWidth();
    }

    public MyLivingDrawable setStrokeWidth(float width) {
        if (mPaint.getStrokeWidth() != width) {
            mPaint.setStrokeWidth(width);
            invalidateSelf();
        }
        return this;
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != mPaint.getAlpha()) {
            mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter); invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * Override it, and draw some lines etc on given path object.
     * @param path A path to draw on (path.moveTo(3,4); path.lineTo(15,20); etc...
     * @param bounds Bounds of this drawable
     * @param level An animation progress. Animation starts at 0 and ends at 10000
     */
    public void drawLivingPath(Path path, Rect bounds, @IntRange(from=0,to=10000) int level) {}

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        mPath.rewind();
        drawLivingPath(mPath, bounds, getLevel());
        if(mRotateFrom != 0 || mRotateTo != 0) {
            Matrix m = new Matrix();
            m.setRotate(scale1d(getLevel(), 0, 10000, mRotateFrom, mRotateTo), bounds.centerX(), bounds.centerY());
            mPath.transform(m);
        }
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }
}
