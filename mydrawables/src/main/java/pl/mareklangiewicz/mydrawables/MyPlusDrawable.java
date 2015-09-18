package pl.mareklangiewicz.mydrawables;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A plus/minus animation
 */
public class MyPlusDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, @IntRange(from=0,to=10000) int level, Rect bounds, int cx, int cy) {
        int w4 = bounds.width() / 4;
        int h4 = bounds.height() / 4;

        // horizontal line
        ln(bounds.left + w4, cy, bounds.right - w4, cy);

        // vertical line
        int h = lvl(0, h4);
        ln(cx, cy - h, cx, cy + h);
    }
}
