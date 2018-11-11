package pl.mareklangiewicz.myutils

import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Marek Langiewicz on 29.03.16.
 */

@RunWith(AndroidJUnit4::class)
class MyCommandsAndroidTest {

    val log = MySystemLogger()

    @Test fun testMyCommandToExtrasBundle() {
        log.w(RE_EXTRA_ELEM)
        val extra = "extra integer android.intent.extra.alarm.LENGTH 5 extra string bla ble"
        val mycmd = MyCommand(extra, RE_RULES, log)
        val bundle = mycmd.toExtrasBundle()
        log.w(bundle.toVeryLongStr())
    }

    @Test fun testMyExampleCommandToExtrasBundle() {
        val mycmd = MyCommand(DEFAULT_EXAMPLE_COMMANDS[6], RE_RULES, log)
        val bundle = mycmd.toExtrasBundle()
        log.w(bundle.toVeryLongStr())
    }

    @Test fun testToIntent() {
        val commands = DEFAULT_EXAMPLE_COMMANDS
        for(cmd in commands) {
            log.w("##### Input command: $cmd")
            val mycmd = MyCommand(cmd, RE_RULES, log)
            log.w("##    Parsed command: ${mycmd.str}")
            val intent = mycmd.toIntent()
            log.w("##### Output intent: ${intent.str}")
        }
    }

    @Test fun testToInvalidIntent() {
        val commands = listOf(
                "cxzcxz",
                "staaaart nothing",
                "start nothing bla extra xxx 4",
                "waake me up at 10",
                "wake me up at 10 ee start",
                "action"
        )
        for (cmd in commands) {
            log.w("##### Bad input command: $cmd")
            try {
                val mycmd = MyCommand(cmd, RE_RULES, log)
                throw IllegalStateException("We should not get here. Incorectly parsed command is: $mycmd")
            } catch(e: IllegalArgumentException) {
                log.w("##### Expected error: $e")
            }
        }
    }
}