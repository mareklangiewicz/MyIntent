package pl.mareklangiewicz.myviews;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale0d;
import static pl.mareklangiewicz.myutils.MyMathUtils.scale1d;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A check mark/error animation
 */
public class MyCheckDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, Rect bounds, @IntRange(from=0,to=10000) int level) {

        int widthBy4 = bounds.width() / 4;
        int heightBy4 = bounds.height() / 4;
        int cx = bounds.centerX();
        int cy = bounds.centerY();

        path.moveTo(cx - widthBy4, scale1d(level, 0, 10000, cy, cy - heightBy4));
        path.lineTo(scale1d(level, 0, 10000, cx, cx + widthBy4), cy + heightBy4);

        int x = scale1d(level, 0, 10000, cx, cx - widthBy4);
        if(x != cx)
            path.moveTo(x, cy + heightBy4);

        path.lineTo(cx + widthBy4, cy - heightBy4);

    }
}
