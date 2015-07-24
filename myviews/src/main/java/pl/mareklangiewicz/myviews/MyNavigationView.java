package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.design.widget.NavigationView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.noveogroup.android.log.MyLogger;

import java.util.ArrayList;
import java.util.List;

public class MyNavigationView extends NavigationView {

    protected final MyLogger log = MyLogger.sMyDefaultUILogger;

    private OnNavigationItemSelectedListener mMyListener;

    private boolean mFreezeCheckedItems = true;

    public MyNavigationView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyNavigationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyNavigationView, defStyle, 0);
        mFreezeCheckedItems = a.getBoolean(R.styleable.MyNavigationView_freezeCheckedItems, mFreezeCheckedItems);
        a.recycle();
        super.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return selectMenuItem(item);
            }
        });
    }

    @Override
    public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        mMyListener = listener;
    }


    public void setMenu(@MenuRes int id) {
        getMenu().clear();
        if(id > 0)
            inflateMenu(id);
    }

    public void setHeader(@LayoutRes int id) {
        log.w("TODO: clear old header content");
        //TODO: clear old header content
        if(id > 0)
            inflateHeaderView(id);
    }



    public boolean selectMenuItem(MenuItem item) {
        if(item == null) {
            log.d("menu item is null!");
            return false;
        }
        if(item.isCheckable()) {
            //TODO: handle case when it is just single item with switch (not a group with checkableBehaviour:single)
            if(item.isChecked())
                return false;
            item.setChecked(true);
        }
        if(mMyListener != null)
            return mMyListener.onNavigationItemSelected(item);
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

    /**
     * Default is true.
     * Only items with id can have their checked state freezed!
     */
    public void setFreezeCheckedItems(boolean freeze) { mFreezeCheckedItems = freeze; }
    public boolean geFreezeCheckedItems() { return mFreezeCheckedItems; }







    static class SavedState extends BaseSavedState {

        ArrayList<Integer> mCheckedIds = new ArrayList<>();

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            int N = in.readInt();
            mCheckedIds.ensureCapacity(N);
            mCheckedIds.clear();
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
