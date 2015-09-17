package pl.mareklangiewicz.myviews;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale0d;
import static pl.mareklangiewicz.myutils.MyMathUtils.scale1d;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A plus/minus animation
 */
public class MyPlusDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, Rect bounds, @IntRange(from=0,to=10000) int level) {
        int widthBy4 = bounds.width() / 4;
        int heightBy4 = bounds.height() / 4;
        int cx = bounds.centerX();
        int cy = bounds.centerY();

        // horizontal line
        path.moveTo(bounds.left + widthBy4, cy);
        path.lineTo(bounds.right - widthBy4, cy);

        // vertical line
        int h = scale0d(level, 10000, heightBy4);
        if(h > 1) {
            path.moveTo(cx, cy - h);
            path.lineTo(cx, cy + h);
        }
    }
}
