package pl.mareklangiewicz.myviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView

class MyExampleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : TextView(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }

    private val area = Rect()

    override fun onDraw(canvas: Canvas) {
        canvas.getClipBounds(area)
        canvas.drawLine(area.centerX().toFloat(), area.top.toFloat(), area.left.toFloat(), area.centerY().toFloat(), paint)
        canvas.drawLine(area.centerX().toFloat(), area.top.toFloat(), area.right.toFloat(), area.centerY().toFloat(), paint)
        canvas.drawLine(area.centerX().toFloat(), area.bottom.toFloat(), area.left.toFloat(), area.centerY().toFloat(), paint)
        canvas.drawLine(area.centerX().toFloat(), area.bottom.toFloat(), area.right.toFloat(), area.centerY().toFloat(), paint)
        super.onDraw(canvas)
    }
}
