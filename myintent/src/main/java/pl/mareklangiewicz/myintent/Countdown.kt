package pl.mareklangiewicz.myintent

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import pl.mareklangiewicz.myutils.MyMathUtils

/**
 * Created by Marek Langiewicz on 16.12.15.
 */
class Countdown internal constructor(private val bar: ProgressBar) {

    private var boost = 0L

    private var command: String? = null // we use this also to track if countdown is running at the moment (null = it doesn't)

    var listener: Listener? = null

    private val animator: ObjectAnimator = ObjectAnimator.ofInt(bar, "progress", 0, 10000).apply {
        setDuration(3000)
        interpolator = LinearInterpolator()
    }


    init {

        bar.setOnTouchListener { view, event ->
            boost = MyMathUtils.scale0d(event.x, bar.width.toFloat(), animator.duration.toFloat()).toLong()
            animator.currentPlayTime = boost
            true
        }

        // The feature above (with OnTouchListener) is correct and cool - user can touch how much boost he wants;
        // but below we save simpler and more practical feature - user can click to switch boost between 0 and max.

        //        bar.setOnClickListener {
        //            boost = if (boost == 0L) animator.duration else 0L
        //            animator.currentPlayTime = boost
        //        }

        animator.addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator) {
                        val oldcmd = command
                        command = null
                        listener?.apply { onCountdownCancelled(oldcmd) }
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        val oldcmd = command
                        command = null
                        listener?.apply { oldcmd?.let { onCountdownFinished(it) } }
                        animator.currentPlayTime = boost
                    }

                    override fun onAnimationStart(animation: Animator) {
                        listener?.apply { onCountdownStarted(command) }
                    }
                }
        )
        cancel()
    }


    fun start(cmd: String) {
        cancel()
        command = cmd
        animator.currentPlayTime = boost
        animator.start()
    }

    fun cancel() {
        if (command !== null)
            animator.cancel() //IMPORTANT: it calls onAnimationCancelled followed by onAnimationEnd
    }

    val isRunning: Boolean
        get() = command != null

    interface Listener {
        fun onCountdownStarted(cmd: String?)
        fun onCountdownFinished(cmd: String?)
        fun onCountdownCancelled(cmd: String?)
    }

}

