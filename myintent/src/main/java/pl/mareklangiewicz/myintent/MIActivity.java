package pl.mareklangiewicz.myintent;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.mareklangiewicz.myactivities.MyActivity;
import pl.mareklangiewicz.mydrawables.MyLivingDrawable;
import pl.mareklangiewicz.mydrawables.MyMagicLinesDrawable;
import pl.mareklangiewicz.myutils.MyCommands;
import pl.mareklangiewicz.myutils.MyHttp;
import pl.mareklangiewicz.myviews.IMyNavigation;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static pl.mareklangiewicz.myutils.MyMathUtils.getRandomInt;
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
    private @Nullable TextToSpeech mTextToSpeech;
    private boolean mTTSReady = false;
    private @Nullable GoogleApiClient mClient;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getGlobalNavigation().inflateMenu(R.menu.mi_global);
        getGlobalNavigation().inflateHeader(R.layout.mi_header);
        if(BuildConfig.DEBUG) {
            Menu menu = getGlobalNavigation().getMenu();
            //noinspection ConstantConditions
            menu.findItem(R.id.ds_mode).setTitle("build type: " + BuildConfig.BUILD_TYPE);
            menu.findItem(R.id.ds_flavor).setTitle("build flavor: " + BuildConfig.FLAVOR);
            menu.findItem(R.id.ds_version_code).setTitle("version code: " + BuildConfig.VERSION_CODE);
            menu.findItem(R.id.ds_version_name).setTitle("version name: " + BuildConfig.VERSION_NAME);
            menu.findItem(R.id.ds_time_stamp).setTitle(String.format("build time: %tF %tT", BuildConfig.TIME_STAMP, BuildConfig.TIME_STAMP));
        }

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
                closeDrawersAndPostCommand("start custom action listen");
            }
        });

        //noinspection ConstantConditions
        mHomePageTextView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                play("data " + getResources().getString(R.string.mr_my_homepage));
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
                log.a("Can not load user rules from data base.");
            selectGlobalItem(R.id.mi_start);
        }

        mTextToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS && mTextToSpeech != null) {
                    mTextToSpeech.setLanguage(Locale.US);
                    mTTSReady = true;
                    log.d("Text to speech ready.");
                }
                else
                    log.d("Text to speech disabled.");
            }
        });

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override public void onIntent(@Nullable Intent intent) {

        super.onIntent(intent); // just for logging

        try {

            if(intent == null) {
                log.d("null intent received - ignoring");
                return;
            }

            if((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0)
                return;

            String cmd = intent.getStringExtra(MyCommands.EX_COMMAND);
            if(cmd != null) {
                closeDrawersAndPostCommand(cmd);
                return;
            }

            String action = intent.getAction();

            if(action == null)
                action = Intent.ACTION_MAIN;

            switch(action) {
                case Intent.ACTION_SEARCH:
                case SearchIntents.ACTION_SEARCH:
                    onSearchIntent(intent);
                    break;
                case Intent.ACTION_VIEW:
                    onUri(intent.getData());
                    break;
                case Intent.ACTION_MAIN:
                    if(!sGreeted) {
                        intro();
                        sGreeted = true;
                    }
                    break;
                default:
                    log.e("Unknown intent received: %s", str(intent));
                    break;
            }
        }
        catch(RuntimeException e) {
            log.a("Intent exception.", e);
        }

    }

    private void intro() {
        //log.i("Hello!");
        //FIXME SOMEDAY: I think we don't need any intro message. But maybe someday I'll change my mind.
    }


    @Override protected void onStart() {
        super.onStart();

        //noinspection ConstantConditions
        mClient.connect();
    }

    public void onUri(@Nullable Uri uri) {

        if(uri == null) {
            log.d("null uri received - ignoring");
            return;
        }

        String command = uri.getFragment();

        if(command == null) {
            log.d("URI with empty fragment received. Entering help..");
            log.v("uri: %s", str(uri));
            closeDrawersAndPostCommand("fragment .MIHelpFragment");
            return;

        }

        play(command);
    }


    static private class PlayCmdRunnable implements Runnable {

        private String mCommand;
        private WeakReference<MIActivity> mMIActivityWR;

        public PlayCmdRunnable(@NonNull String command, @NonNull MIActivity activity) {
            mCommand = command;
            mMIActivityWR = new WeakReference<>(activity);
        }

        @Override public void run() {

            MIActivity activity = mMIActivityWR.get();

            if(activity == null)
                return;

            boolean ok = activity.mLocalFragment != null && (activity.mLocalFragment instanceof MIStartFragment);

            if(ok) {
                ((MIStartFragment) activity.mLocalFragment).play(mCommand);
                return;
            }

            ok = activity.onCommand("fragment .MIStartFragment");
            ok = ok && activity.mLocalFragment != null && (activity.mLocalFragment instanceof MIStartFragment);

            if(ok)
                activity.play(mCommand); // IMPORTANT: we have to be asynchrous here again to let fragment initialize fully first.
            else
                activity.log.a("Can not select the \"Start\" section");
        }
    }


    /**
     * Sets current fragment to MIStartFragment and plays given command.
     * It will start the command if user doesn't press stop fast enough.
     * (it runs everything asynchronously - after closing drawers)
     */
    public void play(@Nullable final String command) {

        if(command == null) {
            log.d("null command received - ignoring");
            return;
        }

        closeDrawersAndPostRunnable(new PlayCmdRunnable(command, this));
    }


    private void onSearchIntent(Intent intent) {
        String command = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
        log.v("search: %s", command);
        play(command);
    }

    void startSpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.mi_ask_for_intent));
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
                log.e("Speech recognizer not found.", e);
            }
            catch(SecurityException e) {
                log.a("Security exception.", e);
            }
        }
        else {
            log.a("No activity found for this intent: %s", str(intent));
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

                play(command);
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

        updateWidgets();

        //noinspection ConstantConditions
        mClient.disconnect();

        if(!mSkipSavingToDb) {
            MIContract.RuleUser.clear(this);
            MIContract.RuleUser.save(this, MyCommands.RE_USER_GROUP.getRules());
        }
        super.onStop();
    }


    @Override public boolean onCommand(@Nullable String command) {
        boolean ok = super.onCommand(command);

        if(ok) {
            Action action = Action.newAction(Action.TYPE_VIEW, command,
                    Uri.parse("android-app://pl.mareklangiewicz.myintent/http/mareklangiewicz.pl/mi#" + command)
            );
            PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, action);
            result.setResultCallback(new ResultCallback<Status>() {
                @Override public void onResult(Status status) {
                    if(!status.isSuccess())
                        log.d("App Indexing API problem: %s", str(status));
                }
            });
        }

        return ok;
    }

    @Override public boolean onCommandCustom(@NonNull Map<String, String> command) {
        if(command.get("action").equals("listen")) {
            startSpeechRecognizer();
            return true;
        }
        if(command.get("action").equals("say")) {
            say(command.get("data"));
            return true;
        }
        if(command.get("action").equals("exit")) {
            finish();
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
        if(command.get("action").equals("weather")) {
            weather(
                    command.get("extra string appid"),
                    command.get("extra string city"),
                    command.get("extra string units"),
                    command.get("extra integer day")
            );
            return true;
        }

        return super.onCommandCustom(command);
    }

    /**
     * Reports a weather via logging and tts (if available)
     *
     * @param appid openweathermap api key (you should generate your own key on openweathermap website)
     * @param city  name of the city (may contain country code after comma (like: wroclaw,pl)
     * @param units default (null) are Kelvin, you can change it to "metric" (Celsius), or "imperial" (Fahrenheit)
     * @param day   a day number. Default (null) is "1" (today/now), set it to "2" for tomorrow etc... (up to 16)
     */
    public void weather(@NonNull String appid, @NonNull String city, @Nullable final String units, @Nullable String day) {
        int d = 1;
        if(day != null) {
            try {
                d = Integer.parseInt(day);
            }
            catch(NumberFormatException e) {
                log.e(e, "Bad day..");
                return;
            }
        }
        if(d < 1 || d > 16) {
            log.e("Bad day...");
            return;
        }

        final String tempunit = units == null ? "kelvins" : (units.equals("imperial") ? "\u00B0F" : "\u00B0C");
        final String speedunit = units == null ? "meters per second" : (units.equals("imperial") ? "miles per hour" : "meters per second");

        MyHttp.OpenWeatherMap.Service service = MyHttp.OpenWeatherMap.create();

        if(d == 1) { // we want current weather

            Call<MyHttp.OpenWeatherMap.Forecast> call = service.getWeatherByCity(appid, city, units);

            call.enqueue(new Callback<MyHttp.OpenWeatherMap.Forecast>() {
                @Override public void onResponse(Response<MyHttp.OpenWeatherMap.Forecast> response, Retrofit retrofit) {
                    MyHttp.OpenWeatherMap.Forecast forecast = response.body();
                    if(forecast.weather.length < 1) {
                        log.e("Weather error.");
                        return;
                    }
                    String report = String.format(Locale.US,
                            "Weather in %s: %s, temperature: %.0f %s, pressure: %.0f hectopascals, humidity: %d%%, wind speed: %.0f %s",
                            forecast.name,
                            forecast.weather[0].description,
                            forecast.main.temp,
                            tempunit,
                            forecast.main.pressure,
                            forecast.main.humidity,
                            forecast.wind.speed,
                            speedunit
                    );
                    say(report, TextToSpeech.QUEUE_ADD);
                }

                @Override public void onFailure(Throwable t) {
                    log.e(t, "Fetching weather failed.");
                }
            });

        }
        else { // we want forecast

            Call<MyHttp.OpenWeatherMap.DailyForecasts> call = service.getDailyForecastByCity(appid, city, d, units);

            call.enqueue(new Callback<MyHttp.OpenWeatherMap.DailyForecasts>() {
                @Override public void onResponse(Response<MyHttp.OpenWeatherMap.DailyForecasts> response, Retrofit retrofit) {
                    MyHttp.OpenWeatherMap.DailyForecasts forecasts = response.body();
                    say("Weather forcast for " + forecasts.city.name + ":");
                    for(int i = 1; i < forecasts.list.length; ++i) {
                        if(forecasts.list[i].weather.length < 1) {
                            log.e("Weather forecast error.");
                            return;
                        }
                        long time = forecasts.list[i].dt * 1000;
                        String report = String.format(Locale.US,
                                "On %tA: %s, %.0f %s.",
                                time,
                                forecasts.list[i].weather[0].description,
                                forecasts.list[i].temp.day,
                                tempunit
                        );
                        say(report, TextToSpeech.QUEUE_ADD);
                    }
                }

                @Override public void onFailure(Throwable t) {
                    log.e(t, "Fetching forecast failed.");
                }
            });

        }

    }

    private String remAuthor(String quote) {
        int idx = quote.indexOf("[");
        return idx == -1 ? quote : quote.substring(0, idx);
    }

    private String getRandomQuote(String[] quotes) {
        return remAuthor(quotes[getRandomInt(0, quotes.length - 1)]);
    }

    protected void say(String text) {
        say(text, TextToSpeech.QUEUE_FLUSH);
    }

    protected void say(String text, int queuemode) {
        long time = System.currentTimeMillis();
        if("weekday".equals(text)) {
            say(String.format(Locale.US, "%tA", time));
            return;
        }
        if("date".equals(text)) {
            say(String.format(Locale.US, "%tF", time));
            return;
        }
        if("time".equals(text)) {
            say(String.format(Locale.US, "%tl:%tM %tp", time, time, time));
            return;
        }
        if("something funny".equals(text)) {
            String[] quotes = ((MIApplication) getApplication()).FUNNY_QUOTES;
            say(getRandomQuote(quotes));
            return;
        }
        if("something smart".equals(text)) {
            String[] quotes = ((MIApplication) getApplication()).SMART_QUOTES;
            say(getRandomQuote(quotes));
            return;
        }
        if("something positive".equals(text)) {
            //TODO SOMEDAY http://www.brainyquote.com/quotes/topics/topic_positive.html
            log.i("Not implemented.");
            return;
        }
        if("something motivational".equals(text)) {
            //TODO SOMEDAY http://www.brainyquote.com/quotes/topics/topic_motivational.html
            log.i("Not implemented.");
            return;
        }
        log.w("[SNACK]" + text);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mTextToSpeech != null && mTTSReady) {
                mTextToSpeech.speak(text, queuemode, null, null);
            }
        }
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
                .title(R.string.mi_reset_all)
                .content(R.string.mi_are_you_sure_reset)
                .positiveText(R.string.mi_reset)
                .negativeText(R.string.mi_cancel)
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

        mClient = null;

        if(mTextToSpeech != null) {
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
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
        if(id == R.id.reset_all) {
            resetAll();
            return true;
        }

        return false;
    }

    private void updateWidgets() {
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        awm.notifyAppWidgetViewDataChanged(
                awm.getAppWidgetIds(
                        new ComponentName(this, RecentCommandsAppWidgetProvider.class)
                ),
                R.id.recent_commands_listview
        );
    }
}

