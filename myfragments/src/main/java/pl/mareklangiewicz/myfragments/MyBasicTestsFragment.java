package pl.mareklangiewicz.myfragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class MyBasicTestsFragment extends MyBaseFragment {

    private FloatingActionButton mFAB;

    public MyBasicTestsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        mFAB = (FloatingActionButton) root.findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.w("[SNACK]FAB Clicked!");
            }
        });
        // FIXME: FAB should move up when snack bar is displayed..


        return root;
    }
}
