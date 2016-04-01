package pl.mareklangiewicz.myfragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mf_my_machines_tests_fragment.*
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 05.03.16.
 */
class MyMachinesTestsFragment : MyFragment() {

    val adapter = MyAndroLogAdapter(log.history)

    private val todo = ToDo()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_machines_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val unsub = log.history.changes { adapter.notifyDataSetChanged() }
        todo(unsub)
        adapter.notifyDataSetChanged()

        mf_mmt_rv_log.adapter = adapter

        mf_mmt_b_test1.setOnClickListener {

            log.i("Test 1 - logging faster and faster")

            val intervals = (1..20).asEPullee()
                    .vemap { 2000L - it * 100L }

            val timer = Timer(Handler(), intervals)
                    .lpeek { log.i("item: $it") }
                    .lfilter { it % 5L == 0L }
                    .lpeek { log.w("$it % 5 = 0") }

            timer { } (Start)

        }

        mf_mmt_b_test2.setOnClickListener {

            log.i("Test 2 - moving pies")

            val intervals = (1..20).asEPullee().vemap { 500L }

            val timer = Timer(Handler(), intervals)
                    .lmap { it * 5f + 5f }
                    .lpeek { animTo(mf_mmt_mp1, "to", it) }
                    .lfilter { it % 10f == 0f }
                    .lmap { it / 2f }
                    .lpeek { animTo(mf_mmt_mp2, "to", it) }
                    .lpeek { animTo(mf_mmt_mp3, "to", getRandomFloat(50f, 99f)) }
                    .lpeek { animTo(mf_mmt_mp3, "from", getRandomFloat(1f, mf_mmt_mp3.to)) }

            timer { } (Start)

        }

        mf_mmt_b_test3.setOnClickListener {

            log.i("Test 3: Relay")

            val relay = Relay<Float>()

            val subscriptions = arrayOf<(Unit) -> Unit>( {}, {}, {} )

            val intervals = (1..70).asEPullee().vemap { 300L }

            val timer = Timer(Handler(), intervals)
                    .lpeek {
                        when(it) {
                            10L -> subscriptions[0] = relay { animTo(mf_mmt_mp1, "to", it) }
                            20L -> subscriptions[1] = relay { animTo(mf_mmt_mp2, "to", it) }
                            30L -> subscriptions[2] = relay { animTo(mf_mmt_mp3, "to", it) }
                            40L -> subscriptions[0](Unit) // unsubscribe
                            50L -> subscriptions[1](Unit) // unsubscribe
                            60L -> subscriptions[2](Unit) // unsubscribe
                        }
                    }
                    .lmap { scale1d(it.toFloat(), 0f, 70f, 1f, 99f) }

            timer(relay.pushee)(Start)


        }

        mf_mmt_b_test4.setOnClickListener {
            log.i("Test 4")
            log.i("TODO")

        }

        mf_mmt_b_test5.setOnClickListener {
            log.i("Test 5")
            log.i("TODO")

        }
    }

    fun animTo(obj: Any, property: String, goal: Float) {
        ObjectAnimator.ofFloat(obj, property, goal).start()
    }

    fun animTo(obj: Any, property: String, goal: Int) {
        ObjectAnimator.ofInt(obj, property, goal).start()
    }
    override fun onDestroyView() {
        todo.doItAll()
        super.onDestroyView()
    }
}