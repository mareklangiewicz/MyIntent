package pl.mareklangiewicz.myutils

import com.google.common.truth.Truth.assertThat
import org.junit.Ignore
import org.junit.Test
import java.lang.String.format
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Marek Langiewicz on 29.09.15.
 */
class MyCommandsTest {

    val log = MySystemLogger()

    @Ignore @Test fun testSomething() {
        val d1 = Date()
        val d2 = Date()
        log.i(d1.str)
        log.i(d2.str)
        log.i(format("%b", d1 == d2))

        val l1 = currentTimeMillis()
        val l2 = currentTimeMillis()
        log.i(format("%d", l1))
        log.i(format("%d", l2))
        log.i(format("%b", l1 == l2))
        assertThat(l1).isEqualTo(l2)
    }


    fun testRE(re: String, groupCount: Int, matchWholeString: Boolean, shouldMatch: Boolean, vararg strings: String) {
        val pattern = Pattern.compile(re)
        for (s in strings) {
            val matcher = pattern.matcher(s)
            val matches = if (matchWholeString) matcher.matches() else matcher.lookingAt()
            log.d("RE:\"$re\" ${if (matches) "matches" else "does not match"} ${if (matchWholeString) "whole" else "looking at"} text: \"$s\"")
            assertThat(matcher.groupCount()).isEqualTo(groupCount.toLong())
            assertThat(matches).isEqualTo(shouldMatch)
            if (matches) {
                for (i in 0..groupCount)
                    log.d(format("   group %d: \"%s\"", i, matcher.group(i)))
            }
        }
    }

    @Test fun testSimpleRE() {
        val re = "^activity (?=\\S*\\.)([_\\./a-zA-Z0-9]*)"
        testRE(re, 1, true, true,
                "activity .MyActivity",
                "activity .My/Activity")
        testRE(re, 1, true, false,
                "activity MyActivity")

        val arule = RE_ACTIVITY_GROUP.rules[0]
        val result = arule.apply("activity .MyActivity", log)
        log.w("Result: $result")

    }

    @Test fun testRE_ID() {
        testRE(RE_ID, 1, true, true,
                "fdjskl",
                "RR989dla",
                "_222z",
                "_")
        testRE(RE_ID, 1, true, false,
                " fdjskl",
                "a b",
                "9RR989dla",
                "")
    }

    @Test fun testRE_MULTI_ID() {

        testRE(RE_MULTI_ID, 4, true, true,
                "fdjskl/fdjks",
                "fdjskl.fdjks",
                "RR989dla",
                "a/b/c.d.e.f",
                ".RR989dla",
                "_222z.a.a.a.b.d",
                "._")
        testRE(RE_MULTI_ID, 4, true, false,
                " fdjskl",
                "aaa.",
                "",
                "/dla",
                "a b",
                ".2fjdskl",
                "")
    }

    @Test fun testRE_EXTRA_TYPE() {

        testRE(RE_EXTRA_TYPE, 1, true, true,
                "string",
                "integer",
                "short")
        testRE(RE_EXTRA_TYPE, 1, true, false,
                "object",
                "integer.string",
                "")
    }

    @Test fun testRE_KEYWORD() {

        testRE(RE_KEYWORD, 1, true, true,
                "start",
                "category",
                "extra")
        testRE(RE_KEYWORD, 1, true, false,
                "object",
                "action start",
                " ",
                "")
    }

    @Test fun testRE_VALUE() {
        // we will try to show that our RE_VALUE is not greedy..
        val txt = "yyyyRyyyyRyyyyRyyyyRyyyy"
        val gval = "(.*)"
        val lval= RE_VALUE
        testRE(gval + "R" + gval + "R" + gval, 3, false, true, txt)
        testRE(lval + "R" + lval + "R" + lval, 3, false, true, txt)
    }

    @Ignore @Test @Throws(Exception::class)
    fun testRE_END() {
        //TODO SOMEDAY
    }

    @Test fun testRE_SEGMENT() {

        testRE(RE_SEGMENT, 4, false, true,
                "extra bla ble bleee",
                "extra bla action x",
                "extra bla acton x",
                "extra ",
                "action xxx component a.b.c",
                "component a/b.c.d bleee",
                "component a/b.c.d start service")
        testRE(RE_SEGMENT, 4, false, false,
                "extrrra bla ble bleee",
                "extra")
    }

    @Test fun testRE_EXTRA_ELEM() {

        testRE(RE_EXTRA_ELEM, 7, true, true,
                "integer bla ble bleee",
                "string action x",
                "integer val 7",
                "float f 222.333")
        testRE(RE_EXTRA_ELEM, 7, true, false,
                "extrrra bla ble bleee",
                "extra integer val 7",
                "integer 7",
                "")
    }

    @Test fun testRE_EXTRA_ELEM_TYPE_AND_KEY() {

        testRE(RE_EXTRA_ELEM_TYPE_AND_KEY, 6, true, true,
                "integer bla",
                "string kkeeyy",
                "integer val",
                "float flf")
        testRE(RE_EXTRA_ELEM_TYPE_AND_KEY, 6, true, false,
                "integer bla ble",
                "extrrra bla ble bleee",
                "integer val 7",
                "integer 7",
                "")
    }


    @Test fun testRE_RULES() {
        log.i(RE_RULES[0].str)
        log.i(RE_RULES[1].str)
        log.i(RE_RULES[2].str)
        log.i(RE_RULES[3].str)
        log.i(RE_RULES[4].str)
        log.i(RE_RULES[5].str)
        log.i(RE_RULES[6].str)
    }


    fun testApplyAllRERules(input: String, expected: String) {
        val command = RE_RULES.applyAllGroups(input, log)
        assertThat(command).isEqualTo(expected)
    }

    fun multiTestApplyAllRERules(inputs: List<String>, expected: String) {
        for (input in inputs)
            testApplyAllRERules(input, expected)
    }


    @Test fun testAlarmRules() {

        var expected = "action android.intent.action.SET_ALARM extra integer android.intent.extra.alarm.HOUR 1 extra integer android.intent.extra.alarm.MINUTES 0"
        var commands = Arrays.asList(
                "set alarm at 1 0",
                "set an alarm at 1 0",
                "set an alarm;for 1;0",
                "set alarm to 1:0",
                "set;an;alarm;to;1;0")

        multiTestApplyAllRERules(commands, expected)


        expected = "action android.intent.action.SET_ALARM extra integer android.intent.extra.alarm.HOUR 2 extra integer android.intent.extra.alarm.MINUTES 59 extra boolean android.intent.extra.alarm.SKIP_UI true"
        commands = Arrays.asList(
                "set alarm at 2 59 quickly",
                "set an alarm;for 2;59 quickly",
                "set;an;alarm;to;2;59;quickly")

        multiTestApplyAllRERules(commands, expected)

        expected = "action android.intent.action.SET_ALARM extra integer android.intent.extra.alarm.HOUR 2 extra integer android.intent.extra.alarm.MINUTES 59 extra string android.intent.extra.alarm.MESSAGE bla blee"
        commands = Arrays.asList(
                "set alarm at 2 59 with message bla blee",
                "set an alarm;for 2;59 with message bla blee",
                "set;an;alarm;to;2;59; with;message;bla;blee")

        multiTestApplyAllRERules(commands, expected)

    }

    @Test fun testTimerRules() {
        val expected = "action android.intent.action.SET_TIMER extra integer android.intent.extra.alarm.LENGTH 30"
        val commands = Arrays.asList(
                "set timer at 30",
                "set a timer for 30",
                "set;a;timer;for;30",
                "set timer to 30 seconds")

        multiTestApplyAllRERules(commands, expected)

    }

    @Test fun testActivityRules() {

        testApplyAllRERules("start activity component MyActivity", "start activity component MyActivity")
        testApplyAllRERules("start activity component bla.MyActivity", "start activity component bla.MyActivity")
        testApplyAllRERules("start activity component .MyActivity", "start activity component .MyActivity")
        testApplyAllRERules("start activity MyActivity", "start activity MyActivity") //WARNING: we do NOT recognize this kind of shortcut!
        testApplyAllRERules("activity component MyActivity", "start activity component MyActivity")
        testApplyAllRERules("activity data http://blabla", "start activity data http://blabla")
        testApplyAllRERules("activity .MyActivity", "start activity component .MyActivity")
        testApplyAllRERules("activity b.la action view", "start activity component b.la action android.intent.action.VIEW")
        testApplyAllRERules("activity action view", "start activity action android.intent.action.VIEW")

    }


    @Test fun testFragmentRules() {

        testApplyAllRERules("start fragment component a.b.c.MyXFragment", "start fragment component a.b.c.MyXFragment")
        testApplyAllRERules("fragment a.b.c.MyXFragment", "start fragment component a.b.c.MyXFragment")
        testApplyAllRERules("fragment .bla action view", "start fragment component .bla action android.intent.action.VIEW")

    }

    @Test fun testMyCommandParsing() {
        val commands = Arrays.asList(
                "action b data d;",
                "action x.y.z",
                "extra integer a.b.c.d 667",
                "action send data http://blabla.blabla.com/path/to/something extra boolean checked true",
                "action a.b.X.Y extra string bla blabla extra integer satan 666 task hell")
        for (command in commands) {
            log.i("############### parsing command: $command")
            val mycmd = MyCommand(command, RE_RULES, log)
            log.i("############### result: ${mycmd.str}")
        }

    }



        @Test(expected = IllegalArgumentException::class)
        fun testMyCommandParsingError() {
            val extra = "extra untiger android.intent.extra.alarm.LENGTH 5"
            val mycmd = MyCommand(extra, RE_RULES, log)
            log.w(mycmd.str)
        }


}