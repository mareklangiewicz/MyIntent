package pl.mareklangiewicz.myintent;

import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.noveogroup.android.log.Logger;

import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable;
import pl.mareklangiewicz.myfragments.MyFragment;
import pl.mareklangiewicz.myviews.IMyNavigation;

public final class MILogFragment extends MyFragment {

    private @Nullable View mRootView;
    private @Nullable ProgressBar mProgressBar; //TODO LATER: use it.
    private @Nullable SearchView mSearchView;
    private @Nullable MyMDLogAdapter mAdapter;
    private @Nullable RecyclerView mRecyclerView;
    private @Nullable FloatingActionButton mGlobalFAB;
    private @Nullable FloatingActionButton mLocalFAB;

    private final Runnable mRunUpdateGlobalFAB = new Runnable() { @Override public void run() { updateGlobalFAB(); } };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        mRootView = inflater.inflate(R.layout.mi_log_fragment, container, false);

        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.progress_bar);
        mSearchView = (SearchView) mRootView.findViewById(R.id.search_view);

        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        //noinspection ConstantConditions
        mSearchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true); // FIXME: probably not needed - remove when I implement my animated play button
        mSearchView.setQueryRefinementEnabled(true);
        mGlobalFAB = getFAB();
        mLocalFAB = (FloatingActionButton) mRootView.findViewById(R.id.local_fab);

        mAdapter = new MyMDLogAdapter();
        mAdapter.setLog(log);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.mi_log_recycler_view);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager llmanager = new LinearLayoutManager(getActivity());

        llmanager.setReverseLayout(true);
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
                ((MIActivity) getActivity()).onCommand("start custom action listen");
            }
        });

        //FIXME REMOVE CODE BELOW (IT IS TEMPORARY - FOR TESTS)
        final Drawable d = new MyPlayStopDrawable().setColorFrom(0xff0000c0).setColorTo(0xffc00000).setRotateTo(90f).setStrokeWidth(6);
        //noinspection ConstantConditions
        mLocalFAB.setImageDrawable(d);
        mLocalFAB.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ObjectAnimator.ofInt(d, "level", 0, 10000, 10000, 0).setDuration(1000).start();
                log.w("[SNACK]FAB Clicked!");
            }
        });
        //FIXME: REMOVE CODE ABOVE

        return mRootView;

    }

    @Override public void onResume() {
        super.onResume();
        updateLocalFAB();
        lazyUpdateGlobalFAB();
    }

    @Override
    public void onDestroyView() {

        //noinspection ConstantConditions
        mRootView.removeCallbacks(mRunUpdateGlobalFAB);
        mRootView = null;

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

        mProgressBar = null;
        mSearchView = null;

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
        if(mGlobalFAB == null)
            return;

        if(slideOffset == 0) {
            lazyUpdateGlobalFAB();
        }
        else {
            mGlobalFAB.hide();
        }
    }

    @Override public void onDrawerClosed(View drawerView) {
        lazyUpdateGlobalFAB();
    }

    private void updateGlobalFAB() {
        if(mGlobalFAB == null)
            return;
        IMyNavigation lnav = getLocalNavigation();
        IMyNavigation gnav = getGlobalNavigation();
        if( (lnav != null && lnav.overlaps(mRootView)) || (gnav != null && gnav.overlaps(mRootView)) )
            mGlobalFAB.hide();
        else
            mGlobalFAB.show();
    }


    private void lazyUpdateGlobalFAB() {
        if(mRootView == null) {
            log.d("lazyUpdteGlobalFAB: mRootView == null");
            return;
        }
        mRootView.removeCallbacks(mRunUpdateGlobalFAB);
        mRootView.postDelayed(mRunUpdateGlobalFAB, 300);
    }

    private void updateLocalFAB() {
        if(mLocalFAB == null)
            return;
        mLocalFAB.show();
    }

}
