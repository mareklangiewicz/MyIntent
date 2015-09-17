package pl.mareklangiewicz.myviews;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale1d;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * An arrow/menu animation
 */
public class MyArrowDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, Rect bounds, @IntRange(from=0,to=10000) int level) {
        int widthBy4 = bounds.width() / 4;
        int heightBy4 = bounds.height() / 4;
        int cx = bounds.centerX();
        int cy = bounds.centerY();
        int topBarY = bounds.top + heightBy4;
        int bottomBarY = bounds.bottom - heightBy4;
        int leftX = bounds.left + widthBy4;
        int rightX = bounds.right - widthBy4;

        // middle bar
        path.moveTo(leftX, cy);
        path.lineTo(rightX, cy);

        // top bar
        path.moveTo(scale1d(level, 0, 10000, leftX, cx), topBarY);
        path.lineTo(rightX, scale1d(level, 0, 10000, topBarY, cy));

        // bottom bar
        float y = scale1d(level, 0, 10000, bottomBarY, cy);
        if(y - cy < 2)
            path.lineTo(rightX, y);
        else
            path.moveTo(rightX, y);
        path.lineTo(scale1d(level, 0, 10000, leftX, cx), bottomBarY);

    }
}
