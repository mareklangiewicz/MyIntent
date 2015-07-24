package pl.mareklangiewicz.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Marek Langiewicz on 30.05.15.
 * WARNING: this class works for me, but it is a kind of hack..
 * It overrides addView method to insert some additional layout (decoration),
 * so every child view becomes a grandchild or great-grandchild or so..
 * This is VIOLATION of public API contract, so it CAN CAUSE PROBLEMS.
 * Especially if you manipulate view tree structure later - dynamically..
 * But it works fine for me ;)
 * Better alternative (without so much hacking) is: MyViewDecorator.
 */
public class MyLLDecorator extends LinearLayout {

    private @LayoutRes int mDecoration = 0;
    private LayoutInflater mInflater;

    public MyLLDecorator(Context context) {
        this(context, null);
    }

    public MyLLDecorator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLLDecorator(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyLLDecorator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);


        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.MyLLDecorator, defStyleAttr, defStyleRes);
        try {
            mDecoration = a.getResourceId(R.styleable.MyLLDecorator_decoration, 0);
        }
        finally {
            a.recycle();
        }

        if(mDecoration == 0)
            return;

        mInflater = LayoutInflater.from(context);
    }



    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if(mDecoration != 0) {
            View egg = child;
            child = mInflater.inflate(mDecoration, null);
            ViewGroup content = (ViewGroup) child.findViewById(android.R.id.content);
            content.addView(egg, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        super.addView(child, index, params);
    }

}
