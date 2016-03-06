package pl.mareklangiewicz.mydrawables;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * An arrow/menu animation
 */
public class MyArrowDrawable extends MyLivingDrawable {

    @Override public void drawLivingPath(@NonNull Path path, @IntRange(from = 0, to = 10000) int level, @NonNull Rect bounds, int cx, int cy) {
        int w4 = bounds.width() / 4;
        int h4 = bounds.height() / 4;
        int h8 = bounds.height() / 8;
        int topBarY = cy - h8;
        int bottomBarY = cy + h8;
        int topArrY = cy - h4;
        int bottomArrY = cy + h4;
        int leftX = bounds.left + w4;
        int rightX = bounds.right - w4;

        // middle bar
        ln(leftX, cy, rightX, cy);

        // top bar
        ln(lvl(leftX, cx), lvl(topBarY, topArrY), rightX, lvl(topBarY, cy));

        // bottom bar
        float y = lvl(bottomBarY, cy);
        if(y - cy < 2)
            path.lineTo(rightX, y);
        else
            path.moveTo(rightX, y);
        path.lineTo(lvl(leftX, cx), lvl(bottomBarY, bottomArrY));

    }
}
