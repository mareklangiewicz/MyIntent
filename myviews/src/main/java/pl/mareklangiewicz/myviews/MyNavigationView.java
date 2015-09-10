package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.noveogroup.android.log.MyLogger;

import java.util.ArrayList;
import java.util.List;

public final class MyNavigationView extends NavigationView implements IMyNavigation {

    protected final MyLogger log = MyLogger.sMyDefaultUILogger;

    private @Nullable View mHeader;

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
    }


    public @Nullable View getHeader() {
        return mHeader;
    }

    public void inflateHeader(@LayoutRes int id) {
        mHeader = inflateHeaderView(id);
    }


    /**
     * Finds (recursively: DFS) first checked item in navigation view menu.
     * WARNING: do not call it too soon!
     * it works correctly from Fragment.onResume.
     * it does NOT work correctly from Fragment.onViewStateRestored!!!
     * @return
     */
    @Override
    public @Nullable MenuItem getFirstCheckedItem() {
        Menu menu = getMenu();
        if(menu == null) {
            log.w("Menu is null");
            return null;
        }
        return getFirstCheckedItem(menu);
    }


    static private @Nullable MenuItem getFirstCheckedItem(@NonNull Menu menu) {
        int size = menu.size();
        for(int i = 0; i < size; ++i) {
            MenuItem item = menu.getItem(i);
            if(item.isChecked())
                return item;
            if(item.hasSubMenu()) {
                Menu submenu = item.getSubMenu();
                if(submenu != null) {
                    MenuItem fci = getFirstCheckedItem(submenu);
                    if(fci != null)
                        return fci;
                }
            }
        }
        return null;
    }


    @Override
    public void clearMenu() {
        getMenu().clear();
    }

    @Override
    public void clearHeader() {
        if(mHeader == null)
            return;
        removeHeaderView(mHeader);
        mHeader = null;
    }











/*
    FIXME: WARNING looks like there is a bug in NavigationView class:
           It looks like state of checked menu items is preserved after configuration change,
           but the MenuItem.isChecked returns false for every item...
           (I think that only views of items are recreated correctly)
           As a workaround we allow to save checked items manually below.
    TODO NOW: load checked ids when user inflates new menu!
    TODO:  Remove code below if NavigationView is fixed..
*/


/*

    private void saveCheckedIds(List<Integer> output) {
        Menu menu = getMenu();
        if(menu == null) {
            log.d("menu is null!");
            return;
        }
        saveCheckedIds(menu, output);
    }

    private static void saveCheckedIds(@NonNull Menu menu, @NonNull List<Integer> output) {
        int N = menu.size();
        for(int i = 0; i < N; i++) {
            MenuItem item = menu.getItem(i);
            if (item.isChecked()) {
                int id = item.getItemId();
                if(id > 0)
                    output.add(id);
            }
            if(item.hasSubMenu())
                saveCheckedIds(item.getSubMenu(), output);
        }
    }



    private void loadCheckedIds(List<Integer> input) {
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


*/
}
