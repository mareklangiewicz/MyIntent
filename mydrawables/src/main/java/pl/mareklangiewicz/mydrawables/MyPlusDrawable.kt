package pl.mareklangiewicz.mydrawables

import android.graphics.Path
import android.graphics.Rect
import android.support.annotation.IntRange


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A plus/minus animation
 */
class MyPlusDrawable : MyLivingDrawable() {

    override fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) {
        val w4 = bounds.width() / 4
        val h4 = bounds.height() / 4

        // horizontal line
        ln(bounds.left + w4, cy, bounds.right - w4, cy)

        // vertical line
        val h = lvl(0, h4)
        ln(cx, cy - h, cx, cy + h)
    }
}
