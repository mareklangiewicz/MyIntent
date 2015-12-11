package pl.mareklangiewicz.myfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by Marek Langiewicz on 13.10.15.
 */
public class MyWebFragment extends MyFragment {

    public static final String ARG_URL = "url";
    protected String mURL;

    protected @Nullable WebView mWebView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mURL = args.getString(ARG_URL);
        }
    }


    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState); //just for logging

        View rootView = inflater.inflate(R.layout.mf_my_web_fragment, container, false);

        mWebView = (WebView) rootView.findViewById(R.id.my_web_view);

        mWebView.loadUrl(mURL);

        return rootView;
    }

    @Override public void onDestroyView() {

        mWebView = null;

        super.onDestroyView();

    }

    public String getURL() { return mURL; }

    public void setURL(@NonNull String url) {
        if(url.equals(mURL))
            return;
        mURL = url;
        if(mWebView != null)
            mWebView.loadUrl(mURL);
    }

}
