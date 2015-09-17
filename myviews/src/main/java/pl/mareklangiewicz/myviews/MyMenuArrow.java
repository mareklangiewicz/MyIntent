package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class MyMenuArrow extends View {

    private float mDirection = 0;

    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;

    private int mContentWidth;
    private int mContentHeight;

    private int mCenterX;
    private int mCenterY;

    private int mHighY;
    private int mLowY;

    private int mLeftX;
    private int mRightX;

    private int mHalfX;
    private int mHalfY;

    private final Paint mPaint = new Paint();

    private final Path mPath = new Path();

    public MyMenuArrow(Context context) {
        super(context);
        init(null, 0);
    }

    public MyMenuArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyMenuArrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyMenuArrow, defStyle, 0);
        try {
            mDirection = a.getFloat(R.styleable.MyMenuArrow_arrDirection, mDirection);
            setArrColor(a.getColor(R.styleable.MyMenuArrow_arrColor, Color.BLACK));
        }
        finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();

        mContentWidth = w - mPaddingLeft - mPaddingRight;
        mContentHeight = h - mPaddingTop - mPaddingBottom;

        mCenterX = mPaddingLeft + mContentWidth / 2;
        mCenterY = mPaddingTop + mContentHeight / 2;

        mHighY = mPaddingTop + mContentHeight / 4;
        mLowY = mPaddingTop + mContentHeight * 3 / 4;

        mHalfY = (mLowY - mHighY) / 2;

        mLeftX = mPaddingLeft + mContentWidth / 4;
        mRightX = mPaddingLeft + mContentWidth * 3 / 4;

        mHalfX = (mRightX - mLeftX) / 2;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(h / 10);
        mPaint.setStrokeJoin(Paint.Join.MITER);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        mPath.rewind();

        float x1 = mLeftX;
        float x2 = mRightX;
        float y1 = mCenterY;
        float y2 = mCenterY;

        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y2);

        x1 = mLeftX  + mDirection * mHalfX;
        x2 = mRightX;
        y1 = mHighY;
        y2 = mHighY + mDirection * mHalfY;

        mPath.moveTo(x1, y1);
        mPath.lineTo(x2, y2);

        y1 = mLowY;
        y2 = mLowY - mDirection * mHalfY;

        if(Math.abs(y2 - mCenterY) > 1)
            mPath.moveTo(x2, y2);
        else
            mPath.lineTo(x2, y2);

        mPath.lineTo(x1, y1);

        canvas.drawPath(mPath, mPaint);
    }

    public @FloatRange(from=0.0,to=1.0) float getDirection() {
        return mDirection;
    }

    public void setDirection(@FloatRange(from=0.0,to=1.0) float direction) {
        mDirection = direction;
        invalidate();
    }

    @ColorInt public int getArrColor() { return mPaint.getColor(); }

    public void setArrColor(@ColorInt int color) {mPaint.setColor(color); invalidate(); }



    static final class SavedState extends BaseSavedState {

        float mDirection;

        //TODO LATER: we should save color too

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mDirection = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(mDirection);
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
        ss.mDirection = getDirection();
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

        setDirection(ss.mDirection);
    }




}
