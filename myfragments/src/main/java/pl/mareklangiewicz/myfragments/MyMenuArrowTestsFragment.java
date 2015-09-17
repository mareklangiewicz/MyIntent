package pl.mareklangiewicz.myfragments;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import pl.mareklangiewicz.myviews.MyMenuArrow;

public final class MyMenuArrowTestsFragment extends MyFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private @Nullable SeekBar mSeekBar;

    private @Nullable MyMenuArrow mArrow1;
    private @Nullable MyMenuArrow mArrow2;
    private @Nullable MyMenuArrow mArrow3;

    private @Nullable ObjectAnimator mAnim1;
    private @Nullable ObjectAnimator mAnim2;
    private @Nullable ObjectAnimator mAnim3;

    public MyMenuArrowTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_menu_arrow_tests_fragment, container, false);

        mSeekBar = (SeekBar) root.findViewById(R.id.seek_bar);

        mSeekBar.setOnSeekBarChangeListener(this);

        mArrow1 = (MyMenuArrow) root.findViewById(R.id.arrow1);
        mArrow2 = (MyMenuArrow) root.findViewById(R.id.arrow2);
        mArrow3 = (MyMenuArrow) root.findViewById(R.id.arrow3);

        mAnim1 = ObjectAnimator.ofPropertyValuesHolder(mArrow1,
                PropertyValuesHolder.ofFloat("direction", 0f, 1f, 1f, 0f),
                PropertyValuesHolder.ofFloat("rotation", 0f, 180f, 180f, 0f)
        ).setDuration(3000);

        mAnim2 = ObjectAnimator.ofPropertyValuesHolder(mArrow2,
                PropertyValuesHolder.ofFloat("direction", 0f, 1f, 1f, 0f),
                PropertyValuesHolder.ofFloat("rotation", 0f, 360f, 360f, 0f)
        ).setDuration(3000);

        mAnim3 = ObjectAnimator.ofPropertyValuesHolder(mArrow3,
                PropertyValuesHolder.ofFloat("direction", 0f, 1f, 1f, 0f)
        ).setDuration(3000);

        //noinspection ConstantConditions
        mArrow1.setTag(R.id.tag_animator, mAnim1);
        //noinspection ConstantConditions
        mArrow2.setTag(R.id.tag_animator, mAnim2);
        //noinspection ConstantConditions
        mArrow3.setTag(R.id.tag_animator, mAnim3);


        mArrow1.setOnClickListener(this);
        mArrow2.setOnClickListener(this);
        mArrow3.setOnClickListener(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSeekBar = null;
        mArrow1 = null;
        mArrow2 = null;
        mArrow3 = null;
        mAnim1 = null;
        mAnim2 = null;
        mAnim3 = null;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag(R.id.tag_animator);
        if(tag instanceof ObjectAnimator)
        ((ObjectAnimator)tag).start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float dir = progress / 100f;
        if (mArrow1 != null) {
            mArrow1.setDirection(dir);
            mArrow1.setRotation(dir * 180);
        }
        if (mArrow2 != null) {
            mArrow2.setDirection(dir);
            mArrow2.setRotation(dir * 360);
        }
        if (mArrow3 != null) {
            mArrow3.setDirection(dir);
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override public void onStopTrackingTouch(SeekBar seekBar) { }
}
