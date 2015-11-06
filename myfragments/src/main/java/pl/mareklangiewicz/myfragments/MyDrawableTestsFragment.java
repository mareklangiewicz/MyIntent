package pl.mareklangiewicz.myfragments;


import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import pl.mareklangiewicz.mydrawables.MyArrowDrawable;
import pl.mareklangiewicz.mydrawables.MyCheckDrawable;
import pl.mareklangiewicz.mydrawables.MyLessDrawable;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable;
import pl.mareklangiewicz.mydrawables.MyPlayStopDrawable;
import pl.mareklangiewicz.mydrawables.MyPlusDrawable;

public final class MyDrawableTestsFragment extends MyFragment implements View.OnClickListener, SeekBar
        .OnSeekBarChangeListener {

    private final MyLivingDrawable[] mDrawables = {
            new MyPlusDrawable().setColor(0xff00a000).setRotateTo(180f),
            new MyPlusDrawable().setColorFrom(0xffc00000).setColorTo(0xff0000c0).setRotateTo(90f),
            new MyPlusDrawable().setColor(0xff00a000).setRotateTo(-360f),
            new MyArrowDrawable().setColor(0xff008080).setRotateFrom(-180f),
            new MyArrowDrawable().setColor(0xff008000).setRotateFrom(180f),
            new MyArrowDrawable().setColor(0xff808000).setRotateFrom(360f),
            new MyPlayStopDrawable().setColorFrom(0xff0000c0).setColorTo(0xffc00000).setRotateTo(180f),
            new MyPlayStopDrawable().setColorFrom(0xff0000c0).setColorTo(0xffc00000).setRotateTo(90f),
            new MyCheckDrawable().setColorFrom(0xff00f000).setColorTo(0xfff00000).setRotateTo(90f),
            new MyCheckDrawable().setColorFrom(0xff00a000).setColorTo(0xffa00000).setRotateTo(180f),
            new MyCheckDrawable().setColorFrom(0xff00c000).setColorTo(0xffc00000).setRotateTo(-180f),
            new MyCheckDrawable().setColorFrom(0xff0000f0).setColorTo(0xfff00000).setRotateTo(360f),
            new MyLessDrawable().setColor(0xffa000a0).setRotateFrom(-180f),
            new MyLessDrawable().setColor(0xff8000a0).setRotateTo(180f),
            new MyLessDrawable().setColor(0xff20a050).setRotateTo(360f),
            new MyMagicLinesDrawable().setLines(0, 3000, 3000, 6000, 6000, 9000, 9000, 10000, 0, 10000).setColor
                    (0x400000a0),
            new MyMagicLinesDrawable().setRandomLines(2).setColor(0x400000a0),
            new MyMagicLinesDrawable().setRandomLines(5).setColor(0x400000a0),
            new MyMagicLinesDrawable().setRandomLines(10).setColor(0x400000a0),
            new MyMagicLinesDrawable().setRandomLines(8).setColorFrom(0x000000a0).setColorTo(0x600000a0),
            new MyMagicLinesDrawable().setColor(0x400000a0),
            new MyMagicLinesDrawable().setColor(0x400000a0)
    };
    private @Nullable SeekBar mLevelSeekBar;
    private @Nullable SeekBar mStrokeWidthSeekBar;
    private @Nullable RecyclerView mRecyclerView;

    public MyDrawableTestsFragment() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        View root = inflater.inflate(R.layout.my_dawable_tests_fragment, container, false);

        mLevelSeekBar = (SeekBar) root.findViewById(R.id.seek_bar_level);
        mStrokeWidthSeekBar = (SeekBar) root.findViewById(R.id.seek_bar_stroke_width);
        //noinspection ConstantConditions
        mLevelSeekBar.setOnSeekBarChangeListener(this);
        //noinspection ConstantConditions
        mStrokeWidthSeekBar.setOnSeekBarChangeListener(this);

        mRecyclerView = (RecyclerView) root.findViewById(R.id.grid_view);
        //noinspection ConstantConditions
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mRecyclerView.setAdapter(new MyAdapter());

        mStrokeWidthSeekBar.setProgress(12);

        return root;
    }

    @Override public void onResume() {
        super.onResume();
        final Drawable d = new MyCheckDrawable().setStrokeWidth(6).setColorFrom(0xff0000f0).setColorTo(0xff00f000)
                .setRotateTo(360f);
        //noinspection ConstantConditions
        getFAB().setImageDrawable(d);
        getFAB().setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ObjectAnimator.ofInt(d, "level", 0, 10000, 10000, 0).setDuration(7000).start();
                log.w("[SNACK]FAB Clicked!");
            }
        });
        getFAB().show();
    }

    @Override public void onPause() {
        //noinspection ConstantConditions
        getFAB().hide();
        super.onPause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mLevelSeekBar = null;
        mStrokeWidthSeekBar = null;
        mRecyclerView = null;
    }

    @Override public void onClick(View v) {
        Object tag = v.getTag(R.id.tag_animator);
        if(tag instanceof ObjectAnimator)
            ((ObjectAnimator) tag).start();
    }

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == mLevelSeekBar) {
            for(MyLivingDrawable drawable : mDrawables)
                drawable.setLevel(progress);
        }
        else if(seekBar == mStrokeWidthSeekBar) {
            for(MyLivingDrawable drawable : mDrawables)
                drawable.setStrokeWidth(progress);
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        if(seekBar == mLevelSeekBar)
            log.i("level = %d", seekBar.getProgress());
        else if(seekBar == mStrokeWidthSeekBar)
            log.i("stroke width = %d", seekBar.getProgress());
        else
            log.e("Unknown seek bar.");
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private View mContent;

        public MyViewHolder(View v, View content) {
            super(v);
            mContent = content;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        MyAdapter() {
            setHasStableIds(true);
        }

        @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View content = new View(getActivity());
            FrameLayout.LayoutParams contentparams = new FrameLayout.LayoutParams(150, 150);
            contentparams.gravity = Gravity.CENTER;
            content.setLayoutParams(contentparams);
            CardView card = new CardView(getActivity());
            ViewGroup.MarginLayoutParams cardparams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardparams.setMargins(8, 8, 8, 8);
            card.setLayoutParams(cardparams);
            card.addView(content);
            return new MyViewHolder(card, content);
        }

        @Override public void onBindViewHolder(MyViewHolder holder, int position) {
            View content = holder.mContent;
            Drawable drawable = mDrawables[position];
            ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "level", 0, 10000, 10000, 0);
            animator.setDuration(3000);
            animator.setInterpolator(new LinearInterpolator());
            content.setBackground(drawable);
            content.setTag(R.id.tag_animator, animator);
            content.setOnClickListener(MyDrawableTestsFragment.this);
        }

        @Override public int getItemCount() {
            return mDrawables.length;
        }

        @Override public long getItemId(int position) {
            return position; // our array is constant.
        }
    }
}
