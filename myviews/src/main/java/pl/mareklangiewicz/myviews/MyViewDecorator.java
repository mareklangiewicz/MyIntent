package pl.mareklangiewicz.myviews;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * Created by Marek Langiewicz on 31.05.15.
 * TODO later: javadocs and tests
 * We do not decorate inside already decorated view.
 */
public final class MyViewDecorator {

    static public void decorateOne(@NonNull View view, @LayoutRes int decoration, @Nullable LayoutInflater inflater) {

        if(inflater == null)
            inflater = LayoutInflater.from(view.getContext());

        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup parent = (ViewGroup) view.getParent();
        int idx = parent.indexOfChild(view);
        parent.removeViewAt(idx);
        View decorated = inflater.inflate(decoration, null);
        parent.addView(decorated, idx, params);
        ViewGroup content = (ViewGroup) decorated.findViewById(android.R.id.content);
        content.addView(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

    }

    static public void decorateTree(@NonNull View root, @NonNull String tag, @LayoutRes int decoration,
                                    @Nullable LayoutInflater inflater) {
        if(inflater == null)
            inflater = LayoutInflater.from(root.getContext());

        if(tag.equals(root.getTag())) {
            decorateOne(root, decoration, inflater);
        }
        else if(root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            int N = group.getChildCount();
            for(int i = 0; i < N; ++i) {
                decorateTree(group.getChildAt(i), tag, decoration, inflater);
            }
        }
    }

    static public void decorateTree(@NonNull View root, Map<String, Integer> decorations,
                                    @Nullable LayoutInflater inflater) {
        if(inflater == null)
            inflater = LayoutInflater.from(root.getContext());
        if(decorations.containsKey(root.getTag().toString())) {
            decorateOne(root, decorations.get(root.getTag().toString()), inflater);
        }
        else if(root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            int N = group.getChildCount();
            for(int i = 0; i < N; ++i) {
                decorateTree(group.getChildAt(i), decorations, inflater);
            }
        }
    }

}
