package pl.mareklangiewicz.myutils;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import java.util.Random;

/**
 * Created by marek on 15.07.15.
 */
public final class MyMathUtils {

    static public final Random RANDOM = new Random();

    private MyMathUtils() {};







    static public int scale0d(int old, int oldMax, int newMax) {
        return old * newMax / oldMax;
    }

    static public int scale1d(int old, int oldMin, int oldMax, int newMin, int newMax) {
        return scale0d(old-oldMin, oldMax-oldMin, newMax-newMin) + newMin;
    }

    static public Point scale2d(Point old, Point oldMin, Point oldMax, Point newMin, Point newMax) {
        return new Point(
                scale1d(old.x, oldMin.x, oldMax.x, newMin.x, newMax.x),
                scale1d(old.y, oldMin.y, oldMax.y, newMin.y, newMax.y));
    }

    static public Point scale2d(Point old, Rect oldRange, Rect newRange) {
        return scale2d(
                old,
                new Point(oldRange.left, oldRange.top),
                new Point(oldRange.right, oldRange.bottom),
                new Point(newRange.left, newRange.top),
                new Point(newRange.right, newRange.bottom));
    }


    static public int getRandomInt(int min, int max) {
        return scale1d(RANDOM.nextInt(), 0, 1, min, max);
    }

    static public Point getRandomPoint(Point min, Point max) {
        return new Point(getRandomInt(min.x, max.x), getRandomInt(min.y, max.y));
    }



    static public float scale0d(float old, float oldMax, float newMax) {
        return old * newMax / oldMax;
    }

    static public float scale1d(float old, float oldMin, float oldMax, float newMin, float newMax) {
        return scale0d(old-oldMin, oldMax-oldMin, newMax-newMin) + newMin;
    }

    static public PointF scale2d(PointF old, PointF oldMin, PointF oldMax, PointF newMin, PointF newMax) {
        return new PointF(
                scale1d(old.x, oldMin.x, oldMax.x, newMin.x, newMax.x),
                scale1d(old.y, oldMin.y, oldMax.y, newMin.y, newMax.y));
    }

    static public PointF scale2d(PointF old, RectF oldRange, RectF newRange) {
        return scale2d(
                old,
                new PointF(oldRange.left, oldRange.top),
                new PointF(oldRange.right, oldRange.bottom),
                new PointF(newRange.left, newRange.top),
                new PointF(newRange.right, newRange.bottom));
    }


    static public float getRandomFloat(float min, float max) {
        return scale1d(RANDOM.nextFloat(), 0, 1, min, max);
    }

    static public PointF getRandomPointF(PointF min, PointF max) {
        return new PointF(getRandomFloat(min.x, max.x), getRandomFloat(min.y, max.y));
    }

    public static @ColorInt
    int getRandomColor(@ColorInt int colormin, @ColorInt int colormax) {
        return Color.argb(
                getRandomInt(Color.alpha(colormin), Color.alpha(colormax)),
                getRandomInt(Color.red(colormin), Color.red(colormax)),
                getRandomInt(Color.green(colormin), Color.green(colormax)),
                getRandomInt(Color.blue(colormin), Color.blue(colormax))
                );

    }
}
