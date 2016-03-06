package pl.mareklangiewicz.myfragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noveogroup.android.log.MyAndroidLogger
import kotlinx.android.synthetic.main.mf_my_machines_tests_fragment.*
import pl.mareklangiewicz.myloggers.MyLogAdapter
import pl.mareklangiewicz.myutils.*

/**
 * Created by Marek Langiewicz on 05.03.16.
 */
class MyMachinesTestsFragment : MyFragment() {

    val adapter = MyLogAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        return inflater.inflate(R.layout.mf_my_machines_tests_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        adapter.setLog(MyAndroidLogger.UIL)

        mf_mmt_rv_log.adapter = adapter

        mf_mmt_b_test1.setOnClickListener {

            log.i("Test 1 - logging faster and faster")

            val intervals = (1..20).asEPullee()
                    .vemap { 2000L - it * 100L }

            val timer = Timer(Handler(), intervals)
                    .leff { log.i("item: $it") }
                    .lfilter { it % 5L == 0L }
                    .leff { log.w("$it % 5 = 0") }

            timer { } (Start)

        }

        mf_mmt_b_test2.setOnClickListener {

            log.i("Test 2 - moving pies")

            val intervals = (1..20).asEPullee().vemap { 500L }

            val timer = Timer(Handler(), intervals)
                    .lmap { it * 5f + 5f }
                    .leff { ObjectAnimator.ofFloat(mf_mmt_mp1, "to", it).start() }
                    .lfilter { it % 10f == 0f }
                    .lmap { it / 2f }
                    .leff { ObjectAnimator.ofFloat(mf_mmt_mp2, "to", it).start() }
                    .leff { ObjectAnimator.ofFloat(mf_mmt_mp3, "to", MyMathUtils.getRandomFloat(50f, 99f)).start() }
                    .leff { ObjectAnimator.ofFloat(mf_mmt_mp3, "from", MyMathUtils.getRandomFloat(1f, mf_mmt_mp3.to)).start() }

            timer { } (Start)

        }

        mf_mmt_b_test3.setOnClickListener {
            log.i("Test 3")
            log.i("TODO")

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

    override fun onDestroyView() {
        adapter.setLog(null)
        super.onDestroyView()
    }
}