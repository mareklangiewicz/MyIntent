package pl.mareklangiewicz.myfragments


import android.animation.ObjectAnimator
import android.os.Bundle



import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.mf_my_dawable_tests_fragment.grid_view
import kotlinx.android.synthetic.main.mf_my_dawable_tests_fragment.seek_bar_level
import kotlinx.android.synthetic.main.mf_my_dawable_tests_fragment.seek_bar_stroke_width
import pl.mareklangiewicz.mydrawables.MyArrowDrawable
import pl.mareklangiewicz.mydrawables.MyCheckDrawable
import pl.mareklangiewicz.mydrawables.MyLessDrawable
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable
import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable
import pl.mareklangiewicz.mydrawables.MyPlusDrawable
import pl.mareklangiewicz.myutils.e
import pl.mareklangiewicz.pue.e
import pl.mareklangiewicz.myutils.i
import pl.mareklangiewicz.myutils.w

class MyDrawableTestsFragment : MyFragment(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private val drawables = arrayOf(
            MyPlusDrawable().apply { color = 0xff00a000.toInt(); rotateTo = 180f },
            MyPlusDrawable().apply { colorFrom = 0xffc00000.toInt(); colorTo = 0xff0000c0.toInt(); rotateTo = 90f },
            MyPlusDrawable().apply { color = 0xff00a000.toInt(); rotateTo = -360f },
            MyArrowDrawable().apply { color = 0xff008080.toInt(); rotateFrom = -180f },
            MyArrowDrawable().apply { color = 0xff008000.toInt(); rotateFrom = 180f },
            MyArrowDrawable().apply { color = 0xff808000.toInt(); rotateFrom = 360f },
            MyPlayStopDrawable().apply { colorFrom = 0xff0000c0.toInt(); colorTo = 0xffc00000.toInt(); rotateTo = 180f },
            MyPlayStopDrawable().apply { colorFrom = 0xff0000c0.toInt(); colorTo = 0xffc00000.toInt(); rotateTo = 90f },
            MyCheckDrawable().apply { colorFrom = 0xff00f000.toInt(); colorTo = 0xfff00000.toInt(); rotateTo = 90f },
            MyCheckDrawable().apply { colorFrom = 0xff00a000.toInt(); colorTo = 0xffa00000.toInt(); rotateTo = 180f },
            MyCheckDrawable().apply { colorFrom = 0xff00c000.toInt(); colorTo = 0xffc00000.toInt(); rotateTo = -180f },
            MyCheckDrawable().apply { colorFrom = 0xff0000f0.toInt(); colorTo = 0xfff00000.toInt(); rotateTo = 360f },
            MyLessDrawable().apply { color = 0xffa000a0.toInt(); rotateFrom = -180f },
            MyLessDrawable().apply { color = 0xff8000a0.toInt(); rotateTo = 180f },
            MyLessDrawable().apply { color = 0xff20a050.toInt(); rotateTo = 360f },
            MyMagicLinesDrawable().apply { setLines(0, 3000, 3000, 6000, 6000, 9000, 9000, 10000, 0, 10000); color = 0x400000a0 },
            MyMagicLinesDrawable().apply { setRandomLines(2); color = 0x400000a0 },
            MyMagicLinesDrawable().apply { setRandomLines(5); color = 0x400000a0 },
            MyMagicLinesDrawable().apply { setRandomLines(10); color = 0x400000a0 },
            MyMagicLinesDrawable().apply { setRandomLines(8); colorFrom = 0x000000a0; colorTo = 0x600000a0 },
            MyMagicLinesDrawable().apply { color = 0x400000a0 },
            MyMagicLinesDrawable().apply { color = 0x400000a0 }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_dawable_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seek_bar_level.setOnSeekBarChangeListener(this)
        seek_bar_stroke_width.setOnSeekBarChangeListener(this)

        seek_bar_stroke_width.progress = 12

        grid_view.layoutManager = GridLayoutManager(activity, 4)
        grid_view.adapter = MyAdapter()

    }

    override fun onResume() {
        super.onResume()
        val d = MyCheckDrawable().apply { strokeWidth = 6f; colorFrom = 0xff0000f0.toInt(); colorTo = 0xff00f000.toInt(); rotateTo = 360f }
        manager?.fab?.setImageDrawable(d)
        manager?.fab?.setOnClickListener {
            ObjectAnimator.ofInt(d, "level", 0, 10000, 10000, 0).setDuration(7000).start()
            log.w("[SNACK]FAB Clicked!")
        }
        manager?.fab?.show()
    }

    override fun onPause() {
        manager?.fab?.setOnClickListener(null)
        manager?.fab?.hide()
        super.onPause()
    }

    override fun onDestroyView() {
        grid_view.adapter = null
        super.onDestroyView()
    }

    override fun onClick(v: View) {
        val tag = v.getTag(R.id.mf_tag_animator)
        if (tag is ObjectAnimator)
            tag.start()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar === seek_bar_level) {
            for (drawable in drawables)
                drawable.level = progress
        } else if (seekBar === seek_bar_stroke_width) {
            for (drawable in drawables)
                drawable.strokeWidth = progress.toFloat()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (seekBar === seek_bar_level)
            log.i(String.format("level = %d", seekBar.progress))
        else if (seekBar === seek_bar_stroke_width)
            log.i(String.format("stroke width = %d", seekBar.progress))
        else
            log.e("Unknown seek bar.")
    }

    private class MyViewHolder(v: View, val content: View) : RecyclerView.ViewHolder(v)

    private inner class MyAdapter() : RecyclerView.Adapter<MyViewHolder>() {
        init {
            setHasStableIds(true)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val content = View(activity)
            val contentparams = FrameLayout.LayoutParams(200, 200)
            contentparams.gravity = Gravity.CENTER
            content.layoutParams = contentparams
            val card = CardView(activity!!)
            val cardparams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            cardparams.setMargins(8, 8, 8, 8)
            card.layoutParams = cardparams
            card.addView(content)
            return MyViewHolder(card, content)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val content = holder.content
            val drawable = drawables[position]
            val animator = ObjectAnimator.ofInt(drawable, "level", 0, 10000, 10000, 0)
            animator.duration = 3000
            animator.interpolator = LinearInterpolator()
            content.background = drawable
            content.setTag(R.id.mf_tag_animator, animator)
            content.setOnClickListener(this@MyDrawableTestsFragment)
        }

        override fun getItemCount(): Int {
            return drawables.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong() // our array is constant.
        }
    }
}
