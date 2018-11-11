package pl.mareklangiewicz.mydrawables

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.IntRange
import pl.mareklangiewicz.myutils.scale0d
import pl.mareklangiewicz.myutils.scale1d

/**
 * Created by Marek Langiewicz on 16.09.15.
 * Base class for simple animated drawables.
 * You only have to override the drawLivingPath method.
 */
open class MyLivingDrawable : Drawable() {

    protected val mPaint = Paint()
    protected val mPath = Path()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.BUTT
        mPaint.strokeJoin = Paint.Join.MITER
        mPaint.strokeWidth = 0f
        mPaint.isAntiAlias = true
    }

    var rotateFrom: Float = 0f
        set(from: Float) {
            field = from
            invalidateSelf()
        }

    var rotateTo: Float = 0f
        set(to: Float) {
            field = to
            invalidateSelf()
        }

    var color: Int
        get() = mPaint.color
        set(color: Int) {
            mPaint.color = color
            colorFrom = -1
            colorTo = -1
            invalidateSelf()
        }

    var  colorFrom = -1
        set(value: Int) {
            field = value
            if (value != -1 && colorTo == -1)
                colorTo = mPaint.color
        }

    var colorTo = -1
        set(value: Int) {
            field = value
            if (value != -1 && colorFrom == -1)
                colorFrom = mPaint.color
        }

    var strokeWidth: Float
        get() = mPaint.strokeWidth
        set(width: Float) {
            if (mPaint.strokeWidth != width) {
                mPaint.strokeWidth = width
                invalidateSelf()
            }
        }

    @IntRange(from = 0, to = 0xff) override fun getAlpha(): Int {
        return mPaint.alpha
    }

    override fun setAlpha(@IntRange(from = 0, to = 0xff) alpha: Int) {
        if (alpha != mPaint.alpha) {
            mPaint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun getColorFilter(): ColorFilter {
        return mPaint.colorFilter
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    /**
     * similar to MyMathUtils.scale1d, but uses drawable level as input;
     * Also it keeps output between 'from' and 'to' even if level is not between 'lfrom' and 'lto'
     * See code for details.
     * @param lfrom start level
     * @param lto   end level
     * @param from  start returned value
     * @param to    end returned value
     * @return a value in range: from .. to corresponding to level in range lfrom..lto
     */
    protected fun lvl(@IntRange(from = 0, to = 10000) lfrom: Int, @IntRange(from = 0, to = 10000) lto: Int, from: Int, to: Int): Int {
        var value = level.scale1d(lfrom, lto, from, to)
        val min = if (from < to) from else to
        val max = if (from < to) to else from
        if (value < min)
            value = min
        if (value > max)
            value = max
        return value
    }

    protected fun lvl(from: Int, to: Int): Int = lvl(0, 10000, from, to)

    /**
     * Floating point version of the above
     * @param lfrom start level
     * @param lto   end level
     * @param from  start returned value
     * @param to    end returned value
     * @return a value in range: from .. to corresponding to level in range lfrom..lto
     */
    protected fun lvl(@IntRange(from = 0, to = 10000) lfrom: Int, @IntRange(from = 0, to = 10000) lto: Int, from: Float, to: Float): Float {
        var value = level.toFloat().scale1d(lfrom.toFloat(), lto.toFloat(), from, to)
        val min = if (from < to) from else to
        val max = if (from < to) to else from
        if (value < min)
            value = min
        if (value > max)
            value = max
        return value
    }

    protected fun lvl(from: Float, to: Float): Float = lvl(0, 10000, from, to)

    protected fun lvlcolor(colorFrom: Int, colorTo: Int): Int {
        val fraction = level.toFloat().scale0d(10000f, 1f)
        val startA = colorFrom shr 24 and 0xff
        val startR = colorFrom shr 16 and 0xff
        val startG = colorFrom shr 8 and 0xff
        val startB = colorFrom and 0xff

        val endA = colorTo shr 24 and 0xff
        val endR = colorTo shr 16 and 0xff
        val endG = colorTo shr 8 and 0xff
        val endB = colorTo and 0xff

        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }


    /**
     * Just look at the code..
     */
    protected fun ln(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        if (x1 == x2 && y1 == y2)
            return false
        mPath.moveTo(x1.toFloat(), y1.toFloat())
        mPath.lineTo(x2.toFloat(), y2.toFloat())
        return true
    }

    /**
     * Override it, and draw some lines etc on given path object.
     * @param path   A path to draw on (path.moveTo(3,4); path.lineTo(15,20); etc...
     * @param level  An animation progress. Animation starts at 0 and ends at 10000
     * @param bounds Bounds of this drawable
     * @param cx     bounds.centerX()
     * @param cy     bounds.centerY()
     */
    open fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) { }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val cx = bounds.centerX()
        val cy = bounds.centerY()
        if (colorFrom != -1)
            mPaint.color = lvlcolor(colorFrom, colorTo)
        mPath.rewind()
        drawLivingPath(mPath, level, bounds, cx, cy)
        if (rotateFrom != 0f || rotateTo != 0f) {
            val m = Matrix()
            m.setRotate(lvl(from = rotateFrom, to = rotateTo), cx.toFloat(), cy.toFloat())
            mPath.transform(m)
        }
        canvas.drawPath(mPath, mPaint)
    }

    override fun onLevelChange(@IntRange(from = 0, to = 10000) level: Int): Boolean {
        invalidateSelf()
        return true
    }
}
