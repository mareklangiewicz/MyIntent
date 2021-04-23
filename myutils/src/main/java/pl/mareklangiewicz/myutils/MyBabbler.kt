package pl.mareklangiewicz.myutils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import pl.mareklangiewicz.upue.Pushee
import java.util.*

/**
 * Created by Marek Langiewicz on 12.03.16.
 */
class MyBabbler(
        context: Context,
        val log: Pushee<MyLogEntry>,
        val funnyQuotes: Array<String> = arrayOf("No."),
        val smartQuotes: Array<String> = arrayOf("No.")
) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(context, this)

    private var ttsready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            ttsready = true
            log.d("Text to speech ready.")
        } else
            log.d("Text to speech disabled.")
    }

    @TargetApi(21)
    fun say(text: String?, flush: Boolean = true) {
        val time = System.currentTimeMillis()
        if (text == null) {
            log.w("null")
            return
        }
        if ("weekday" == text) {
            say("%tA".format(Locale.US, time))
            return
        }
        if ("date" == text) {
            say("%tF".format(Locale.US, time))
            return
        }
        if ("time" == text) {
            say("%tl:%tM %tp".format(Locale.US, time, time, time))
            return
        }
        if ("something funny" == text) {
            say(getRandomQuote(funnyQuotes))
            return
        }
        if ("something smart" == text) {
            say(getRandomQuote(smartQuotes))
            return
        }
        if ("something positive" == text) {
            //TODO SOMEDAY http://www.brainyquote.com/quotes/topics/topic_positive.html
            say("Not implemented.")
            return
        }
        if ("something motivational" == text) {
            //TODO SOMEDAY http://www.brainyquote.com/quotes/topics/topic_motivational.html
            say("Not implemented.")
            return
        }

        log.w("[SNACK]" + text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ttsready) {
                tts.speak(text, if(flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, null)
            }
        }
    }

    /**
     * This should be called on: onDestroy
     */
    fun shutdown() {
        ttsready = false
        tts.shutdown()
    }

    private fun remAuthor(quote: String): String {
        val idx = quote.indexOf("[")
        return if (idx == -1) quote else quote.substring(0, idx)
    }

    private fun getRandomQuote(quotes: Array<String>): String {
        return remAuthor(quotes[RANDOM.nextInt(0, quotes.size - 1)])
    }


}
