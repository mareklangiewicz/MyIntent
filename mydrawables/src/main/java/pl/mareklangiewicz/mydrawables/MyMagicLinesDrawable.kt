package pl.mareklangiewicz.mydrawables

import android.graphics.Path
import android.graphics.Rect
import android.support.annotation.IntRange

import java.util.Random


/**
 * Created by Marek Langiewicz on 16.09.15.
 * Generates cool effect of animated lines (random)
 * Check MyBlocks app for demo (look at left drawer header).
 */
class MyMagicLinesDrawable : MyLivingDrawable() {

    private val RANDOM = Random()
    private var mLines: IntArray? = null

    fun setLines(vararg lines: Int): MyMagicLinesDrawable {
        mLines = lines
        return this
    }

    fun setRandomLines(count: Int, seed: Long): MyMagicLinesDrawable {
        RANDOM.setSeed(seed)
        setRandomLines(count)
        return this
    }

    fun setRandomLines(count: Int): MyMagicLinesDrawable {
        val lines = IntArray(count * 2)
        for (i in 0..count - 1) {
            lines[i * 2] = RANDOM.nextInt(
                    1 + RANDOM.nextInt(10000))
            lines[i * 2 + 1] = 10000 - RANDOM.nextInt(
                    1 + RANDOM.nextInt(
                            10000 - lines[i * 2]))
        }
        mLines = lines
        return this
    }

    fun setRandomLines(): MyMagicLinesDrawable {
        setRandomLines(3)
        return this
    }


    override fun drawLivingPath(path: Path, @IntRange(from = 0, to = 10000) level: Int, bounds: Rect, cx: Int, cy: Int) {

        if (mLines == null)
            setRandomLines()

        val count = mLines!!.size / 2
        for (i in 0..count - 1) {
            val from = mLines!![i * 2]
            val to = mLines!![i * 2 + 1]
            val height = (i + 1) * bounds.height() / (count + 1)
            ln(bounds.left, height, lvl(from, to, bounds.left, bounds.right), height)
        }
    }
}
