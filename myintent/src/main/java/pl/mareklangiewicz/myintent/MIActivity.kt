package pl.mareklangiewicz.myintent

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.annotation.IdRes
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.actions.SearchIntents
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.AppIndex
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.mi_header.view.*
import pl.mareklangiewicz.myactivities.MyActivity
import pl.mareklangiewicz.myutils.MyBabbler
import pl.mareklangiewicz.myutils.MyCommands
import pl.mareklangiewicz.myutils.myhttp.OWMDailyForecasts
import pl.mareklangiewicz.myutils.myhttp.OWMForecast
import pl.mareklangiewicz.myutils.myhttp.createOWMService
import pl.mareklangiewicz.myutils.str
import pl.mareklangiewicz.myviews.IMyNavigation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Marek Langiewicz on 02.10.15.
 * My Intent main activity.
 */


class MIActivity : MyActivity() {

    private val SPEECH_REQUEST_CODE = 0

    private lateinit var animations: MIActivityAnimations
    private lateinit var gapi: GoogleApiClient
    private var dbsave = true

    private lateinit var babbler: MyBabbler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nav = globalNavigation!!

        nav.inflateMenu(R.menu.mi_global)
        nav.inflateHeader(R.layout.mi_header)

        if (BuildConfig.DEBUG) {
            val menu = nav.menu!!
            menu.findItem(R.id.ds_mode).title = "build type: " + BuildConfig.BUILD_TYPE
            menu.findItem(R.id.ds_flavor).title = "build flavor: " + BuildConfig.FLAVOR
            menu.findItem(R.id.ds_version_code).title = "version code: " + BuildConfig.VERSION_CODE
            menu.findItem(R.id.ds_version_name).title = "version name: " + BuildConfig.VERSION_NAME
            menu.findItem(R.id.ds_time_stamp).title = "build time: %tF %tT".format(BuildConfig.TIME_STAMP, BuildConfig.TIME_STAMP)
        }

        val header = nav.header!!

        animations = MIActivityAnimations(
                header.mi_gh_tv_logo,
                header.mi_gh_tv_home_page,
                header.mi_gh_v_magic_underline,
                lcolor = 822083583,
                lwidth = dp2px(4f)
        )

        header.mi_gh_iv_logo!!.setOnClickListener { closeDrawersAndPostCommand("start custom action listen") }

        header.mi_gh_tv_home_page!!.setOnClickListener { play("data " + resources.getString(R.string.mr_my_homepage)) }

        if (savedInstanceState == null) {

            val rules = MyCommands.RE_USER_GROUP.rules
            rules.clear()
            val ok = MIContract.RuleUser.load(this, rules)
            if (!ok)
                log.a("Can not load user rules from data base.")
            selectGlobalItem(R.id.mi_start)
        }

        val app = application as MIApplication

        babbler = MyBabbler(applicationContext, log, app.FUNNY_QUOTES, app.SMART_QUOTES)

        gapi = GoogleApiClient.Builder(this).addApi(AppIndex.API).build()
    }

    override fun onIntent(intent: Intent?) {

        super.onIntent(intent) // just for logging

        try {

            if (intent == null) {
                log.d("null intent received - ignoring")
                return
            }

            if ((intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0)
                return

            val cmd = intent.getStringExtra(MyCommands.EX_COMMAND)
            if (cmd != null) {
                closeDrawersAndPostCommand(cmd)
                return
            }

            val action = intent.action ?: Intent.ACTION_MAIN
            when (action) {
                Intent.ACTION_SEARCH, SearchIntents.ACTION_SEARCH -> onSearchIntent(intent)
                Intent.ACTION_VIEW -> onUri(intent.data)
                Intent.ACTION_MAIN -> Unit
                else -> log.e("Unknown intent received: ${intent.str}")
            }
        } catch (e: RuntimeException) {
            log.a("Intent exception.", e)
        }

    }

    override fun onStart() {
        super.onStart()
        gapi.connect()
    }

    fun onUri(uri: Uri?) {

        if (uri == null) {
            log.d("null uri received - ignoring")
            return
        }

        val command = uri.fragment

        if (command == null) {
            log.d("URI with empty fragment received. Entering help..")
            log.v("uri: ${uri.str}")
            closeDrawersAndPostCommand("fragment .MIHelpFragment")
            return

        }

        play(command)
    }


    private class PlayCmdRunnable(private val mCommand: String, activity: MIActivity) : Runnable {
        private val mMIActivityWR: WeakReference<MIActivity>

        init {
            mMIActivityWR = WeakReference(activity)
        }

        override fun run() {

            val activity = mMIActivityWR.get() ?: return

            var ok = activity.mLocalFragment != null && (activity.mLocalFragment is MIStartFragment)

            if (ok) {
                (activity.mLocalFragment as MIStartFragment).play(mCommand)
                return
            }

            ok = activity.onCommand("fragment .MIStartFragment")
            ok = ok && activity.mLocalFragment != null && (activity.mLocalFragment is MIStartFragment)

            if (ok)
                activity.play(mCommand) // IMPORTANT: we have to be asynchrous here again to let fragment initialize fully first.
            else
                activity.log.a("Can not select the \"Start\" section")
        }
    }


    /**
     * Sets current fragment to MIStartFragment and plays given command.
     * It will start the command if user doesn't press stop fast enough.
     * (it runs everything asynchronously - after closing drawers)
     */
    fun play(command: String?) {

        if (command == null) {
            log.d("null command received - ignoring")
            return
        }

        closeDrawersAndPostRunnable(PlayCmdRunnable(command, this))
    }


    private fun onSearchIntent(intent: Intent) {
        val command = intent.getStringExtra(SearchManager.QUERY).toLowerCase()
        log.v("search: $command")
        play(command)
    }

    internal fun startSpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.mi_ask_for_intent))
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US") //TODO SOMEDAY: remove this line so default user language is chosen.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true) //TODO SOMEDAY: check if works online if language data is not downloaded
        }
        // Start the activity, the intent will be populated with the speech text
        if (intent.resolveActivity(packageManager) != null) {
            try {
                startActivityForResult(intent, SPEECH_REQUEST_CODE)
            } catch (e: ActivityNotFoundException) {
                log.e("Speech recognizer not found.", e)
            } catch (e: SecurityException) {
                log.a("Security exception.", e)
            }

        } else {
            log.a("No activity found for this intent: ${intent.str}")
        }
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where we process the intent and extract the speech text from the intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val results = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                //                float[] scores = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                //                log.v("Voice recognition results:");
                //                for(int i = 0; i < results.size(); ++i) {
                //                    String result = results.get(i);
                //                    float score = scores == null ? -1 : scores[i];
                //                    log.v("   %f:%s", score, result);
                //                }
                val command = results[0].toLowerCase()

                play(command)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                log.d("Voice recognition cancelled.")
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                log.e("Voice recognition: audio error.")
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                log.e("Voice recognition: generic client error.")
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                log.e("Voice recognition: network error.")
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                log.e("Voice recognition: no match.")
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                log.e("Voice recognition: server error.")
            } else {
                log.e("Voice recognition: error code: $resultCode")
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {

        updateWidgets()

        gapi.disconnect()

        if (dbsave) {
            MIContract.RuleUser.clear(this)
            MIContract.RuleUser.save(this, MyCommands.RE_USER_GROUP.rules)
        }
        super.onStop()
    }


    override fun onCommand(command: String?): Boolean {
        val ok = super.onCommand(command)

        if (ok) {
            val action = Action.newAction(Action.TYPE_VIEW, command,
                    Uri.parse("android-app://pl.mareklangiewicz.myintent/http/mareklangiewicz.pl/mi#" + command))
            val result = AppIndex.AppIndexApi.start(gapi, action)
            result.setResultCallback { status ->
                if (!status.isSuccess)
                    log.d("App Indexing API problem: ${status.str}")
            }
        }

        return ok
    }

    override fun onCommandCustom(command: Map<String, String>): Boolean {
        if (command["action"] == "listen") {
            startSpeechRecognizer()
            return true
        }
        if (command["action"] == "say") {
            babbler.say(command["data"])
            return true
        }
        if (command["action"] == "exit") {
            finish()
            return true
        }
        if (command["action"] == "suicide") {
            suicide()
            return true
        }
        if (command["action"] == "resurrection") {
            resurrection()
            return true
        }
        if (command["action"] == "weather") {
            val appid = command["extra string appid"];
            if (appid == null) {
                log.e("No OpenWeatherMap Api Key provided.");
                return false
            }
            val city = command["extra string city"];
            if (city == null) {
                log.e("No city provided.");
                return false
            }
            weather(
                    appid,
                    city,
                    command["extra string units"] ?: "kelvins",
                    command["extra integer day"] ?: "1")
            return true
        }

        return super.onCommandCustom(command)
    }

    /**
     * Reports a weather via logging and tts (if available)
     *
     * @param appid openweathermap api key (you should generate your own key on openweathermap website)
     * *
     * @param city  name of the city (may contain country code after comma (like: wroclaw,pl)
     * *
     * @param units default are "kelvins", you can change it to "metric" (Celsius), or "imperial" (Fahrenheit)
     * *
     * @param day   a day number. Default is "1" (today/now), set it to "2" for tomorrow etc... (up to 16)
     *
     * TODO SOMEDAY: remove all !! operators in this function...
     */
    fun weather(appid: String, city: String, units: String = "kelvins", day: String = "1") {
        val d: Int
        try {
            d = Integer.parseInt(day)
        } catch (e: NumberFormatException) {
            log.e("Bad day..", e)
            return
        }

        if (d < 1 || d > 16) {
            log.e("Bad day...")
            return
        }

        val tempunit = if (units == "kelvins") "kelvins" else (if (units == "imperial") "\u00B0F" else "\u00B0C")
        val speedunit = if (units == "kelvins") "meters per second" else (if (units == "imperial") "miles per hour" else "meters per second")

        val service = createOWMService()

        if (d == 1) {
            // we want current weather

            val call = service.getWeatherByCity(appid, city, units)

            call.enqueue(object : Callback<OWMForecast> {
                override fun onResponse(call: Call<OWMForecast>?, response: Response<OWMForecast>?) {
                    val forecast = response?.body()
                    if(forecast === null || forecast.weather === null) {
                        log.e("Fetching weather failed: empty response.")
                        return
                    }
                    if (forecast.weather!!.size < 1) {
                        log.e("Weather error.")
                        return
                    }
                    val report = "Weather in %s: %s, temperature: %.0f %s, pressure: %.0f hectopascals, humidity: %d%%, wind speed: %.0f %s".format(
                            Locale.US,
                            forecast.name,
                            forecast.weather!![0].description,
                            forecast.main!!.temp,
                            tempunit,
                            forecast.main!!.pressure,
                            forecast.main!!.humidity,
                            forecast.wind!!.speed,
                            speedunit
                    )
                    babbler.say(report, false)
                }

                override fun onFailure(call: Call<OWMForecast>?, t: Throwable?) {
                    log.e("Fetching weather failed.", t)
                }
            })

        } else {
            // we want forecast

            val call = service.getDailyForecastByCity(appid, city, d.toLong(), units)

            call.enqueue(object : Callback<OWMDailyForecasts> {
                override fun onResponse(call: Call<OWMDailyForecasts>?, response: Response<OWMDailyForecasts>?) {
                    val forecasts = response?.body()
                    if(forecasts === null) {
                        log.e("Fetching weather failed: empty response.")
                        return
                    }
                    babbler.say("Weather forcast for " + forecasts.city!!.name + ":")
                    for (i in 1..forecasts.list!!.size - 1) {
                        if (forecasts.list!![i].weather!!.size < 1) {
                            log.e("Weather forecast error.")
                            return
                        }
                        val time = forecasts.list!![i].dt * 1000
                        val report = "On %tA: %s, %.0f %s.".format(
                                Locale.US, time,
                                forecasts.list!![i].weather!![0].description,
                                forecasts.list!![i].temp!!.day, tempunit
                        )
                        babbler.say(report, false)
                    }
                }

                override fun onFailure(call: Call<OWMDailyForecasts>?, t: Throwable?) {
                    log.e("Fetching forecast failed.", t)
                }
            })

        }

    }

    protected fun suicide() {
        System.exit(0)
    }

    protected fun resurrection() {
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(this@MIActivity, 0, i, 0)
        val manager = this@MIActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pi)
        suicide()
    }

    private fun resetAll() {
        MaterialDialog.Builder(this)
                .title(R.string.mi_reset_all)
                .content(R.string.mi_are_you_sure_reset)
                .positiveText(R.string.mi_reset)
                .negativeText(R.string.mi_cancel)
                .onPositive(
                        { dialog, action ->
                            deleteDatabase(MIDBHelper.DATABASE_NAME)
                            dbsave = false
                            resurrection()
                        }
                )
                .show()
    }

    override fun onDestroy() {
        babbler.shutdown()
        super.onDestroy()
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        super.onDrawerSlide(drawerView, slideOffset)
        if (drawerView !== mGlobalNavigationView)
            return
        animations.onGlobalDrawerSlide(slideOffset)
    }


    override fun onDrawerOpened(drawerView: View) {
        super.onDrawerOpened(drawerView)
        if (drawerView !== mGlobalNavigationView)
            return
        animations.onGlobalDrawerOpened()
    }

    override fun onDrawerClosed(drawerView: View) {
        super.onDrawerClosed(drawerView)
        if (drawerView !== mGlobalNavigationView)
            return
        animations.onGlobalDrawerClosed()
    }

    override fun onItemSelected(nav: IMyNavigation, item: MenuItem): Boolean {

        val done = super.onItemSelected(nav, item)

        if (done)
            return true

        @IdRes val id = item.itemId

        if (id == R.id.clear_recent) {
            MIContract.CmdRecent.clear(this)
            return true
        }
        if (id == R.id.reset_all) {
            resetAll()
            return true
        }

        return false
    }

    private fun updateWidgets() {
        val awm = AppWidgetManager.getInstance(this)
        awm.notifyAppWidgetViewDataChanged(
                awm.getAppWidgetIds(
                        ComponentName(this, RecentCommandsAppWidgetProvider::class.java)),
                R.id.recent_commands_listview)
    }

}

