package pl.mareklangiewicz.myintent;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.mareklangiewicz.myactivities.MyActivity;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable;
import pl.mareklangiewicz.myviews.IMyNavigation;

import static pl.mareklangiewicz.myutils.MyTextUtils.str;

/**
 * Created by Marek Langiewicz on 02.10.15.
 * My Intent main activity.
 */
public class MIActivity extends MyActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static boolean greeted = false;
    private @Nullable MyLivingDrawable mMyMagicLinesDrawable;
    private @Nullable View mMagicLinesView;
    private @Nullable ImageView mLogoImageView;
    private @Nullable TextView mLogoTextView;
    private @Nullable TextView mHomePageTextView;
    private @Nullable ObjectAnimator mLogoTextViewAnimator;
    private @Nullable ObjectAnimator mHomePageTextViewAnimator;
    private @Nullable ObjectAnimator mMagicLinesDrawableAnimator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getGlobalNavigation().inflateMenu(R.menu.mi_menu);
        getGlobalNavigation().inflateHeader(R.layout.mi_header);

        //noinspection ConstantConditions
        mMyMagicLinesDrawable = new MyMagicLinesDrawable();
        mMyMagicLinesDrawable.setColor(0x30ffffff).setStrokeWidth(dp2px(4));
        //noinspection ConstantConditions
        mMagicLinesView = getGlobalNavigation().getHeader().findViewById(R.id.magic_underline_view);
        mMagicLinesView.setBackground(mMyMagicLinesDrawable);
        mLogoImageView = (ImageView) getGlobalNavigation().getHeader().findViewById(R.id.image_logo);
        mLogoTextView = (TextView) getGlobalNavigation().getHeader().findViewById(R.id.text_logo);
        mHomePageTextView = (TextView) getGlobalNavigation().getHeader().findViewById(R.id.text_home_page);

        //noinspection ConstantConditions
        mLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mMyPendingCommand = "listen";
                if((mGlobalDrawerLayout != null && mGlobalDrawerLayout.isDrawerVisible(GravityCompat.START))) {
                    mGlobalDrawerLayout.closeDrawers();
                    // we will start pending command after drawer is closed.
                }
                else
                    startPendingCommand();
            }
        });

        //noinspection ConstantConditions
        mHomePageTextView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onCommand("data " + getResources().getString(R.string.user_id));
            }
        });

        PropertyValuesHolder pvha1 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0.2f, 1f);
        PropertyValuesHolder pvhy1 = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -130f, -30f, 0f);
        PropertyValuesHolder pvha2 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 0f, 0.7f);
        PropertyValuesHolder pvhy2 = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -50f, -50f, 0f);

        mLogoTextViewAnimator = ObjectAnimator.ofPropertyValuesHolder(mLogoTextView, pvha1, pvhy1);
        mHomePageTextViewAnimator = ObjectAnimator.ofPropertyValuesHolder(mHomePageTextView, pvha2, pvhy2);
        mLogoTextViewAnimator.setInterpolator(new LinearInterpolator());
        mHomePageTextViewAnimator.setInterpolator(new LinearInterpolator());

        mMagicLinesDrawableAnimator = ObjectAnimator.ofInt(mMyMagicLinesDrawable, "level", 0, 10000);
        mMagicLinesDrawableAnimator.setDuration(1000).setInterpolator(new LinearInterpolator());

        if(savedInstanceState == null) {
            selectGlobalItem(R.id.mi_start);
        }
    }

    @Override public void onIntent(@Nullable Intent intent) {

        super.onIntent(intent);

        try {
            if(intent == null)
                log.d("null intent received - ignoring");
            else if(Intent.ACTION_SEARCH.equals(intent.getAction()) || "com.google.android.gms.actions.SEARCH_ACTION".equals(intent.getAction()))
                onSearchIntent(intent);
            else if(intent.getAction().equals(Intent.ACTION_VIEW))
                onUri(intent.getData());
            else if(intent.getAction().equals(Intent.ACTION_MAIN)) {
                if(!greeted) {
                    log.i("TODO: hello, and where is help..");
                    greeted = true;
                }
            }
            else
                log.e("Unknown intent received: %s", str(intent));
        }
        catch(RuntimeException e) {
            log.e("Intent exception.", e);
        }

    }

    private void intro() {

    }


    public void onUri(@Nullable Uri uri) {

        if(uri == null) {
            log.d("null uri received - ignoring");
            return;
        }

        String command = uri.getFragment();

        if(command == null) {
            log.d("uri with empty fragment received - ignoring");
            log.v("uri: %s", str(uri));
            return;

        }

        boolean ok = onCommand(command);
        if(!ok)
            return;
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MISuggestionProvider.AUTHORITY, MISuggestionProvider.MODE);
        suggestions.saveRecentQuery(command, null);
    }


    private void onSearchIntent(Intent intent) {

        String command = intent.getStringExtra(SearchManager.QUERY);
        boolean ok = onCommand(command);
        if(!ok)
            return;
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MISuggestionProvider.AUTHORITY, MISuggestionProvider.MODE);
        suggestions.saveRecentQuery(command, null);
    }

    // TODO LATER: make option in settings for this:
    // TODO LATER: move it to start fragment?
    private void clearSearchSuggestions() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MISuggestionProvider.AUTHORITY, MISuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.ask_for_intent));
        // Start the activity, the intent will be populated with the speech text
        //TODO: check if it is available first..
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where we process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if(requestCode == SPEECH_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String command = results.get(0);

                //TODO real code: set fragment with command and enable super button there
                boolean ok = onCommand(command);
                if(ok) {
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MISuggestionProvider.AUTHORITY, MISuggestionProvider.MODE);
                    suggestions.saveRecentQuery(command, null);
                }
            }
            else if(resultCode == RESULT_CANCELED) {
                log.d("Voice recognition cancelled.");
            }
            else {
                log.e("Voice recognition error. code: %d", resultCode); //TODO: better resultCode recognition
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public boolean onCommand(@Nullable String command) {

        if(command == null) {
            log.d("null command received - ignoring");
            return false;
        }
        if(command.equals("listen")) {
            startSpeechRecognizer();
            return true;
        }
        return super.onCommand(command);
    }

    @Override protected void onDestroy() {

        mMagicLinesDrawableAnimator = null;
        mHomePageTextViewAnimator = null;
        mLogoTextViewAnimator = null;
        mHomePageTextView = null;
        mLogoTextView = null;
        mLogoImageView = null;
        mMagicLinesView = null;
        mMyMagicLinesDrawable = null;

        super.onDestroy();
    }

    @Override public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, slideOffset);
        if(drawerView != mGlobalNavigationView)
            return;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(mLogoTextViewAnimator != null)
                mLogoTextViewAnimator.setCurrentFraction(slideOffset);
            if(mHomePageTextViewAnimator != null)
                mHomePageTextViewAnimator.setCurrentFraction(slideOffset);
        }
    }


    @Override public void onDrawerOpened(View drawerView) {
        super.onDrawerOpened(drawerView);
        if(drawerView != mGlobalNavigationView)
            return;
        if(mMagicLinesDrawableAnimator != null)
            if(!mMagicLinesDrawableAnimator.isStarted())
                mMagicLinesDrawableAnimator.start();
    }

    @Override public void onDrawerClosed(View drawerView) {
        super.onDrawerClosed(drawerView);
        if(drawerView != mGlobalNavigationView)
            return;
        if(mMagicLinesDrawableAnimator != null)
            mMagicLinesDrawableAnimator.cancel();
        if(mMyMagicLinesDrawable != null)
            mMyMagicLinesDrawable.setLevel(0);
    }

    @Override public boolean onItemSelected(IMyNavigation nav, MenuItem item) {

        boolean done = super.onItemSelected(nav, item);

        if(done)
            return true;

        @IdRes int id = item.getItemId();

        if(id == R.id.action_whats_up) {
            log.i("[SNACK][SHORT]What's up mate?");
            return true;
        }

        return false;
    }

}
