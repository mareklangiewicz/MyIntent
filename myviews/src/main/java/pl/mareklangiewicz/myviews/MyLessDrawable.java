package pl.mareklangiewicz.myviews;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale1d;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A less/equal animation
 */
public class MyLessDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, Rect bounds, @IntRange(from=0,to=10000) int level) {

        int widthBy4 = bounds.width() / 4;
        int heightBy4 = bounds.height() / 4;

        int widthBy8 = bounds.width() / 8;
        int heightBy8 = bounds.height() / 8;

        int cx = bounds.centerX();
        int cy = bounds.centerY();

        path.moveTo(cx + widthBy4, scale1d(level, 0, 10000, cy - heightBy4, cy - heightBy8));
        path.lineTo(cx - widthBy4, scale1d(level, 0, 10000, cy, cy - heightBy8));

        int y = scale1d(level, 0, 10000, cy, cy + heightBy8);
        if(y != cy)
            path.moveTo(cx - widthBy4, y);

        path.lineTo(cx + widthBy4, scale1d(level, 0, 10000, cy + heightBy4, cy + heightBy8));

    }
}
