package pl.mareklangiewicz.myfragments;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public final class MyBasicTestsFragment extends MyFragment {

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


        return root;
    }
}
