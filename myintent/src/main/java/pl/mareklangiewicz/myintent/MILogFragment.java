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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.noveogroup.android.log.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable;
import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myviews.IMyNavigation;

public final class MILogFragment extends MyFragment {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PS_HIDDEN,
            PS_PLAY,
            PS_STOP
    })
    public @interface PSState {
    }

    public static final int PS_HIDDEN = 0;
    public static final int PS_PLAY = 1;
    public static final int PS_STOP = 2;


    private @Nullable View mRootView;
    private @Nullable ProgressBar mProgressBar;
    private @Nullable EditText mEditText;
    private @Nullable MyMDLogAdapter mAdapter;
    private @Nullable RecyclerView mRecyclerView;
    private @Nullable FloatingActionButton mFAB;
    private @Nullable ImageView mPSImageView;
    private @Nullable ObjectAnimator mPSAnimator;

    private @Nullable ObjectAnimator mCountdownAnimator;

    private @Nullable String mCountdownCommand = null; // we use this to track if the red line is running at the moment (null = it doesn't)

    private @PSState int mPSState = PS_HIDDEN;

    private @PSState int getPSState() { return mPSState; }

    private void setPSState(@PSState int state) {
        if(mPSImageView == null || mPSAnimator == null) {
            log.e("Animated button not initialized.");
            mPSState = state;
            return;
        }
        if(state == mPSState)
            return;
        if(state == PS_HIDDEN)
            mPSImageView.animate().alpha(0);
        else if(mPSState == PS_HIDDEN) {
            mPSAnimator.cancel(); //to be sure
            if(state == PS_PLAY)
                mPSAnimator.reverse();
            else
                mPSAnimator.start();
            mPSImageView.animate().alpha(1);
        }
        else {
            if(mPSAnimator.isRunning()) {
                mPSAnimator.reverse();
            }
            else { // we are stopped at some end
                if(mPSState == PS_STOP && state == PS_PLAY)
                    mPSAnimator.reverse();
                else if(mPSState == PS_PLAY && state == PS_STOP)
                    mPSAnimator.start();
                else
                    log.a("Incorrect animated button state.");
            }
        }
        mPSState = state;
    }

    private final Runnable mRunUpdateButtons = new Runnable() {
        @Override public void run() {
            updateFAB();
            updatePS();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        setHasOptionsMenu(true);

        mRootView = inflater.inflate(R.layout.mi_log_fragment, container, false);

        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        //noinspection ConstantConditions
        mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if(mCountdownCommand != null && mCountdownAnimator != null && mProgressBar != null) {
                    // int moveto = (int)scale0d(event.getX(), mProgressBar.getWidth(), mCountdownAnimator.getDuration());
                    // the above is nice and correct, but more practical is to just move progress to almost the end immediately
                    // so it will speed up command execution wherever we click.
                    int moveto = (int) mCountdownAnimator.getDuration() - 50;
                    if(moveto < 0)
                        moveto = 0; // in case we have set really short duration ( < 50ms..)

                    mCountdownAnimator.setCurrentPlayTime(moveto);
                }
                return false;
            }
        });

        mCountdownAnimator = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 10000).setDuration(3000);
        mCountdownAnimator.setInterpolator(new LinearInterpolator());
        mCountdownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) { onCountdownEnd(); }
        });

        mEditText = (EditText) mRootView.findViewById(R.id.edit_text);

        mFAB = getFAB();
        mPSImageView = (ImageView) mRootView.findViewById(R.id.play_stop_image_view);

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
        mFAB.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
//        CoordinatorLayout.LayoutParams lparams = ((CoordinatorLayout.LayoutParams) mFAB.getLayoutParams());
//        lparams.setAnchorId(R.id.mi_log_recycler_view);
//        int margin = (int)((MyActivity)getActivity()).dp2px(8);
//        lparams.setMargins(margin, margin, margin, margin);
//        lparams.anchorGravity = Gravity.END;

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                cancelCountdown();
                if(mEditText != null)
                    mEditText.setText("");
                ((MIActivity) getActivity()).onCommand("start custom action listen");
            }
        });

        final Drawable d = new MyPlayStopDrawable().setColorFrom(0xff0000c0).setColorTo(0xffc00000).setRotateTo(90f).setStrokeWidth(6);
        //noinspection ConstantConditions
        mPSImageView.setImageDrawable(d);
        mPSAnimator = ObjectAnimator.ofInt(d, "level", 0, 10000).setDuration(300);
        mPSImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(getPSState() == PS_PLAY)
                    startCountdown();
                else if(getPSState() == PS_STOP)
                    cancelCountdown();
                // we ignore click if the state is hidden.
            }
        });

        return mRootView;

    }

    @Override public void onResume() {
        super.onResume();
        lazyUpdateButtons();
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
        mRootView.removeCallbacks(mRunUpdateButtons);
        mRootView = null;

        mPSState = PS_HIDDEN;
        if(mPSAnimator != null) {
            mPSAnimator.cancel();
            mPSAnimator = null;
        }

        if(mFAB != null) {
            mFAB.setOnClickListener(null);
            mFAB.hide();
            mFAB = null;
        }
        if(mPSImageView != null) {
            mPSImageView.setOnClickListener(null);
            mPSImageView.animate().alpha(0);
            mPSImageView = null;
        }

        if(mProgressBar != null) {
            mProgressBar.setOnTouchListener(null);
            mProgressBar.setProgress(0);
            mProgressBar = null;
        }

        mCountdownCommand = null;
        mEditText = null;

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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mi_log_omenu, menu);
        SearchView sw = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        sw.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        sw.setIconifiedByDefault(true);
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
            lazyUpdateButtons();
        }
        else {
            if(mFAB != null)
                mFAB.hide();
            setPSState(PS_HIDDEN);
        }
    }

    @Override public void onDrawerClosed(View drawerView) {
        lazyUpdateButtons();
    }

    private boolean isSomethingOnOurFragment() {
        IMyNavigation lnav = getLocalNavigation();
        IMyNavigation gnav = getGlobalNavigation();
        return (lnav != null && lnav.overlaps(mRootView)) || (gnav != null && gnav.overlaps(mRootView));
    }

    private void updateFAB() {
        if(mFAB == null)
            return;
        if(isSomethingOnOurFragment())
            mFAB.hide();
        else
            mFAB.show();
    }


    private void lazyUpdateButtons() {
        if(mRootView == null) {
            log.d("lazyUpdateButtons: mRootView == null");
            return;
        }
        mRootView.removeCallbacks(mRunUpdateButtons);
        mRootView.postDelayed(mRunUpdateButtons, 300);
    }

    private void updatePS() {
        if(isSomethingOnOurFragment())
            setPSState(PS_HIDDEN);
        else {
            if(mCountdownCommand == null)
                setPSState(PS_PLAY);
            else
                setPSState(PS_STOP);
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

        if(mEditText == null) {
            log.d("mEditText is null");
            return;
        }

        mEditText.setText(command);

        startCountdown();

    }

    public void startCountdown() {

        cancelCountdown();

        if(mEditText == null || mEditText.getText().length() == 0) {
            log.e("No command provided.");
            return;
        }

        if(mCountdownAnimator == null) {
            log.a("Countdown animator not initialized.");
            mCountdownCommand = null;
            return;
        }

        mCountdownCommand = mEditText.getText().toString();
        log.w("> cmd: %s", mCountdownCommand);

        mCountdownAnimator.start();
        updatePS();

    }

    public void cancelCountdown() {
        if(mCountdownCommand != null)
            log.w("< cmd: cancelled");
        mCountdownCommand = null;
        if(mCountdownAnimator != null) {
            mCountdownAnimator.cancel(); //mCountdownEnabled have to be false before this line. (it calls onCountdownEnd)
        }
        if(mProgressBar != null) {
            mProgressBar.setProgress(0);
        }
        updatePS();
    }

    public void onCountdownEnd() {

        if(mCountdownCommand == null)
            return;


        if(mEditText == null) {
            log.a("The mEditText is not initialized.");
            mCountdownCommand = null;
            return;
        }

        mEditText.setText("");

        boolean ok = ((MIActivity) getActivity()).onCommand(mCountdownCommand);
        if(ok)
            MIContract.CmdRecent.insert(getActivity(), mCountdownCommand);

        mCountdownCommand = null;

        updatePS();

        if(mProgressBar != null) {
            mProgressBar.setProgress(0);
        }
    }
}
