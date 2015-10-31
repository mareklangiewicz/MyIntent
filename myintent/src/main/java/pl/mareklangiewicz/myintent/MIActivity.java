package pl.mareklangiewicz.myintent;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;
import java.util.Map;

import pl.mareklangiewicz.myactivities.MyActivity;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable;
import pl.mareklangiewicz.myutils.MyCommands;
import pl.mareklangiewicz.myviews.IMyNavigation;

import static pl.mareklangiewicz.myutils.MyTextUtils.str;

/**
 * Created by Marek Langiewicz on 02.10.15.
 * My Intent main activity.
 */
public class MIActivity extends MyActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static boolean sGreeted = false;
    private boolean mSkipSavingToDb = false;
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
                mMyPendingCommand = "start custom action listen";
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

            List<MyCommands.RERule> rules = MyCommands.RE_USER_GROUP.getRules();
            rules.clear();
            boolean ok = MIContract.RuleUser.load(this, rules);
            if(!ok)
                log.e("Can not load user rules from data base.");
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
                if(!sGreeted) {
                    intro();
                    sGreeted = true;
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
        log.i("TODO: hello, and where is help..");
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
        MIContract.CmdRecent.insert(this, command);
    }


    private void onSearchIntent(Intent intent) {

        String command = intent.getStringExtra(SearchManager.QUERY);
        boolean ok = onCommand(command);
        if(!ok)
            return;
        MIContract.CmdRecent.insert(this, command);
    }

    void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.ask_for_intent));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US"); //TODO SOMEDAY: remove this line so default user language is chosen.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true); //TODO SOMEDAY: check if works online if language data is not downloaded
        }
        // Start the activity, the intent will be populated with the speech text
        if(intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
            catch(ActivityNotFoundException e) {
                log.e("Activity not found.", e);
            }
            catch(SecurityException e) {
                log.e("Security exception.", e);
            }
        }
        else {
            log.e("No activity found for this intent: %s", str(intent));
        }
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where we process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if(requestCode == SPEECH_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                float[] scores = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
//                log.v("Voice recognition results:");
//                for(int i = 0; i < results.size(); ++i) {
//                    String result = results.get(i);
//                    float score = scores == null ? -1 : scores[i];
//                    log.v("   %f:%s", score, result);
//                }
                String command = results.get(0).toLowerCase();

                //TODO real code: set fragment with command and enable super button there
                boolean ok = onCommand(command);
                if(ok) {
                    MIContract.CmdRecent.insert(this, command);
                }
            }
            else if(resultCode == RESULT_CANCELED) {
                log.d("Voice recognition cancelled.");
            }
            else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                log.e("Voice recognition: audio error.");
            }
            else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                log.e("Voice recognition: generic client error.");
            }
            else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                log.e("Voice recognition: network error.");
            }
            else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                log.e("Voice recognition: no match.");
            }
            else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                log.e("Voice recognition: server error.");
            }
            else {
                log.e("Voice recognition: error code: %d", resultCode);
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onStop() {
        if(!mSkipSavingToDb) {
            MIContract.RuleUser.clear(this);
            MIContract.RuleUser.save(this, MyCommands.RE_USER_GROUP.getRules());
        }
        super.onStop();
    }


    @Override public boolean onCommandCustom(@NonNull Map<String, String> command) {
        if(command.get("action").equals("listen")) {
            startSpeechRecognizer();
            return true;
        }
        if(command.get("action").equals("suicide")) {
            suicide();
            return true;
        }
        if(command.get("action").equals("resurrection")) {
            resurrection();
            return true;
        }

        return super.onCommandCustom(command);
    }

    protected void suicide() {
        System.exit(0);
    }

    protected void resurrection() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(MIActivity.this, 0, i, 0);
        AlarmManager manager = (AlarmManager) MIActivity.this.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pi);
        suicide();
    }

    private void resetAll() {
        new MaterialDialog.Builder(this)
                .title(R.string.reset_all)
                .content(R.string.are_you_sure_reset)
                .positiveText(R.string.reset)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        deleteDatabase(MIDBHelper.DATABASE_NAME);
                        mSkipSavingToDb = true;
                        resurrection();
                    }
                })
                .show();
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

        if(id == R.id.clear_recent) {
            MIContract.CmdRecent.clear(this);
            return true;
        }
        if(id == R.id.clear_example) {
            MIContract.CmdExample.clear(this);
            return true;
        }
        if(id == R.id.reset_all) {
            resetAll();
            return true;
        }
        if(id == R.id.action_whats_up) {
            log.i("[SNACK][SHORT]What's up mate?");
            return true;
        }

        return false;
    }
}

