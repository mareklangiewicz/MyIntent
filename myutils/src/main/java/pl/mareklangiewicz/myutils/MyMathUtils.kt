package pl.mareklangiewicz.myutils

import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.support.annotation.ColorInt

import java.util.Random

/**
 * Created by Marek Langiewicz on 15.07.15.
 */



fun Int.scale0d(oldMax: Int, newMax: Int) = (toLong() * newMax.toLong() / oldMax.toLong()).toInt()

fun Float.scale0d(oldMax: Float, newMax: Float) = this * newMax / oldMax

fun Double.scale0d(oldMax: Double, newMax: Double) = this * newMax / oldMax

fun Int.scale1d(oldMin: Int, oldMax: Int, newMin: Int, newMax: Int) = (this - oldMin).scale0d(oldMax - oldMin, newMax - newMin) + newMin

fun Float.scale1d(oldMin: Float, oldMax: Float, newMin: Float, newMax: Float) =
        (this - oldMin).scale0d(oldMax - oldMin, newMax - newMin) + newMin

fun Double.scale1d(oldMin: Double, oldMax: Double, newMin: Double, newMax: Double) =
        (this - oldMin).scale0d(oldMax - oldMin, newMax - newMin) + newMin

fun Point.scale2d(oldMin: Point, oldMax: Point, newMin: Point, newMax: Point) = Point(
        x.scale1d(oldMin.x, oldMax.x, newMin.x, newMax.x),
        y.scale1d(oldMin.y, oldMax.y, newMin.y, newMax.y))

fun Point.scale2d(oldRange: Rect, newRange: Rect) = scale2d(
        Point(oldRange.left, oldRange.top),
        Point(oldRange.right, oldRange.bottom),
        Point(newRange.left, newRange.top),
        Point(newRange.right, newRange.bottom))

fun PointF.scale2d(oldMin: PointF, oldMax: PointF, newMin: PointF, newMax: PointF) = PointF(
        x.scale1d(oldMin.x, oldMax.x, newMin.x, newMax.x),
        y.scale1d(oldMin.y, oldMax.y, newMin.y, newMax.y))

fun PointF.scale2d(oldRange: RectF, newRange: RectF) = scale2d(
        PointF(oldRange.left, oldRange.top),
        PointF(oldRange.right, oldRange.bottom),
        PointF(newRange.left, newRange.top),
        PointF(newRange.right, newRange.bottom))



val RANDOM = Random()



// result should be in the half-open range: [min, max)
fun Random.nextInt(min: Int, max: Int) = nextInt(Int.MAX_VALUE).scale1d(0, Int.MAX_VALUE, min, max)

// result should be in the half-open range: [min, max)
fun Random.nextFloat(min: Float, max: Float) = nextFloat().scale1d(0f, 1f, min, max)

// result should be in the half-open range: [min, max)
fun Random.nextDouble(min: Double, max: Double) = nextDouble().scale1d(0.0, 1.0, min, max)

// result should be in the half-open range: [min, max)
fun Random.nextPoint(min: Point, max: Point) = Point(nextInt(min.x, max.x), nextInt(min.y, max.y))

// result should be in the half-open range: [min, max)
fun Random.nextPointF(min: PointF, max: PointF) = PointF(nextFloat(min.x, max.x), nextFloat(min.y, max.y))



// result should be in the half-open range: [min, max)
@ColorInt fun Random.nextColor(@ColorInt colormin: Int, @ColorInt colormax: Int): Int = Color.argb(
        nextInt(Color.alpha(colormin), Color.alpha(colormax)),
        nextInt(Color.red(colormin), Color.red(colormax)),
        nextInt(Color.green(colormin), Color.green(colormax)),
        nextInt(Color.blue(colormin), Color.blue(colormax)))




// common random pullees:

fun Random.ints(min: Int, max: Int): IPullee<Int>  = { nextInt(min, max) }
fun Random.floats(min: Float, max: Float): IPullee<Float> = { nextFloat(min, max) }
fun Random.doubles(min: Double, max: Double): IPullee<Double> = { nextDouble(min, max) }
fun Random.points(min: Point, max: Point): IPullee<Point> = { nextPoint(min, max) }
fun Random.pointfs(min: PointF, max: PointF): IPullee<PointF> = { nextPointF(min, max) }
fun Random.colors(@ColorInt min: Int,@ColorInt max: Int): IPullee<Int> = { nextColor(min, max) }

