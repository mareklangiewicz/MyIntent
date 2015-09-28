package pl.mareklangiewicz.mydrawables;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A check mark/error animation
 */
public class MyCheckDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(@NonNull Path path, @IntRange(from = 0, to = 10000) int level, Rect bounds, int cx, int cy) {

        int w4 = bounds.width() / 4;
        int h4 = bounds.height() / 4;

        ln(cx - w4, lvl(cy, cy - h4), lvl(cx, cx + w4), cy + h4);

        int x = lvl(cx, cx - w4);
        if (x != cx) path.moveTo(x, cy + h4);

        path.lineTo(cx + w4, cy - h4);

    }
}
