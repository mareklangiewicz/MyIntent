package pl.mareklangiewicz.mydrawables

import android.graphics.Path
import android.graphics.Rect
import android.support.annotation.IntRange


/**
 * Created by Marek Langiewicz on 16.09.15.
 * An arrow/menu animation
 */
class MyArrowDrawable : MyLivingDrawable() {

    override fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) {
        val w4 = bounds.width() / 4
        val h4 = bounds.height() / 4
        val h8 = bounds.height() / 8
        val topBarY = cy - h8
        val bottomBarY = cy + h8
        val topArrY = cy - h4
        val bottomArrY = cy + h4
        val leftX = bounds.left + w4
        val rightX = bounds.right - w4

        // middle bar
        ln(leftX, cy, rightX, cy)

        // top bar
        ln(lvl(leftX, cx), lvl(topBarY, topArrY), rightX, lvl(topBarY, cy))

        // bottom bar
        val y = lvl(bottomBarY, cy).toFloat()
        if (y - cy < 2) path.lineTo(rightX.toFloat(), y)
        else path.moveTo(rightX.toFloat(), y)
        path.lineTo(lvl(leftX, cx).toFloat(), lvl(bottomBarY, bottomArrY).toFloat())
    }
}
