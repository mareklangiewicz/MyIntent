package pl.mareklangiewicz.myactivities;

import android.os.Bundle;

/**
 * Created by marek on 23.07.15.
 */
public class MyTestActivity extends MyBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGlobalMenu(R.menu.my_test_global_menu);
        setGlobalHeader(R.layout.my_test_global_header);
        if(savedInstanceState == null)
            selectGlobalMenuItem(R.id.section_my_pie_tests);
    }
}
