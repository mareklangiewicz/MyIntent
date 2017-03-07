package pl.mareklangiewicz.myfragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mf_pue_tests_fragment.*
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter
import pl.mareklangiewicz.myloggers.MyAndroSystemLogger
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 05.03.16.
 */
open class PueTestsFragment : MyFragment() {


    val adapter = MyAndroLogAdapter(log.history)

    private val todo = Lst<(Unit) -> Unit>()

    private val syslog = MyAndroSystemLogger() // this logger can be used from any thread.

    private val uischeduler = HandlerScheduler()
    private val exscheduler = ExecutorScheduler().logex(syslog)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_pue_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val ctl = log.history.changes { adapter.notifyDataSetChanged() }
        todo.add { ctl(Cancel) }
        adapter.notifyDataSetChanged()

        mf_mmt_rv_log.adapter = adapter

        mf_mmt_b_test1.setOnClickListener {

            log.i("Test 1 - logging faster and faster")

            val intervals = (1..20).asNPullee()
                    .vnmap { 2000L - it * 100L }

            val timer = Timer(uischeduler, intervals)
                    .lapeek { log.i("item: $it") }
                    .lafilter { it !== null && it % 5L == 0L }
                    .lapeek { log.w("$it % 5 = 0") }

            val actl = timer { } // we subscribe here with empty function (side effects are attached already)
            // and we get a controller that accepts ICommands

            todo.add { actl(Cancel) }

            actl(Start)

        }

        mf_mmt_b_test2.setOnClickListener {

            log.i("Test 2 - moving pies")

            val intervals = (1..20).asNPullee().vnmap { 500L }

            val timer = Timer(uischeduler, intervals)
                    .lanmap { it * 5f + 5f }
                    .lanpeek { animTo(mf_mmt_mp1, "to", it) }
                    .lafilter { it !== null && it % 10f == 0f }
                    .lamap { it as Float / 2f } // as Float tells compiler that we already filtered nulls out.
                    .lapeek { animTo(mf_mmt_mp2, "to", it) }
                    .lapeek { animTo(mf_mmt_mp3, "to", RANDOM.nextFloat(50f, 99f)) }
                    .lapeek { animTo(mf_mmt_mp3, "from", RANDOM.nextFloat(1f, mf_mmt_mp3.to)) }

            val actl = timer { } // we subscribe here with empty function (side effects are attached already) and we get a controller that accepts ICommands

            todo.add { actl(Cancel) }

            actl(Start)

        }

        mf_mmt_b_test3.setOnClickListener {

            log.i("Test 3: Relay")

            val relay = Relay<Float>()

            val subscriptions = arrayOf<(Cancel) -> Unit>( {}, {}, {} )

            val intervals = (1..70).asNPullee().vnmap { 300L }

            val timer = Timer(uischeduler, intervals)
                    .lapeek {
                        when(it) {
                            10L -> subscriptions[0] = relay { animTo(mf_mmt_mp1, "to", it) }
                            20L -> subscriptions[1] = relay { animTo(mf_mmt_mp2, "to", it) }
                            30L -> subscriptions[2] = relay { animTo(mf_mmt_mp3, "to", it) }
                            40L -> subscriptions[0](Cancel) // unsubscribe
                            50L -> subscriptions[1](Cancel) // unsubscribe
                            60L -> subscriptions[2](Cancel) // unsubscribe
                        }
                    }
                    .lanmap { it.toFloat().scale1d(0f, 70f, 1f, 99f) }

            val actl = timer { if(it !== null) relay.push(it) }

            todo.add { actl(Cancel) }

            actl(Start)

        }

        mf_mmt_b_test4.setOnClickListener {
            log.i("Test 4")
            log.i("EXPERIMENT: set speed using red pie..") // TODO LATER: Clarify

            animTo(mf_mmt_mp3, "from", 0f)
            animTo(mf_mmt_mp3, "to", 80f)

            mf_mmt_mp3.setOnClickListener {
                val interval = RANDOM.nextFloat(10f, 90f)
                log.w("new (random) interval: ${(interval*10).toInt()}ms")
                animTo(mf_mmt_mp3, "to", interval)
            }

            todo.add { mf_mmt_mp3.setOnClickListener(null) }

            val intervals = { u: Unit -> mf_mmt_mp3.to.toLong() * 10 }
                    .vntake(64)
                    .vpeek { if(it === null) syslog.w("All intervals pulled.") }

            val timer = Timer(exscheduler, intervals)
                    .lanmap { it.toFloat() * 3 }
                    .lapeek { syslog.i("before reschedule: thread: ${Thread.currentThread()}")}
                    .lreschedule(uischeduler)
                    .lapeek { syslog.i("after  reschedule: thread: ${Thread.currentThread()}")}

            // we will try to subscribe to our timer twice!
            val ctl1 = timer {
                if(it !== null) animTo(mf_mmt_mp1, "to", it)
                else {
                    syslog.i("SingleTimer 1 finished.")
                    mf_mmt_mp3.setOnClickListener(null)
                }
            }
            val ctl2 = timer {
                if(it !== null) animTo(mf_mmt_mp2, "to", it)
                else {
                    syslog.i("SingleTimer 2 finished.")
                }
            }

            todo.add { ctl1(Cancel) }
            todo.add { ctl2(Cancel) }

            ctl1(Start)
            ctl2(Start)

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
        todo.forEach { it(Unit) }
        todo.clr()
        mf_mmt_rv_log.adapter = null
        super.onDestroyView()
    }
}