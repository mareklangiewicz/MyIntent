package pl.mareklangiewicz.myfragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mareklangiewicz.myviews.MyExampleView;

import static pl.mareklangiewicz.myutils.MyTextUtils.str;


public final class MyExampleFragment extends Fragment {
    private static final String ARG_TEXT = "text";

    public MyExampleFragment() {}

    public static MyExampleFragment newInstance(String text) {
        MyExampleFragment fragment = new MyExampleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_example_fragment, container, false);
        MyExampleView mev = (MyExampleView) root.findViewById(R.id.my_example_view);
        Bundle args = getArguments();
        if(args != null) {
            StringBuilder text = new StringBuilder();
            for(String key: args.keySet()) {
                text.append(key);
                text.append(": ");
                text.append(str(args.get(key)));
                text.append("\n");
            }
            mev.setText(text.toString());
        }
        return root;
    }
}
