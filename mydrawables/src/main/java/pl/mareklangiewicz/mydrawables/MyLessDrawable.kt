package pl.mareklangiewicz.mydrawables

import android.graphics.Path
import android.graphics.Rect
import android.support.annotation.IntRange


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A less/equal animation
 */
class MyLessDrawable : MyLivingDrawable() {

    override fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) {

        val w4 = bounds.width() / 4
        val h4 = bounds.height() / 4

        val h8 = bounds.height() / 8

        ln(cx + w4, lvl(cy - h4, cy - h8), cx - w4, lvl(cy, cy - h8))

        val y = lvl(cy, cy + h8)
        if (y != cy) path.moveTo((cx - w4).toFloat(), y.toFloat())

        path.lineTo((cx + w4).toFloat(), lvl(cy + h4, cy + h8).toFloat())
    }
}
