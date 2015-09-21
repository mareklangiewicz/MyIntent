package pl.mareklangiewicz.mydrawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale0d;
import static pl.mareklangiewicz.myutils.MyMathUtils.scale1d;

/**
 * Created by Marek Langiewicz on 16.09.15.
 * Base class for simple animated drawables.
 * You only have to override the drawLivingPath method.
 */
public class MyLivingDrawable extends Drawable {

    protected float mRotateFrom = 0f;
    protected float mRotateTo = 0f;

    protected int mColorFrom = -1;
    protected int mColorTo = -1;

    protected final Paint mPaint = new Paint();
    protected final Path mPath = new Path();

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
        mPaint.setColor(color);
        mColorFrom = -1;
        mColorTo = -1;
        invalidateSelf();
        return this;
    }

    public MyLivingDrawable setColorFrom(@ColorInt int color) {
        mColorFrom = color;
        if(mColorTo == -1)
            mColorTo = mPaint.getColor();
        return this;
    }

    public MyLivingDrawable setColorTo(@ColorInt int color) {
        mColorTo = color;
        if(mColorFrom == -1)
            mColorFrom = mPaint.getColor();
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
     * TODO LATER: description
     * @param lfrom start level
     * @param lto end level
     * @param from start returned value
     * @param to end returned value
     * @return a value in range: from .. to corresponding to level in range lfrom..lto
     */
    protected int lvl(int lfrom, int lto, int from, int to) {
        int val = scale1d(getLevel(), lfrom, lto, from, to);
        int min = from < to ? from : to;
        int max = from < to ? to : from;
        if(val < min)
            val = min;
        if(val > max)
            val = max;
        return val;
    }
    protected int lvl(int from, int to) { return lvl(0, 10000, from, to); }

    /**
     * TODO LATER: description
     * @param lfrom start level
     * @param lto end level
     * @param from start returned value
     * @param to end returned value
     * @return a value in range: from .. to corresponding to level in range lfrom..lto
     */
    protected float lvl(int lfrom, int lto, float from, float to) {
        float val = scale1d(getLevel(), lfrom, lto, from, to);
        float min = from < to ? from : to;
        float max = from < to ? to : from;
        if(val < min)
            val = min;
        if(val > max)
            val = max;
        return val;
    }
    protected float lvl(float from, float to) { return lvl(0, 10000, from, to); }

    protected int lvlcolor(int colorFrom, int colorTo) {
        float fraction = scale0d((float) getLevel(), 10000f, 1f);
        int startA = (colorFrom >> 24) & 0xff;
        int startR = (colorFrom >> 16) & 0xff;
        int startG = (colorFrom >> 8) & 0xff;
        int startB = colorFrom & 0xff;

        int endA = (colorTo >> 24) & 0xff;
        int endR = (colorTo >> 16) & 0xff;
        int endG = (colorTo >> 8) & 0xff;
        int endB = colorTo & 0xff;

        return ((startA + (int)(fraction * (endA - startA))) << 24) |
                ((startR + (int)(fraction * (endR - startR))) << 16) |
                ((startG + (int)(fraction * (endG - startG))) << 8) |
                ((startB + (int)(fraction * (endB - startB))));
    }


    /**
     * TODO LATER: description
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    protected boolean ln(int x1, int y1, int x2, int y2) {
        if(x1 == x2 && y1 == y2)
            return false;
        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y2);
        return true;
    }

    /**
     * Override it, and draw some lines etc on given path object.
     * @param path A path to draw on (path.moveTo(3,4); path.lineTo(15,20); etc...
     * @param level An animation progress. Animation starts at 0 and ends at 10000
     * @param bounds Bounds of this drawable
     * @param cx bounds.centerX()
     * @param cy bounds.centerY()
     */
    public void drawLivingPath(Path path, @IntRange(from=0,to=10000) int level, Rect bounds, int cx, int cy) {}

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int cx = bounds.centerX();
        int cy = bounds.centerY();
        if(mColorFrom != -1)
            mPaint.setColor(lvlcolor(mColorFrom, mColorTo));
        mPath.rewind();
        drawLivingPath(mPath, getLevel(), bounds, cx, cy);
        if(mRotateFrom != 0 || mRotateTo != 0) {
            Matrix m = new Matrix();
            m.setRotate(lvl(mRotateFrom, mRotateTo), cx, cy);
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
