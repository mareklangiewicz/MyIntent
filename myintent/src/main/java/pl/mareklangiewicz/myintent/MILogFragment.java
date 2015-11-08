package pl.mareklangiewicz.myintent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.noveogroup.android.log.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable;
import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myviews.IMyNavigation;

import static pl.mareklangiewicz.myutils.MyMathUtils.scale0d;

public final class MILogFragment extends MyFragment {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LFAB_HIDDEN, LFAB_PLAY, LFAB_STOP})
    public @interface LocalFabState {}
    public static final int LFAB_HIDDEN = 0;
    public static final int LFAB_PLAY = 1;
    public static final int LFAB_STOP = 2;




    private @Nullable View mRootView;
    private @Nullable ProgressBar mProgressBar;
    private @Nullable SearchView mSearchView;
    private @Nullable MyMDLogAdapter mAdapter;
    private @Nullable RecyclerView mRecyclerView;
    private @Nullable FloatingActionButton mGlobalFAB;
    private @Nullable FloatingActionButton mLocalFAB;
    private @Nullable ObjectAnimator mLocalFABAnimator;

    private @Nullable ObjectAnimator mCountdownAnimator;

    private boolean mCountdownEnabled = false;

    private @LocalFabState int mLocalFABState = LFAB_HIDDEN;

    private @LocalFabState int getLocalFABState() { return mLocalFABState; }

    private void setLocalFABState(@LocalFabState int state) {
        if(mLocalFAB == null || mLocalFABAnimator == null) {
            log.e("Local FAB not initialized.");
            mLocalFABState = state;
            return;
        }
        if(state == mLocalFABState)
            return;
        if(state == LFAB_HIDDEN)
            mLocalFAB.hide();
        else if(mLocalFABState == LFAB_HIDDEN) {
            mLocalFABAnimator.cancel(); //to be sure
            if(state == LFAB_PLAY)
                mLocalFABAnimator.reverse();
            else
                mLocalFABAnimator.start();
            mLocalFAB.show();
        }
        else {
            if(mLocalFABAnimator.isRunning()) {
                mLocalFABAnimator.reverse();
            }
            else { // we are stopped at some end
                if(mLocalFABState == LFAB_STOP && state == LFAB_PLAY)
                    mLocalFABAnimator.reverse();
                else if(mLocalFABState == LFAB_PLAY && state == LFAB_STOP)
                    mLocalFABAnimator.start();
                else
                    log.e("Incorrect Local FAB state.");
            }
        }
        mLocalFABState = state;
    }

    private final Runnable mRunUpdateFABs = new Runnable() {
        @Override public void run() { updateGlobalFAB(); updateLocalFAB();}
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        mRootView = inflater.inflate(R.layout.mi_log_fragment, container, false);

        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        //noinspection ConstantConditions
        mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if(mCountdownEnabled && mCountdownAnimator != null && mProgressBar != null) {
                    // int moveto = (int)scale0d(event.getX(), mProgressBar.getWidth(), mCountdownAnimator.getDuration());
                    // the above is nice and correct, but more practical is to just move progress to almost the end immediately
                    // so it will speed up command execution wherever we click.
                    int moveto = (int) mCountdownAnimator.getDuration() - 50;
                    if(moveto < 0) moveto = 0; // in case we have set really short duration ( < 50ms..)

                    mCountdownAnimator.setCurrentPlayTime(moveto);
                }
                return false;
            }
        });


        mCountdownAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 10000).setDuration(3000);
        mCountdownAnimator.setInterpolator(new LinearInterpolator());
        mCountdownAnimator.addListener(new AnimatorListenerAdapter() { @Override public void onAnimationEnd(Animator animation) { onCountdownEnd(); } });


        mSearchView = (SearchView) mRootView.findViewById(R.id.search_view);

        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        //noinspection ConstantConditions
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));

        mGlobalFAB = getFAB();
        mLocalFAB = (FloatingActionButton) mRootView.findViewById(R.id.local_fab);

        mAdapter = new MyMDLogAdapter();
        mAdapter.setLog(log);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.mi_log_recycler_view);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager llmanager = new LinearLayoutManager(getActivity());

//        llmanager.setReverseLayout(true);
        //noinspection ConstantConditions
        mRecyclerView.setLayoutManager(llmanager);

        //TODO SOMEDAY: some nice simple header with fragment title
        inflateMenu(R.menu.mi_log_lmenu);
        updateCheckedItem();

        //noinspection ConstantConditions
        mGlobalFAB.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
//        CoordinatorLayout.LayoutParams lparams = ((CoordinatorLayout.LayoutParams) mGlobalFAB.getLayoutParams());
//        lparams.setAnchorId(R.id.mi_log_recycler_view);
//        int margin = (int)((MyActivity)getActivity()).dp2px(8);
//        lparams.setMargins(margin, margin, margin, margin);
//        lparams.anchorGravity = Gravity.END;

        mGlobalFAB.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                cancelCountdown();
                ((MIActivity) getActivity()).onCommand("start custom action listen");
            }
        });

        final Drawable d = new MyPlayStopDrawable().setColorFrom(0xff0000c0).setColorTo(0xffc00000).setRotateTo(90f).setStrokeWidth(6);
        //noinspection ConstantConditions
        mLocalFAB.setImageDrawable(d);
        mLocalFABAnimator = ObjectAnimator.ofInt(d, "level", 0, 10000).setDuration(300);
        mLocalFAB.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(getLocalFABState() == LFAB_PLAY)
                    startCountdown();
                else if(getLocalFABState() == LFAB_STOP)
                    cancelCountdown();
            }
        });

        return mRootView;

    }

    @Override public void onResume() {
        super.onResume();
        lazyUpdateFABs();
    }

    @Override public void onStart() {
        super.onStart();
        if(mProgressBar != null)
            mProgressBar.setProgress(0); // some old value could be inflated after screen rotation..
    }

    @Override public void onStop() {
        cancelCountdown();
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        //noinspection ConstantConditions
        mRootView.removeCallbacks(mRunUpdateFABs);
        mRootView = null;

        mLocalFABState = LFAB_HIDDEN;
        if(mLocalFABAnimator != null) {
            mLocalFABAnimator.cancel();
            mLocalFABAnimator = null;
        }

        if(mGlobalFAB != null) {
            mGlobalFAB.setOnClickListener(null);
            mGlobalFAB.hide();
            mGlobalFAB = null;
        }
        if(mLocalFAB != null) {
            mLocalFAB.setOnClickListener(null);
            mLocalFAB.hide();
            mLocalFAB = null;
        }

        if(mProgressBar != null) {
            mProgressBar.setOnTouchListener(null);
            mProgressBar.setProgress(0);
            mProgressBar = null;
        }

        mCountdownEnabled = false;
        mSearchView = null;

        if(mCountdownAnimator != null) {
            mCountdownAnimator.cancel();
            mCountdownAnimator = null;
        }

        //noinspection ConstantConditions
        mAdapter.setLog(null);
        mAdapter = null;
        //noinspection ConstantConditions
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;
        super.onDestroyView();
    }

    @Override
    public boolean onItemSelected(IMyNavigation nav, MenuItem item) {
        @IdRes int id = item.getItemId();
        if(id == R.id.log_level_error) {
            log.setHistoryFilterLevel(Logger.Level.ERROR);
            return true;
        }
        else if(id == R.id.log_level_warning) {
            log.setHistoryFilterLevel(Logger.Level.WARN);
            return true;
        }
        else if(id == R.id.log_level_info) {
            log.setHistoryFilterLevel(Logger.Level.INFO);
            return true;
        }
        else if(id == R.id.log_level_debug) {
            log.setHistoryFilterLevel(Logger.Level.DEBUG);
            return true;
        }
        else if(id == R.id.log_level_verbose) {
            log.setHistoryFilterLevel(Logger.Level.VERBOSE);
            return true;
        }
        else if(id == R.id.clear_log_history) {
            log.getLogHistory().clear();
            //noinspection ConstantConditions
            mAdapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.log_some_assert) {
            log.a("some assert");
            return true;
        }
        else if(id == R.id.log_some_error) {
            log.e("some error");
            return true;
        }
        else if(id == R.id.log_some_warning) {
            log.w("some warning");
            return true;
        }
        else if(id == R.id.log_some_info) {
            log.i("some info");
            return true;
        }
        else if(id == R.id.log_some_debug) {
            log.d("some debug");
            return true;
        }
        else if(id == R.id.log_some_verbose) {
            log.v("some verbose");
            return true;
        }
        return super.onItemSelected(nav, item);
    }

    private void updateCheckedItem() {
        switch(log.getLogHistory().getFilterLevel()) {
            case ERROR:
                setCheckedItem(R.id.log_level_error);
                break;
            case WARN:
                setCheckedItem(R.id.log_level_warning);
                break;
            case INFO:
                setCheckedItem(R.id.log_level_info);
                break;
            case DEBUG:
                setCheckedItem(R.id.log_level_debug);
                break;
            case VERBOSE:
                setCheckedItem(R.id.log_level_verbose);
                break;
        }
    }

    @Override public void onDrawerSlide(View drawerView, float slideOffset) {

        if(slideOffset == 0) {
            lazyUpdateFABs();
        }
        else {
            if(mGlobalFAB != null) mGlobalFAB.hide();
            setLocalFABState(LFAB_HIDDEN);
        }
    }

    @Override public void onDrawerClosed(View drawerView) {
        lazyUpdateFABs();
    }

    private boolean isSomethingOnOurFragment() {
        IMyNavigation lnav = getLocalNavigation();
        IMyNavigation gnav = getGlobalNavigation();
        return (lnav != null && lnav.overlaps(mRootView)) || (gnav != null && gnav.overlaps(mRootView));
    }

    private void updateGlobalFAB() {
        if(mGlobalFAB == null)
            return;
        if(isSomethingOnOurFragment())
            mGlobalFAB.hide();
        else
            mGlobalFAB.show();
    }


    private void lazyUpdateFABs() {
        if(mRootView == null) {
            log.d("lazyUpdateFABs: mRootView == null");
            return;
        }
        mRootView.removeCallbacks(mRunUpdateFABs);
        mRootView.postDelayed(mRunUpdateFABs, 300);
    }

    private void updateLocalFAB() {
        if(isSomethingOnOurFragment())
            setLocalFABState(LFAB_HIDDEN);
        else {
            if(mCountdownEnabled)
                setLocalFABState(LFAB_STOP);
            else
                setLocalFABState(LFAB_PLAY);
        }
    }

    /**
     * Inserts command to edit text and presses play.
     * It will start the command if user doesn't press stop fast enough.
     */
    public void playCommand(@Nullable String command) {

        if(command == null) {
            log.d("null command received - ignoring");
            return;
        }

        if(mSearchView == null) {
            log.d("mSearchView is null");
            return;
        }

        mSearchView.setQuery(command, false);

        startCountdown();

    }

    public void startCountdown() {

        cancelCountdown();

        if(mSearchView == null || mSearchView.getQuery().length() == 0) {
            log.e("No command provided.");
            return;
        }

        if(mCountdownAnimator == null) {
            log.e("Countdown animator not initialized.");
            mCountdownEnabled = false;
            return;
        }

        log.w("> cmd: %s", mSearchView.getQuery().toString());

        mCountdownEnabled = true;
        mCountdownAnimator.start();
        updateLocalFAB();

    }

    public void cancelCountdown() {
        if(mCountdownEnabled)
            log.w("< cmd: cancelled");
        mCountdownEnabled = false;
        if(mCountdownAnimator != null) {
            mCountdownAnimator.cancel(); //mCountdownEnabled have to be false before this line. (it calls onCountdownEnd)
        }
        if(mProgressBar != null) {
            mProgressBar.setProgress(0);
        }
        updateLocalFAB();
    }

    public void onCountdownEnd() {

        if(!mCountdownEnabled)
            return;

        mCountdownEnabled = false;

        if(mSearchView == null) {
            log.e("The mSearchView is not initialized.");
            return;
        }

        String cmd = mSearchView.getQuery().toString();
        mSearchView.setQuery("", false);

        boolean ok = ((MIActivity) getActivity()).onCommand(cmd);
        if(ok)
            MIContract.CmdRecent.insert(getActivity(), cmd);

        updateLocalFAB();

        if(mProgressBar != null) {
            mProgressBar.setProgress(0);
        }
    }
}
