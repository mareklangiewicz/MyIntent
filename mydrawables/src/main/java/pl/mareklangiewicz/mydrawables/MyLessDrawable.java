package pl.mareklangiewicz.mydrawables;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A less/equal animation
 */
public class MyLessDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, @IntRange(from=0,to=10000) int level, Rect bounds, int cx, int cy) {

        int w4 = bounds.width() / 4;
        int h4 = bounds.height() / 4;

        int h8 = bounds.height() / 8;

        ln(cx + w4, lvl(cy - h4, cy - h8), cx - w4, lvl(cy, cy - h8));

        int y = lvl(cy, cy + h8);
        if(y != cy)
            path.moveTo(cx - w4, y);

        path.lineTo(cx + w4, lvl(cy + h4, cy + h8));

    }
}
