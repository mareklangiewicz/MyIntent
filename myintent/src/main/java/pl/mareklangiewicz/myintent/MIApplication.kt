package pl.mareklangiewicz.myintent

import android.app.Application

import pl.mareklangiewicz.myloggers.MyAndroLogger
import pl.mareklangiewicz.myloggers.*
import pl.mareklangiewicz.myutils.MyLogLevel

val APP_START_TIME = System.currentTimeMillis()

/**
 * Created by Marek Langiewicz on 20.10.15.
 */
class MIApplication : Application() {


    init {

        if(!BuildConfig.DEBUG)
            MY_DEFAULT_ANDRO_LOGGER = MyAndroLogger(false)
            // FIXME LATER: this is a hack. TODO LATER: Delete this MY_DE.. global and use Dagger2 to provide appropriate loggers

        MY_DEFAULT_ANDRO_LOGGER.history.level = MyLogLevel.INFO
    }

    lateinit var FUNNY_QUOTES: Array<String>
    lateinit var SMART_QUOTES: Array<String>

    override fun onCreate() {
        FUNNY_QUOTES = resources.getStringArray(R.array.mr_funny_quotes)
        SMART_QUOTES = resources.getStringArray(R.array.mr_smart_quotes)
        super.onCreate()
    }
}
