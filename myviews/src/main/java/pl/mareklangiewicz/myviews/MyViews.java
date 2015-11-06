package pl.mareklangiewicz.myviews;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Marek Langiewicz on 06.11.15.
 */
public final class MyViews {

    private MyViews() {
        throw new AssertionError("MyViews class is noninstantiable.");
    }

    public static boolean overlaps(@Nullable View v1, @Nullable View v2) {

        if(v1 == null || v2 == null)
            return false;

        if(v1.getVisibility() != View.VISIBLE || v2.getVisibility() != View.VISIBLE)
            return false;

//        int w1 = v1.getWidth();
        int w1 = v1.getMeasuredWidth();
        if(w1 < 1)
            return false;

//        int w2 = v2.getWidth();
        int w2 = v2.getMeasuredWidth();
        if(w2 < 1)
            return false;

//        int h1 = v1.getHeight();
        int h1 = v1.getMeasuredHeight();
        if(h1 < 1)
            return false;

//        int h2 = v2.getHeight();
        int h2 = v2.getMeasuredHeight();
        if(h2 < 1)
            return false;

        int[] pos1 = new int[2];
        int[] pos2 = new int[2];
        v1.getLocationInWindow(pos1);
        v2.getLocationInWindow(pos2);

        Rect r1 = new Rect(pos1[0], pos1[1], pos1[0]+w1, pos1[1]+h1);
        Rect r2 = new Rect(pos2[0], pos2[1], pos2[0]+w2, pos2[1]+h2);

        return Rect.intersects(r1, r2);
    }

}
