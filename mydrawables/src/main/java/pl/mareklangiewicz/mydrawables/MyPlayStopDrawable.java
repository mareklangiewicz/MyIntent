package pl.mareklangiewicz.mydrawables;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * An arrow/menu animation
 */
public class MyPlayStopDrawable extends MyLivingDrawable {

    public MyPlayStopDrawable() {
        super();
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override public void drawLivingPath(@NonNull Path path, @IntRange(from = 0, to = 10000) int level, Rect bounds, int cx, int cy) {

        int w8 = bounds.width() / 8;
        int h8 = bounds.height() / 8;
        int y1 = cy - 3 * h8;
        int y2 = cy + 3 * h8;
        int x1 = cx - 3 * w8;
        int x11 = cx - w8;
        int x2 = cx + 3 * w8;

        path.moveTo(lvl(x11, x1), lvl(cy, y1));
        path.lineTo(lvl(x1, x2), y1);
        path.lineTo(x2, lvl(cy, y2));
        path.lineTo(x1, y2);
        path.close();
    }
}
