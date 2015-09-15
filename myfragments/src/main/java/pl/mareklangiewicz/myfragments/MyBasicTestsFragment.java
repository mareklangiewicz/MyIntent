package pl.mareklangiewicz.myfragments;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import pl.mareklangiewicz.myviews.MyMenuArrow;
import pl.mareklangiewicz.myviews.MyPie;


public final class MyBasicTestsFragment extends MyFragment implements DrawerLayout.DrawerListener {

    private @Nullable MyPie mPie1;
    private @Nullable MyPie mPie3;
    private @Nullable MyMenuArrow mArrow;

    public MyBasicTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflateHeader(R.layout.my_basic_header);

        View root = inflater.inflate(R.layout.my_basic_tests_fragment, container, false);
        EditText et = (EditText) root.findViewById(R.id.edit_text_name);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if ((v.getText().toString().equalsIgnoreCase("Marek"))) {
                        v.setError("You are not Marek! I am Marek!!");
                        log.e("[SNACK]You are not Marek! I am Marek!!");
                    } else {
                        log.i("[SNACK]Hello %s!", v.getText());
                    }
                    return false; //false so keyboard is hidden anyway
                }
                return false;
            }
        });

        mPie1 = (MyPie) root.findViewById(R.id.pie1);
        mPie3 = (MyPie) root.findViewById(R.id.pie3);

        mArrow = (MyMenuArrow) root.findViewById(R.id.my_first_menu_arrow);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPie1 = null;
        mPie3 = null;
        mArrow = null;
    }

    @Override
    public void onDrawerSlide(View view, float slideOffset) {
        if (mPie1 != null) {
            mPie1.setRotation(slideOffset * 360);
        }
        if (mPie3 != null) {
            mPie3.setTo(75 - slideOffset * 50);
        }
        if (mArrow != null) {
            mArrow.setDirection(slideOffset);
        }
        if (mArrow != null) {
            mArrow.setRotation(slideOffset * 180);
        }
    }

    @Override public void onDrawerOpened(View view) { }
    @Override public void onDrawerClosed(View view) { }
    @Override public void onDrawerStateChanged(int i) { }

}
