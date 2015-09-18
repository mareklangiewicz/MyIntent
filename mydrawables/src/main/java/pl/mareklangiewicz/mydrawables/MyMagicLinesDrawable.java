package pl.mareklangiewicz.mydrawables;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * TODO LATER: description
 */
public class MyMagicLinesDrawable extends MyLivingDrawable {

    @Override
    public void drawLivingPath(Path path, @IntRange(from=0,to=10000) int level, Rect bounds, int cx, int cy) {
        int h4 = bounds.height() / 4;

        ln(bounds.left, cy - h4, lvl(   0,  5000, bounds.left, bounds.right), cy - h4);
        ln(bounds.left, cy     , lvl(5000, 10000, bounds.left, bounds.right), cy     );
        ln(bounds.left, cy + h4, lvl(5000, 10000, bounds.left, bounds.right), cy + h4);
    }

    // TODO LATER: setLinesCount
    // TODO LATER: setShowingStyle - like one line following another; or outrunning another etc..
    // TODO LATER: setRandomShowingStyle - we generate random delay and random speed for every line
    // TODO LATER: allow random with seed
    // TODO SOMEDAY MAYBE: setAlphaChangingStyle - although it can be done easily by user (ObjectAnimator)

}
