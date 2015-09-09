package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.noveogroup.android.log.MyLogger;

import java.util.ArrayList;
import java.util.List;

public final class MyNavigationView extends NavigationView implements IMyNavigation, NavigationView.OnNavigationItemSelectedListener {

    protected final MyLogger log = MyLogger.sMyDefaultUILogger;

    private View mHeader;

    @Nullable
    private OnNavigationItemSelectedListener mMyListener;

    private boolean mFreezeCheckedItems = true;

    public MyNavigationView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyNavigationView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(@Nullable AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyNavigationView, defStyle, 0);
        mFreezeCheckedItems = a.getBoolean(R.styleable.MyNavigationView_freezeCheckedItems, mFreezeCheckedItems);
        a.recycle();
        super.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return selectMenuItem(item);
    }

    @Override
    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        mMyListener = listener;
    }


    public @Nullable View getHeader() {
        return mHeader;
    }

    public void inflateHeader(@LayoutRes int id) {
        mHeader = inflateHeaderView(id);
    }


    @Override
    public void clearMenu() {
        getMenu().clear();
    }

    @Override
    public void clearHeader() {
        removeHeaderView(mHeader);
        mHeader = null;
    }

    public boolean selectMenuItem(@Nullable MenuItem item) {
        if(item == null) {
            log.d("menu item is null!");
            return false;
        }
        if(item.isCheckable()) {
            item.setChecked(true);
            //TODO: handle case when it is just single item with switch (not a group with checkableBehaviour:single)
        }
        if(mMyListener != null) {
            return mMyListener.onNavigationItemSelected(item);
        }
        return true;
    }

    public boolean selectMenuItem(@IdRes int id) {
        Menu menu = getMenu();
        if(menu == null) {
            log.d("menu is null!");
            return false;
        }
        MenuItem item = menu.findItem(id);
        return selectMenuItem(item);
    }



    /**
     * Default is true.
     * Only items with id can have their checked state freezed!
     */
    public void setFreezeCheckedItems(boolean freeze) { mFreezeCheckedItems = freeze; }
    public boolean geFreezeCheckedItems() { return mFreezeCheckedItems; }




    void saveCheckedIds(List<Integer> output) {
        Menu menu = getMenu();
        if(menu == null) {
            log.d("menu is null!");
            return;
        }
        int N = menu.size();
        for(int i = 0; i < N; i++) {
            MenuItem item = menu.getItem(i);
            if (item.isChecked()) {
                int id = item.getItemId();
                if(id > 0)
                    output.add(id);
            }
        }
    }

    void loadCheckedIds(List<Integer> input) {
        Menu menu = getMenu();
        if(menu == null) {
            log.d("menu is null!");
            return;
        }
        for(int i : input) {
            MenuItem item = menu.findItem(i);
            if(item == null) {
                log.d("item not found!");
            }
            else {
                if(!item.isCheckable()) {
                    log.d("item is not checkable!");
                }
                else
                    item.setChecked(true);
            }
        }
    }


    static class SavedState extends BaseSavedState {

        ArrayList<Integer> mCheckedIds = new ArrayList<>();

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            int N = in.readInt();
            mCheckedIds.clear();
            mCheckedIds.ensureCapacity(N);
            for(int i = 0; i < N; i++)
                mCheckedIds.add(in.readInt());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            int N = mCheckedIds.size();
            out.writeInt(N);
            for(int i = 0; i < N; i++)
                out.writeInt(mCheckedIds.get(i));
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        if(!mFreezeCheckedItems)
            return superState;
        SavedState ss = new SavedState(superState);
        saveCheckedIds(ss.mCheckedIds);
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        loadCheckedIds(ss.mCheckedIds);
    }
}
