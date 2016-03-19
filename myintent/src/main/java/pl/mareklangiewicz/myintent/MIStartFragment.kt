package pl.mareklangiewicz.myintent

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import kotlinx.android.synthetic.main.mi_log_fragment.*
import pl.mareklangiewicz.myfragments.MyFragment
import pl.mareklangiewicz.myintent.PlayStopButton.State.*
import pl.mareklangiewicz.myutils.MyLogLevel
import pl.mareklangiewicz.myviews.IMyNavigation

class MIStartFragment : MyFragment(), PlayStopButton.Listener, Countdown.Listener {

    var mSearchItem: MenuItem? = null

    private val adapter = MyMDAndroLogAdapter(log.history)

    internal var sub: Function1<Unit, Unit>? = null

    lateinit var mPSButton: PlayStopButton
    lateinit var mCountdown: Countdown


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

        mCountdown = Countdown(mi_lf_pb_countdown)
        mCountdown.listener = this

        mPSButton = PlayStopButton(mi_lf_iv_play_stop)
        mPSButton.listener = this

        mi_lf_rv_log.adapter = adapter

        sub = log.history.changes { adapter.notifyDataSetChanged() }
        adapter.notifyDataSetChanged() // to make sure we are up to date

        //TODO SOMEDAY: some nice simple header with fragment title
        inflateMenu(R.menu.mi_log_local)
        updateCheckedItem()

        fab?.setImageResource(R.drawable.mi_ic_mic_white_24dp)

        fab?.setOnClickListener {
            if(isViewAvailable) {
                mCountdown.cancel()
                mi_lf_et_command.setText("")
                (activity as MIActivity).onCommand("start custom action listen")
            }
        }

        mi_lf_et_command.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable) { updatePS() }
        })

    }

    override fun onResume() {
        super.onResume()
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

        fab?.setOnClickListener(null)
        fab?.hide()

        mCountdown.cancel()
        mCountdown.listener = null

        sub?.invoke(Unit)
        sub = null

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

    override fun onItemSelected(nav: IMyNavigation, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_ll_i_error        -> log.history.level = MyLogLevel.ERROR
            R.id.mi_ll_i_warning      -> log.history.level = MyLogLevel.WARN
            R.id.mi_ll_i_info         -> log.history.level = MyLogLevel.INFO
            R.id.mi_ll_i_debug        -> log.history.level = MyLogLevel.DEBUG
            R.id.mi_ll_i_verbose      -> log.history.level = MyLogLevel.VERBOSE
            R.id.clear_log_history    -> log.history.clear()
            R.id.log_some_assert      -> log.a("some assert")
            R.id.log_some_error       -> log.e("some error")
            R.id.log_some_warning     -> log.w("some warning")
            R.id.log_some_info        -> log.i("some info")
            R.id.log_some_debug       -> log.d("some debug")
            R.id.log_some_verbose     -> log.v("some verbose")
            else -> return super.onItemSelected(nav, item)
        }
        return true
    }

    private fun updateCheckedItem() {
        when (log.history.level) {
            MyLogLevel.ERROR, MyLogLevel.ASSERT -> setCheckedItem(R.id.mi_ll_i_error)
            MyLogLevel.WARN -> setCheckedItem(R.id.mi_ll_i_warning)
            MyLogLevel.INFO -> setCheckedItem(R.id.mi_ll_i_info)
            MyLogLevel.DEBUG -> setCheckedItem(R.id.mi_ll_i_debug)
            MyLogLevel.VERBOSE -> setCheckedItem(R.id.mi_ll_i_verbose)
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        if (slideOffset == 0f) {
            lazyUpdateButtons()
        } else {
            fab?.hide()
            mPSButton.state = HIDDEN
        }
    }

    override fun onDrawerClosed(drawerView: View) {
        lazyUpdateButtons()
    }

    private val isSomethingOnOurFragment: Boolean
        get() {
            val lnav = localNavigation
            val gnav = globalNavigation
            return view === null || lnav !== null && lnav.overlaps(view) || gnav !== null && gnav.overlaps(view)
        }

    private fun updateFAB() {
        if (isSomethingOnOurFragment)
            fab?.hide()
        else
            fab?.show()
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
    }

    /**
     * Starts counting to start given command.
     * It will start the command if user doesn't press stop fast enough.
     * If no command is given it will try to get command from EditText
     */
    fun play(cmd: String = "") {

        if(!isViewAvailable) {
            log.e("UI not ready.")
            return
        }

        val acmd = if(!cmd.isEmpty()) cmd else mi_lf_et_command.text.toString()

        if (acmd.isEmpty()) {
            log.e("No command provided.")
            lazyUpdateButtons()
            return
        }

        mSearchItem?.collapseActionView()
        mi_lf_et_command.setText("")
        mCountdown.start(acmd)
        updatePS()

    }

    override fun onPlayStopClicked(oldState: PlayStopButton.State, newState: PlayStopButton.State) {
        when (oldState) {
            PLAY -> play()
            STOP -> mCountdown.cancel()
            HIDDEN -> log.d("Clicked on hidden button. ignoring..")
        }
    }

    override fun onCountdownStarted(cmd: String?) {
        log.w(cmd)
        updatePS()
    }

    override fun onCountdownFinished(cmd: String?) {
        if (cmd == null) {
            log.d("onCountdownFinished(null)")
            return
        }

        try {
            val ok = (activity as MIActivity).onCommand(cmd)
            if (ok)
                MIContract.CmdRecent.insert(activity, cmd)
        } catch (e: RuntimeException) {
            log.e(e.message, e)
        }

        updatePS()
    }

    override fun onCountdownCancelled(cmd: String?) {
        log.w("cancelled")
        updatePS()
    }
}
