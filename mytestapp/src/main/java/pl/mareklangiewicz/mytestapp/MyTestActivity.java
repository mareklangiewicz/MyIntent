package pl.mareklangiewicz.mytestapp;

import android.os.Bundle;

/**
 * Created by marek on 23.07.15.
 */

// TODO: set lint to fatal and remove all problems before putting MyBlocks on github
    
public final class MyTestActivity extends pl.mareklangiewicz.myactivities.MyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlobalNavigation.inflateMenu(R.menu.my_test_global_menu);
        mGlobalNavigation.inflateHeader(R.layout.my_test_global_header);
        if(savedInstanceState == null)
            mGlobalNavigation.selectMenuItem(R.id.section_my_pie_tests);
    }
}
