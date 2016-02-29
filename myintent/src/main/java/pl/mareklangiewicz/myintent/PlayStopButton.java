package pl.mareklangiewicz.myintent;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.noveogroup.android.log.MyAndroidLogger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable;
import pl.mareklangiewicz.myutils.IMyLogger;

/**
 * Created by Marek Langiewicz on 15.12.15.
 */
public final class PlayStopButton {

    private @NonNull final IMyLogger log = MyAndroidLogger.UIL;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            HIDDEN,
            PLAY,
            STOP
    })
    public @interface State {
    }

    public static final int HIDDEN = 0;
    public static final int PLAY = 1;
    public static final int STOP = 2;


    private @State int mState = HIDDEN;

    @SuppressWarnings("FieldCanBeLocal")
    private final Drawable mDrawable = new MyPlayStopDrawable()
            .setColorFrom(0xff0000c0)
            .setColorTo(0xffc00000)
            .setRotateTo(90f)
            .setStrokeWidth(6);

    private @NonNull ImageView mView;
    private @NonNull ObjectAnimator mAnimator;

    private @Nullable Listener mListener;

    private final int ANIM_DURATION = 300;

    PlayStopButton(@NonNull ImageView view) {
        mView = view;
        mView.setImageDrawable(mDrawable);
        mAnimator = ObjectAnimator.ofInt(mDrawable, "level", 0, 10000).setDuration(ANIM_DURATION);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(mState == HIDDEN) {
                    log.v("Hidden button clicked. ignoring..", null);
                    return;
                }
                setState(mState == STOP ? PLAY : STOP, true, false);
            }
        });
        setState(HIDDEN, false, true);
    }


    @SuppressWarnings("unused")
    public @State int getState() { return mState; }

    public void setState(@State int state) {
        setState(state, false, false);
    }

    private void setState(@State int state, boolean byUser, boolean immediately) {

        @State int oldState = mState;
        mState = state;

        if(immediately) {
            mAnimator.cancel();
            mView.animate().cancel();
            mView.setAlpha(mState == HIDDEN ? 0f : 1f);
            mAnimator.setCurrentPlayTime(mState == PLAY ? 0 : ANIM_DURATION);
        }
        else if(oldState != mState) {
            if(mState == HIDDEN) {
                mView.animate().alpha(0f);
            }
            else if(oldState == HIDDEN) {
                mView.setAlpha(0f);
                mAnimator.cancel();
                if(mState == PLAY) {
                    mAnimator.reverse();
                    mView.animate().alpha(1f);
                }
                else if(mState == STOP) {
                    mAnimator.start();
                    mView.animate().alpha(1f);
                }
            }
            else { // play -> stop or stop -> play
                if(mAnimator.isRunning()) {
                    mAnimator.reverse();
                }
                else { // we are stopped at some end
                    if(oldState == STOP && mState == PLAY) {
                        mAnimator.reverse();
                    }
                    else if(oldState == PLAY && mState == STOP) {
                        mAnimator.start();
                    }
                    else
                        log.a("Incorrect animated button state.", null);
                }
            }

        }

        if(mListener != null)
            mListener.onPlayStopChanged(oldState, mState, byUser);
    }

    @SuppressWarnings("unused")
    public @Nullable Listener getListener() {
        return mListener;
    }

    public void setListener(@Nullable Listener listener) {
        mListener = listener;
    }


    public interface Listener {
        void onPlayStopChanged(@State int oldState, @State int newState, boolean byUser);
    }

}
