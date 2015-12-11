package pl.mareklangiewicz.myintent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import pl.mareklangiewicz.myfragments.MyWebFragment;

/**
 * Created by Marek Langiewicz on 13.10.15.
 */
public class MIHelpFragment extends MyWebFragment {

    @SuppressLint("SetJavaScriptEnabled") @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        //noinspection ConstantConditions
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUserAgentString(webSettings.getUserAgentString() + " " + getString(R.string.mi_user_agent_suffix));
        webSettings.setJavaScriptEnabled(true);

        if(getURL() == null)
            setURL("file:///android_asset/mi.html");


        return rootView;
    }
}
