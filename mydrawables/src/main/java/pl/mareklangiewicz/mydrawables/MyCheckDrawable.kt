package pl.mareklangiewicz.mydrawables

import android.graphics.Path
import android.graphics.Rect
import androidx.annotation.IntRange


/**
 * Created by Marek Langiewicz on 16.09.15.
 * A check mark/error animation
 */
class MyCheckDrawable : MyLivingDrawable() {

    override fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) {

        val w4 = bounds.width() / 4
        val h4 = bounds.height() / 4

        ln(cx - w4, lvl(cy, cy - h4), lvl(cx, cx + w4), cy + h4)

        val x = lvl(cx, cx - w4)
        if (x != cx) path.moveTo(x.toFloat(), (cy + h4).toFloat())

        path.lineTo((cx + w4).toFloat(), (cy - h4).toFloat())
    }
}
