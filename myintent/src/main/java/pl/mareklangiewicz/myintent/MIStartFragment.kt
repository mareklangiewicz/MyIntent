package pl.mareklangiewicz.myintent

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import kotlinx.android.synthetic.main.mi_log_fragment.*
import pl.mareklangiewicz.myactivities.MyActivity
import pl.mareklangiewicz.myfragments.MyFragment
import pl.mareklangiewicz.myintent.PlayStopButton.State.*
import pl.mareklangiewicz.myutils.*

class MIStartFragment : MyFragment(), PlayStopButton.Listener, Countdown.Listener {

    var mSearchItem: MenuItem? = null

    private val adapter = MyMDAndroLogAdapter(log.history)

    private val todo = ToDo()

    lateinit var mPSButton: PlayStopButton
    lateinit var mCountdown: Countdown

    var onResumePlayCmd = ""


    private val updateButtonsRunnable = Runnable {
        updateFAB()
        updatePS()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) //just for logging
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.mi_log_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        manager?.name = BuildConfig.NAME_PREFIX + getString(R.string.mi_start)

        mCountdown = Countdown(mi_lf_pb_countdown)
        mCountdown.listener = this

        mPSButton = PlayStopButton(mi_lf_iv_play_stop)
        mPSButton.listener = this

        mi_lf_rv_log.adapter = adapter

        val ctl1 = log.history.changes { adapter.notifyDataSetChanged() }
        todo.push { ctl1(Cancel) }
        adapter.notifyDataSetChanged() // to make sure we are up to date

        //TODO SOMEDAY: some nice simple header with fragment title
        manager?.lnav?.menuId = R.menu.mi_log_local
        updateCheckedItem()

        manager?.fab?.setImageResource(R.drawable.mi_ic_mic_white_24dp)

        manager?.fab?.setOnClickListener {
            if(isViewAvailable) {
                mCountdown.cancel()
                mi_lf_et_command.setText("")
                (activity as MyActivity).execute("start custom action listen")
            }
        }

        mi_lf_et_command.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable) { updatePS() }
        })

        arguments?.getString("play")?.let { onResumePlayCmd = it }

        val ctl2 = manager!!.lnav!!.items {
            when (it) {
                R.id.mi_ll_i_error        -> log.history.level = MyLogLevel.ERROR
                R.id.mi_ll_i_warning      -> log.history.level = MyLogLevel.WARN
                R.id.mi_ll_i_info         -> log.history.level = MyLogLevel.INFO
                R.id.mi_ll_i_debug        -> log.history.level = MyLogLevel.DEBUG
                R.id.mi_ll_i_verbose      -> log.history.level = MyLogLevel.VERBOSE
                R.id.mi_clear_log_history    -> log.history.clear()
            }
        }
        todo.push { ctl2(Cancel) }
    }

    override fun onResume() {
        super.onResume()
        if(onResumePlayCmd.isNotEmpty())
            play(onResumePlayCmd)
        onResumePlayCmd = ""
        lazyUpdateButtons()
    }

    override fun onStop() {
        mCountdown.cancel()
        super.onStop()
    }

    override fun onDestroyView() {

        view?.removeCallbacks(updateButtonsRunnable)

        mSearchItem = null

        mPSButton.listener = null
        mPSButton.state = HIDDEN

        manager?.fab?.setOnClickListener(null)
        manager?.fab?.hide()

        manager?.lnav?.menuId = -1

        mCountdown.cancel()
        mCountdown.listener = null

        todo.doItAll()

        mi_lf_rv_log.adapter = null

        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mi_log_options, menu)
        mSearchItem = menu.findItem(R.id.action_search)
        val sview = mSearchItem?.actionView as SearchView
        val manager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        sview.setSearchableInfo(manager.getSearchableInfo(activity.componentName))
        sview.setIconifiedByDefault(true)
    }

    private fun updateCheckedItem() {
        val id = when (log.history.level) {
            MyLogLevel.ERROR, MyLogLevel.ASSERT -> R.id.mi_ll_i_error
            MyLogLevel.WARN -> R.id.mi_ll_i_warning
            MyLogLevel.INFO -> R.id.mi_ll_i_info
            MyLogLevel.DEBUG -> R.id.mi_ll_i_debug
            MyLogLevel.VERBOSE -> R.id.mi_ll_i_verbose
        }
        manager?.lnav?.setCheckedItem(id, false)
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        if (slideOffset == 0f) {
            lazyUpdateButtons()
        } else {
            manager?.fab?.hide()
            mPSButton.state = HIDDEN
        }
    }

    override fun onDrawerClosed(drawerView: View) {
        lazyUpdateButtons()
    }

    private val isSomethingOnOurFragment: Boolean
        get() = view !== null && ( manager?.lnav?.overlaps(view) ?: false || manager?.gnav?.overlaps(view) ?: false )

    private fun updateFAB() {
        if (isSomethingOnOurFragment)
            manager?.fab?.hide()
        else
            manager?.fab?.show()
    }


    private fun lazyUpdateButtons() {
        if (!isViewAvailable) {
            log.d("View is not available.")
            return
        }
        view?.removeCallbacks(updateButtonsRunnable)
        view?.postDelayed(updateButtonsRunnable, 300)
    }

    private fun updatePS() {
        if (!isViewAvailable) {
            log.v("UI not ready.")
            return
        }
        if (isSomethingOnOurFragment)
            mPSButton.state = HIDDEN
        else
            mPSButton.state = if (mCountdown.isRunning) STOP else PLAY
        if(mCountdown.isRunning)
            mi_lf_et_command.isFocusable = false // it also sets isFocusableInTouchMode property to false
        else
            mi_lf_et_command.isFocusableInTouchMode = true // it also sets isFocusable property to true
    }

    /**
     * Starts counting to start given command.
     * It will start the command if user doesn't press stop fast enough.
     * If no command is given it will try to get command from EditText
     */
    fun play(cmd: String = "") {

        if(!isViewAvailable) {
            onResumePlayCmd = cmd // if it is empty - nothing is scheduled.
            return
        }

        if(!cmd.isEmpty()) mi_lf_et_command.setText(cmd)

        val acmd = if(!cmd.isEmpty()) cmd else mi_lf_et_command.text.toString()

        if (acmd.isEmpty()) {
            log.e("No command provided.")
            lazyUpdateButtons()
            return
        }

        mSearchItem?.collapseActionView()
        mCountdown.start(acmd)
    }

    override fun onPlayStopClicked(oldState: PlayStopButton.State, newState: PlayStopButton.State) {
        when (oldState) {
            PLAY -> play()
            STOP -> mCountdown.cancel()
            HIDDEN -> log.d("Clicked on hidden button. ignoring..")
        }
    }

    override fun onCountdownStarted(cmd: String?) {
        activity?.hideKeyboard()
        updatePS()
    }

    override fun onCountdownFinished(cmd: String?) {

        if (cmd == null) {
            log.d("onCountdownFinished(null)")
            return
        }

        log.w(cmd)

        try {
            (activity as MyActivity).execute(cmd)
            MIContract.CmdRecent.insert(activity, cmd)
            mi_lf_et_command.setText("")
        } catch (e: RuntimeException) {
            log.e(e.message, "ML", e)
        }

        updatePS()
    }

    override fun onCountdownCancelled(cmd: String?) = updatePS()
}
