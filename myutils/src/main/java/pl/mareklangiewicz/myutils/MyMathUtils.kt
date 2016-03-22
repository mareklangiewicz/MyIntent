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

val RANDOM = Random()


fun scale0d(old: Int, oldMax: Int, newMax: Int): Int = (old.toLong() * newMax.toLong() / oldMax.toLong()).toInt()

fun scale1d(old: Int, oldMin: Int, oldMax: Int, newMin: Int, newMax: Int): Int = scale0d(old - oldMin, oldMax - oldMin, newMax - newMin) + newMin

fun scale2d(old: Point, oldMin: Point, oldMax: Point, newMin: Point, newMax: Point): Point = Point(
        scale1d(old.x, oldMin.x, oldMax.x, newMin.x, newMax.x),
        scale1d(old.y, oldMin.y, oldMax.y, newMin.y, newMax.y))

fun scale2d(old: Point, oldRange: Rect, newRange: Rect): Point = scale2d(
        old,
        Point(oldRange.left, oldRange.top),
        Point(oldRange.right, oldRange.bottom),
        Point(newRange.left, newRange.top),
        Point(newRange.right, newRange.bottom))


fun getRandomInt(min: Int, max: Int): Int = scale1d(RANDOM.nextInt(Integer.MAX_VALUE shr 4), 0, Integer.MAX_VALUE shr 4, min, max)

fun getRandomPoint(min: Point, max: Point): Point = Point(getRandomInt(min.x, max.x), getRandomInt(min.y, max.y))


fun scale0d(old: Float, oldMax: Float, newMax: Float): Float = old * newMax / oldMax

fun scale1d(old: Float, oldMin: Float, oldMax: Float, newMin: Float, newMax: Float): Float =
        scale0d(old - oldMin, oldMax - oldMin, newMax - newMin) + newMin

fun scale2d(old: PointF, oldMin: PointF, oldMax: PointF, newMin: PointF, newMax: PointF): PointF = PointF(
        scale1d(old.x, oldMin.x, oldMax.x, newMin.x, newMax.x),
        scale1d(old.y, oldMin.y, oldMax.y, newMin.y, newMax.y))

fun scale2d(old: PointF, oldRange: RectF, newRange: RectF): PointF = scale2d(
        old,
        PointF(oldRange.left, oldRange.top),
        PointF(oldRange.right, oldRange.bottom),
        PointF(newRange.left, newRange.top),
        PointF(newRange.right, newRange.bottom))


fun getRandomFloat(min: Float, max: Float): Float = scale1d(RANDOM.nextFloat(), 0f, 1f, min, max)

fun getRandomPointF(min: PointF, max: PointF): PointF = PointF(getRandomFloat(min.x, max.x), getRandomFloat(min.y, max.y))

@ColorInt fun getRandomColor(@ColorInt colormin: Int, @ColorInt colormax: Int): Int = Color.argb(
        getRandomInt(Color.alpha(colormin), Color.alpha(colormax)),
        getRandomInt(Color.red(colormin), Color.red(colormax)),
        getRandomInt(Color.green(colormin), Color.green(colormax)),
        getRandomInt(Color.blue(colormin), Color.blue(colormax)))

