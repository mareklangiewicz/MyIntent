package pl.mareklangiewicz.myintent

import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable
import pl.mareklangiewicz.myintent.PlayStopButton.State.*
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 15.12.15.
 */

class PlayStopButton(private val view: ImageView) {

    enum class State {
        HIDDEN, PLAY, STOP
    }

    val ANIM_DURATION = 300L

    private val log = MY_DEFAULT_ANDRO_LOGGER


    private val drawable = MyPlayStopDrawable().apply { colorFrom = 0xff0000c0.toInt(); colorTo = 0xffc00000.toInt(); rotateTo = 90f; strokeWidth = 6f }
    private val animator = ObjectAnimator.ofInt(drawable, "level", 0, 10000).setDuration(ANIM_DURATION)

    var listener: Listener? = null


    init {
        view.setImageDrawable(drawable)
        view.setOnClickListener(View.OnClickListener {
            val oldState = state
            if (oldState == HIDDEN) {
                log.v("Hidden button clicked. ignoring..")
                return@OnClickListener
            }
            state = if (oldState == STOP) PLAY else STOP
            listener?.onPlayStopClicked(oldState, state)
        })
        view.alpha = 0f
        animator.currentPlayTime = ANIM_DURATION
    }

    var state: State = HIDDEN
        set(newState) {
            if (newState == field)
                return
            val oldState = field
            field = newState

            if (newState == HIDDEN) {
                view.animate().alpha(0f)
            } else if (oldState == HIDDEN) {
                view.alpha = 0f
                animator.cancel()
                if (newState == PLAY) {
                    animator.reverse()
                    view.animate().alpha(1f)
                } else if (newState == STOP) {
                    animator.start()
                    view.animate().alpha(1f)
                }
            } else {
                // play -> stop or stop -> play
                if (animator.isRunning) {
                    animator.reverse()
                } else {
                    // we are stopped at some end
                    if (oldState == STOP && newState == PLAY) {
                        animator.reverse()
                    } else if (oldState == PLAY && newState == STOP) {
                        animator.start()
                    } else
                        log.a("Incorrect animated button state.")
                }
            }
        }

    interface Listener { // TODO SOMEDAY: use MyMachines.Relay to publish changes
        fun onPlayStopClicked(oldState: State, newState: State)
    }

}
