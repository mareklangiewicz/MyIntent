package pl.mareklangiewicz.myviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import pl.mareklangiewicz.myutils.scale1d

class MyPie : View {

    private var _minimum: Float = 0f
    private var _maximum: Float = 100f
    private var _from: Float = 0f
    private var _to: Float = 75f

    var minimum: Float get() = _minimum; set(value) { _minimum = value; invalidate() }
    var maximum: Float get() = _maximum; set(value) { _maximum = value; invalidate() }
    var from:    Float get() = _from;    set(value) { _from = value; invalidate() }
    var to:      Float get() = _to;      set(value) { _to = value; invalidate() }

    private val mOvalDrawable = ShapeDrawable(OvalShape())
    private val mPieDrawable = ShapeDrawable(object : OvalShape() {
        override fun draw(canvas: Canvas, paint: Paint) {
            canvas.drawArc(rect(),
                    scale1d(_from, _minimum, _maximum, 270f, (270 + 360).toFloat()),
                    scale1d(_to - _from, _minimum, _maximum, 0f, 360f),
                    true, paint)
        }
    })

    var pieColor: Int
        get() = mPieDrawable.paint.color
        set(value) {
            mPieDrawable.paint.color = value
            invalidate()
        }

    var ovalColor: Int
        get() = mOvalDrawable.paint.color
        set(value) {
            mOvalDrawable.paint.color = value
            invalidate()
        }


    constructor(context: Context) : super(context) { init(context, null, 0) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs, 0) }
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { init(context, attrs, defStyle) }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {

        background = mOvalDrawable

        // Load attributes
        val a = context.theme.obtainStyledAttributes( attrs, R.styleable.mv_MyPie, defStyle, 0)
        try {
            _minimum = a.getFloat(R.styleable.mv_MyPie_mv_min, _minimum)
            _maximum = a.getFloat(R.styleable.mv_MyPie_mv_max, _maximum)
            _from = a.getFloat(R.styleable.mv_MyPie_mv_from, _from)
            _to = a.getFloat(R.styleable.mv_MyPie_mv_to, _to)
            pieColor = a.getColor(R.styleable.mv_MyPie_mv_pieColor, Color.BLACK)
            ovalColor = a.getColor(R.styleable.mv_MyPie_mv_ovalColor, Color.TRANSPARENT)
        } finally {
            a.recycle()
        }
        invalidateData()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft
        val top = paddingTop
        val right = width - left - paddingRight
        val bottom = height - top - paddingBottom
        mPieDrawable.setBounds(left, top, right, bottom)
        mOvalDrawable.setBounds(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPieDrawable.draw(canvas)
    }

    private fun invalidateData() {
        _maximum = _maximum.coerceAtLeast(_minimum)
        _from = _from.coerceIn(_minimum, _maximum)
        _to = _to.coerceIn(_from, _maximum)
    }

    override fun invalidate() {
        invalidateData()
        super.invalidate()

    }

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.mMinimum = _minimum
        ss.mMaximum = _maximum
        ss.mFrom = _from
        ss.mTo = _to
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        _minimum = state.mMinimum
        _maximum = state.mMaximum
        _from = state.mFrom
        _to = state.mTo
        invalidate()
    }

    internal class SavedState : View.BaseSavedState {
        var mMinimum: Float = 0f
        var mMaximum: Float = 0f
        var mFrom: Float = 0f
        var mTo: Float = 0f

        constructor(superState: Parcelable) : super(superState) {
        }

        private constructor(inp: Parcel) : super(inp) {
            mMinimum = inp.readFloat()
            mMaximum = inp.readFloat()
            mFrom = inp.readFloat()
            mTo = inp.readFloat()
        }

        override fun writeToParcel(outp: Parcel, flags: Int) {
            super.writeToParcel(outp, flags)
            outp.writeFloat(mMinimum)
            outp.writeFloat(mMaximum)
            outp.writeFloat(mFrom)
            outp.writeFloat(mTo)
        }

        companion object {
            //TODO SOMEDAY: we should save colors too
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(inp: Parcel): SavedState { return SavedState(inp) }
                override fun newArray(size: Int): Array<SavedState?> { return arrayOfNulls(size) }
            }
        }
    }

}
