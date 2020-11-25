package pl.mareklangiewicz.myintent

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.SearchManager
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.actions.SearchIntents
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.AppIndex
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.mi_header.view.*
import pl.mareklangiewicz.myactivities.MyActivity
import pl.mareklangiewicz.myintent.MIContract.RuleUser
import pl.mareklangiewicz.myutils.*
import pl.mareklangiewicz.myutils.myhttp.OpenWeatherMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        val nav = gnav!!

        nav.menuId = R.menu.mi_global
        nav.headerId = R.layout.mi_header

        val header = nav.headerObj!!

        animations = MIActivityAnimations(
                header.mi_gh_tv_logo,
                header.mi_gh_tv_home_page,
                header.mi_gh_v_magic_underline,
                lcolor = 822083583,
                lwidth = dp2px(4f)
        )

        header.mi_gh_iv_logo!!.setOnClickListener { execute("start custom action listen") }

        header.mi_gh_tv_home_page!!.setOnClickListener { play("data " + resources.getString(R.string.mr_my_homepage)) }

        nav.items { // we ignore returned subscription - navigation will live as long as activity
            when(it) {
                R.id.mi_clear_recent -> {
                    MIContract.CmdRecent.clear(this)
                    (fgmt as? MIRecentCmdListFragment)?.apply { refresh() }
                }
                R.id.mi_reset_all -> resetAll()
            }
        }

        if (savedInstanceState == null) {

            val rules = RE_USER_GROUP.rules
            rules.clear()
            val ok = RuleUser.load(this, rules)
            if (!ok)
                log.a("Can not load user rules from data base.")
            nav.setCheckedItem(R.id.mi_start, true)
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

            val cmd = intent.getStringExtra(EX_COMMAND)
            if (cmd != null) {
                if(cmd == "start custom action listen")
                    execute(cmd)
                else
                    play(cmd)
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
            log.a("Intent exception.", "ML", e)
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
            execute("fragment .MIHelpFragment")
            return

        }

        play(command)
    }

    /**
     * Sets current fragment to MIStartFragment and plays given command.
     * It will start the command if user doesn't press stop fast enough.
     * (it runs everything asynchronously - after closing drawers)
     */
    fun play(command: String): Unit = closeDrawersAnd {
        val f = fgmt
        if(f is MIStartFragment) f.play(command)
        else {
            execute("fragment .MIStartFragment")
            play(command) // IMPORTANT: we have to be asynchrous here again to let fragment initialize fully first.
        }
    }

    private fun onSearchIntent(intent: Intent) {
        val command = intent.getStringExtra(SearchManager.QUERY)?.toLowerCase()
        log.v("search: $command")
        command?.let { play(it) }
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
                log.e("Speech recognizer not found.", "ML", e)
            } catch (e: SecurityException) {
                log.a("Security exception.", "ML", e)
            }

        } else {
            log.a("No activity found for this intent: ${intent.str}")
        }
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where we process the intent and extract the speech text from the intent.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode != SPEECH_REQUEST_CODE)
            return super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                val results = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!!
                //                float[] scores = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                //                log.v("Voice recognition results:");
                //                for(int i = 0; i < results.size(); ++i) {
                //                    String result = results.get(i);
                //                    float score = scores == null ? -1 : scores[i];
                //                    log.v("   %f:%s", score, result);
                //                }
                val command = results[0].toLowerCase(Locale.US)

                play(command)
            }
            Activity.RESULT_CANCELED -> log.d("Voice recognition cancelled.")
            RecognizerIntent.RESULT_AUDIO_ERROR -> log.e("Voice recognition: audio error.")
            RecognizerIntent.RESULT_CLIENT_ERROR -> log.e("Voice recognition: generic client error.")
            RecognizerIntent.RESULT_NETWORK_ERROR -> log.e("Voice recognition: network error.")
            RecognizerIntent.RESULT_NO_MATCH -> log.e("Voice recognition: no match.")
            RecognizerIntent.RESULT_SERVER_ERROR -> log.e("Voice recognition: server error.")
            else -> log.e("Voice recognition: error code: $resultCode")
        }
    }

    override fun onStop() {

        updateWidgets()

        gapi.disconnect()

        if (dbsave) {
            RuleUser.clear(this)
            RuleUser.save(this, RE_USER_GROUP.rules)
        }
        super.onStop()
    }


    // TODO NOW: invoke it for every successfully executed command
    private fun indexCommand(command: String) {
        val action = Action.newAction(Action.TYPE_VIEW, command, Uri.parse("android-app://pl.mareklangiewicz.myintent/http/mareklangiewicz.pl/mi#$command"))
        val result = AppIndex.AppIndexApi.start(gapi, action)
        result.setResultCallback { if (!it.isSuccess) log.d("App Indexing API problem: ${it.str}") }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCommandCustom(command: MyCommand) {
        when(command["action"]) {
            "listen" -> startSpeechRecognizer()
            "say" -> babbler.say(command["data"])
            "orientation" -> when(command["data"]) {
                    "portrait" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    "landscape" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    "unspecified" -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            "exit" -> finish()
            "suicide" -> suicide()
            "resurrection" -> resurrection()
            "weather" -> {
                val appid = command["extra string appid"]
                if (appid == null) {
                    log.e("No OpenWeatherMap Api Key provided.")
                    return
                }
                val city = command["extra string city"]
                if (city == null) {
                    log.e("No city provided.")
                    return
                }
                weather(
                        appid,
                        city,
                        command["extra string units"] ?: "kelvins",
                        command["extra integer day"] ?: "1")
            }
            else -> super.onCommandCustom(command)
        }
    }

    /**
     * Reports a weather via logging and tts (if available)
     * @param appid openweathermap api key (you should generate your own key on openweathermap website)
     * @param city  name of the city (may contain country code after comma (like: wroclaw,pl)
     * @param units default are "kelvins", you can change it to "metric" (Celsius), or "imperial" (Fahrenheit)
     * @param day   a day number. Default is "1" (today/now), set it to "2" for tomorrow etc... (up to 16)
     * TODO SOMEDAY: remove all !! operators in this function...
     */
    fun weather(appid: String, city: String, units: String = "kelvins", day: String = "1") {
        val d: Int
        try {
            d = Integer.parseInt(day)
        } catch (e: NumberFormatException) {
            log.e("Bad day..", "ML", e)
            return
        }

        if (d < 1 || d > 16) {
            log.e("Bad day...")
            return
        }

        val tempunit = if (units == "kelvins") "kelvins" else (if (units == "imperial") "\u00B0F" else "\u00B0C")
        val speedunit = if (units == "kelvins") "meters per second" else (if (units == "imperial") "miles per hour" else "meters per second")

        if (d == 1) {
            // we want current weather

            val call = OpenWeatherMap.service.getWeatherByCityCall(appid, city, units)

            call.enqueue(object : Callback<OpenWeatherMap.Forecast> {
                override fun onResponse(call: Call<OpenWeatherMap.Forecast>?, response: Response<OpenWeatherMap.Forecast>) {
                    if(!response.isSuccessful) {
                        log.e("Fetching weather failed: code: ${response.code()}; message: ${response.message()}")
                        return
                    }
                    val forecast = response.body()
                    if (forecast === null || forecast.weather === null) {
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

                override fun onFailure(call: Call<OpenWeatherMap.Forecast>?, t: Throwable?) {
                    log.e("Fetching weather failed.", "ML", t)
                }
            })

        } else {
            // we want forecast

            val call = OpenWeatherMap.service.getDailyForecastByCityCall(appid, city, d.toLong(), units)

            call.enqueue(object : Callback<OpenWeatherMap.DailyForecasts> {
                override fun onResponse(call: Call<OpenWeatherMap.DailyForecasts>?, response: Response<OpenWeatherMap.DailyForecasts>) {
                    if(!response.isSuccessful) {
                        log.e("Fetching weather failed: code: ${response.code()}; message: ${response.message()}")
                        return
                    }
                    val forecasts = response.body()
                    if (forecasts === null) {
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

                override fun onFailure(call: Call<OpenWeatherMap.DailyForecasts>?, t: Throwable?) {
                    log.e("Fetching forecast failed.", "ML", t)
                }
            })

        }

    }

    private fun suicide() {
        System.exit(0)
    }

    private fun resurrection() {
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)!!
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(this@MIActivity, 0, i, 0)
        val manager = this@MIActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pi)
        suicide()
    }

    private fun resetAll() {
        MaterialDialog(this)
                .title(R.string.mi_reset_all)
                .message(R.string.mi_are_you_sure_reset)
                .positiveButton(R.string.mi_reset) {
                    deleteDatabase(MIDBHelper.DATABASE_NAME)
                    dbsave = false
                    resurrection()
                }
                .negativeButton(R.string.mi_cancel)
                .show()
    }

    override fun onDestroy() {
        babbler.shutdown()
        super.onDestroy()
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        super.onDrawerSlide(drawerView, slideOffset)
        if (drawerView !== gnav)
            return
        animations.onGlobalDrawerSlide(slideOffset)
    }


    override fun onDrawerOpened(drawerView: View) {
        super.onDrawerOpened(drawerView)
        if (drawerView !== gnav)
            return
        animations.onGlobalDrawerOpened()
    }

    override fun onDrawerClosed(drawerView: View) {
        super.onDrawerClosed(drawerView)
        if (drawerView !== gnav)
            return
        animations.onGlobalDrawerClosed()
    }

    private fun updateWidgets() {
        val awm = AppWidgetManager.getInstance(this)
        awm.notifyAppWidgetViewDataChanged(
                awm.getAppWidgetIds(
                        ComponentName(this, RecentCommandsAWProvider::class.java)),
                R.id.recent_commands_listview)
    }
}

