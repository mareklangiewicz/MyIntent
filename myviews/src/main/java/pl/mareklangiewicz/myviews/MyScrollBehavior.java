package pl.mareklangiewicz.myviews;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

import pl.mareklangiewicz.myloggers.MyAndroLogger;
import pl.mareklangiewicz.myloggers.MyAndroLoggerKt;
import pl.mareklangiewicz.myutils.MyMathUtils;

/**
 * Created by Marek Langiewicz on 22.03.16.
 */
public class MyScrollBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    protected @NonNull final MyAndroLogger log = MyAndroLoggerKt.getMY_DEFAULT_ANDRO_LOGGER();

    private int min = 0;
    private int max = 100;
    private int scroll_min = -200;
    private int scroll_max = 200;
    private @Nullable String mScrollProperty;
    private @Nullable ObjectAnimator mAnimator;

    public MyScrollBehavior(@Nullable String property) {
        mScrollProperty = property;
    }

    public MyScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.mv_MyScrollBehavior);
        min = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_min, min);
        max = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_max, max);
        scroll_min = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_scroll_min, scroll_min);
        scroll_max = a.getInt(R.styleable.mv_MyScrollBehavior_mv_behavior_scroll_max, scroll_max);
        mScrollProperty = a.getString(R.styleable.mv_MyScrollBehavior_mv_behavior_property);
        a.recycle();
    }

    @Override public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
                                                 V child, View directTargetChild, View target, int nestedScrollAxes) {
        if(mScrollProperty == null) {
            log.d("No property to scroll provided.");
            return false;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            log.d("This behavior requies at least lollipop mr1 device.");
            return false;
        }
        if(mAnimator == null) mAnimator = ObjectAnimator.ofInt(child, mScrollProperty, min, max);
        return true;
    }

    @SuppressLint("NewApi")
    @Override public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                                                                 V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if(mAnimator == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1)
            return;

        float progress = MyMathUtils.scale1d((float)dyConsumed, (float)scroll_min, (float)scroll_max, 0f, 1f);
        mAnimator.setCurrentFraction(progress);
    }
}
