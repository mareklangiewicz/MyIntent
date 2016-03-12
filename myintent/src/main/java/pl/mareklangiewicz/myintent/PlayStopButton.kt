package pl.mareklangiewicz.myintent

import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER

/**
 * Created by Marek Langiewicz on 15.12.15.
 */
class PlayStopButton(private val mView: ImageView) {

    private val log = MY_DEFAULT_ANDRO_LOGGER

    private var mState = HIDDEN

    @SuppressWarnings("FieldCanBeLocal")
    private val mDrawable = MyPlayStopDrawable().apply { colorFrom = 0xff0000c0.toInt(); colorTo = 0xffc00000.toInt(); rotateTo = 90f; strokeWidth = 6f }
    private val mAnimator: ObjectAnimator

    var listener: Listener? = null

    private val ANIM_DURATION = 300

    init {
        mView.setImageDrawable(mDrawable)
        mAnimator = ObjectAnimator.ofInt(mDrawable, "level", 0, 10000).setDuration(ANIM_DURATION.toLong())
        mView.setOnClickListener(View.OnClickListener {
            if (mState == HIDDEN) {
                log.v("Hidden button clicked. ignoring..")
                return@OnClickListener
            }
            setState(if (mState == STOP) PLAY else STOP, true, false)
        })
        setState(HIDDEN, false, true)
    }

    fun setState(state: Int) = setState(state, false, false)
    private fun setState(state: Int, byUser: Boolean, immediately: Boolean) {

        val oldState = mState
        mState = state

        if (immediately) {
            mAnimator.cancel()
            mView.animate().cancel()
            mView.alpha = if (mState == HIDDEN) 0f else 1f
            mAnimator.currentPlayTime = (if (mState == PLAY) 0 else ANIM_DURATION).toLong()
        } else if (oldState != mState) {
            if (mState == HIDDEN) {
                mView.animate().alpha(0f)
            } else if (oldState == HIDDEN) {
                mView.alpha = 0f
                mAnimator.cancel()
                if (mState == PLAY) {
                    mAnimator.reverse()
                    mView.animate().alpha(1f)
                } else if (mState == STOP) {
                    mAnimator.start()
                    mView.animate().alpha(1f)
                }
            } else {
                // play -> stop or stop -> play
                if (mAnimator.isRunning) {
                    mAnimator.reverse()
                } else {
                    // we are stopped at some end
                    if (oldState == STOP && mState == PLAY) {
                        mAnimator.reverse()
                    } else if (oldState == PLAY && mState == STOP) {
                        mAnimator.start()
                    } else
                        log.a("Incorrect animated button state.", null)
                }
            }

        }

        if (listener != null)
            listener!!.onPlayStopChanged(oldState, mState, byUser)
    }


    interface Listener {
        fun onPlayStopChanged(oldState: Int, newState: Int, byUser: Boolean)
    }

    companion object {
        // TODO LATER: change to enum
        const val HIDDEN = 0
        const val PLAY = 1
        const val STOP = 2
    }

}
