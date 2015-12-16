package pl.mareklangiewicz.myintent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

/**
 * Created by Marek Langiewicz on 16.12.15.
 */
public class Countdown {

    @SuppressWarnings("FieldCanBeLocal")
    private @NonNull ProgressBar mProgressBar;

    private @NonNull ObjectAnimator mAnimator;
    static private long mBoost = 0;

    @Nullable Listener mListener;

    private @Nullable String mCommand = null; // we use this also to track if countdown is running at the moment (null = it doesn't)

    Countdown(@NonNull ProgressBar aProgressBar) {

        mProgressBar = aProgressBar;

        //noinspection ConstantConditions
        mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                // mBoost = (int)scale0d(event.getX(), mProgressBar.getWidth(), mAnimator.getDuration());
                // the above is nice and correct, but more practical is to just set boost to the end or to 0
                mBoost = mBoost == 0 ? mAnimator.getDuration() : 0;
                mAnimator.setCurrentPlayTime(mBoost);
                return false;
            }
        });

        mAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 10000).setDuration(3000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                if(mListener != null)
                    //noinspection ConstantConditions
                    mListener.onCountdownCancelled(mCommand);
                mCommand = null;
            }

            @Override public void onAnimationEnd(Animator animation) {
                if(mListener != null && mCommand != null)
                    mListener.onCountdownFinished(mCommand);
                mCommand = null;
                mAnimator.setCurrentPlayTime(mBoost);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if(mListener != null)
                    //noinspection ConstantConditions
                    mListener.onCountdownStarted(mCommand);
            }
        });

        cancel();
    }


    public void start(@NonNull String cmd) {
        cancel();
        mCommand = cmd;
        mAnimator.setCurrentPlayTime(mBoost);
        mAnimator.start();
    }

    public void cancel() {
        if(mCommand != null)
            mAnimator.cancel(); //IMPORTANT: it calls onAnimationCancelled followed by onAnimationEnd
    }

    @SuppressWarnings("unused")
    public @Nullable Listener getListener() {
        return mListener;
    }

    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }

    public boolean isRunning() {
        return mCommand != null;
    }

    public interface Listener {
        void onCountdownStarted(@NonNull String cmd);
        void onCountdownFinished(@NonNull String cmd);
        void onCountdownCancelled(@NonNull String cmd);
    }

}
