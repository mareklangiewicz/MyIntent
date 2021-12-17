package pl.mareklangiewicz.myloggers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import pl.mareklangiewicz.upue.*
import pl.mareklangiewicz.myutils.MyLogEntry

/**
 * Created by Marek Langiewicz on 30.06.15.
 */
class MyAndroLogView : View {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }


    var paint = Paint()
            set(value) {
                field.set(value)
                invalidate()
            }

    var arr: IArr<MyLogEntry>? = null
        set(value) {
            field = value
            invalidate()
        }

    var lines = 16
        set(value) {
            if (value < 0 || value > 200)
                throw IllegalArgumentException()
            field = value
            invalidate()
        }

    private fun init() {
        paint.typeface = Typeface.MONOSPACE
        paint.setARGB(255, 0, 0, 0)
    }



    override fun onDraw(canvas: Canvas) {
        arr?.run { draw(canvas, 4, height - 2, paint, lines) }
        super.onDraw(canvas)
    }
}
