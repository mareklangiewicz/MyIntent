package pl.mareklangiewicz.mydrawables

import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.support.annotation.IntRange


/**
 * Created by Marek Langiewicz on 16.09.15.
 * An arrow/menu animation
 */
class MyPlayStopDrawable : MyLivingDrawable() {

    init {
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
    }

    override fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) {

        val w8 = bounds.width() / 8
        val h8 = bounds.height() / 8
        val y1 = cy - 3 * h8
        val y2 = cy + 3 * h8
        val x1 = cx - 3 * w8
        val x11 = cx - w8
        val x2 = cx + 3 * w8

        path.moveTo(lvl(x11, x1).toFloat(), lvl(cy, y1).toFloat())
        path.lineTo(lvl(x1, x2).toFloat(), y1.toFloat())
        path.lineTo(x2.toFloat(), lvl(cy, y2).toFloat())
        path.lineTo(x1.toFloat(), y2.toFloat())
        path.close()
    }
}
