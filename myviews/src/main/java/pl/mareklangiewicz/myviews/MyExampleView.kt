package pl.mareklangiewicz.myviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView

class MyExampleView : TextView {

    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }

    private val area = Rect()

    constructor(context: Context) : super(context) { }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { }

    override fun onDraw(canvas: Canvas) {
        canvas.getClipBounds(area)
        canvas.drawLine(area.centerX().toFloat(), area.top.toFloat(), area.left.toFloat(), area.centerY().toFloat(), paint)
        canvas.drawLine(area.centerX().toFloat(), area.top.toFloat(), area.right.toFloat(), area.centerY().toFloat(), paint)
        canvas.drawLine(area.centerX().toFloat(), area.bottom.toFloat(), area.left.toFloat(), area.centerY().toFloat(), paint)
        canvas.drawLine(area.centerX().toFloat(), area.bottom.toFloat(), area.right.toFloat(), area.centerY().toFloat(), paint)
        super.onDraw(canvas)
    }
}
