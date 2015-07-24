package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import pl.mareklangiewicz.myutils.MyMath;

public class MyPie extends View {

    private float mMinimum = 0;
    private float mMaximum = 100;
    private float mFrom = 0;
    private float mTo = 75;
    private final ShapeDrawable mPieDrawable = new ShapeDrawable(new OvalShape() {
        @Override
        public void draw(Canvas canvas, Paint paint) {
            canvas.drawArc(rect(),
                    MyMath.scale1d(mFrom, mMinimum, mMaximum, 270, 270 + 360),
                    MyMath.scale1d(mTo - mFrom, mMinimum, mMaximum, 0, 360),
                    true, paint
            );
        }
    });
    private final ShapeDrawable mOvalDrawable = new ShapeDrawable(new OvalShape());

    public MyPie(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MyPie(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MyPie(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        setBackground(mOvalDrawable);

        // Load attributes
        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.MyPie, defStyle, 0);

        try {
            mMinimum = a.getFloat(R.styleable.MyPie_min, mMinimum);
            mMaximum = a.getFloat(R.styleable.MyPie_max, mMaximum);
            mFrom = a.getFloat(R.styleable.MyPie_from, mFrom);
            mTo = a.getFloat(R.styleable.MyPie_to, mTo);
            setPieColor(a.getColor(R.styleable.MyPie_pieColor, Color.BLACK));
            setOvalColor(a.getColor(R.styleable.MyPie_ovalColor, Color.TRANSPARENT));
        }
        finally {
            a.recycle();
        }

        invalidateData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getWidth() - left - getPaddingRight();
        int bottom = getHeight() - top - getPaddingBottom();
        mPieDrawable.setBounds(left, top, right, bottom);
        mOvalDrawable.setBounds(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPieDrawable.draw(canvas);
    }

    private void invalidateData() {
        if(mMaximum < mMinimum) mMaximum = mMinimum;
        if(mFrom < mMinimum) mFrom = mMinimum;
        if(mFrom > mMaximum) mFrom = mMaximum;
        if(mTo < mFrom) mTo = mFrom;
        if(mTo > mMaximum) mTo = mMaximum;
    }

    @Override
    public void invalidate() {
        invalidateData();
        super.invalidate();

    }

    public int getPieColor() { return mPieDrawable.getPaint().getColor(); }
    public int getOvalColor() { return mOvalDrawable.getPaint().getColor(); }
    public float getMinimum() { return mMinimum; }
    public float getMaximum() { return mMaximum; }
    public float getFrom() { return mFrom; }
    public float getTo() { return mTo; }

    public void setPieColor(int color) { mPieDrawable.getPaint().setColor(color); invalidate(); }
    public void setOvalColor(int color) { mOvalDrawable.getPaint().setColor(color); invalidate(); }

    public void setMinimum(float minimum) { mMinimum = minimum; invalidate(); }
    public void setMaximum(float maximum) { mMaximum = maximum; invalidate(); }
    public void setFrom(float from) { mFrom = from; invalidate(); }
    public void setTo(float to) { mTo = to; invalidate(); }


    static class SavedState extends BaseSavedState {

        float mMinimum;
        float mMaximum;
        float mFrom;
        float mTo;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mMinimum = in.readFloat();
            mMaximum = in.readFloat();
            mFrom = in.readFloat();
            mTo = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(mMinimum);
            out.writeFloat(mMaximum);
            out.writeFloat(mFrom);
            out.writeFloat(mTo);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mMinimum = getMinimum();
        ss.mMaximum = getMaximum();
        ss.mFrom = getFrom();
        ss.mTo = getTo();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        setMinimum(ss.mMinimum);
        setMaximum(ss.mMaximum);
        setFrom(ss.mFrom);
        setTo(ss.mTo);
    }


}
