package pl.mareklangiewicz.myutils;

import com.google.common.truth.Expect;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static pl.mareklangiewicz.myutils.MyCommands.RERule;
import static pl.mareklangiewicz.myutils.MyCommands.RE_ACTIVITY_GROUP;
import static pl.mareklangiewicz.myutils.MyCommands.parseCommand;
import static pl.mareklangiewicz.myutils.MyTextUtilsKt.str;

/**
 * Created by Marek Langiewicz on 29.09.15.
 *
 */
public class MyCommandsTest {

    private static final IMyLogger log = new MySystemLogger();

    @Rule public final Expect EXPECT = Expect.create();

    @Before
    public void setUp() throws Exception {
        MyCommands.sUT = true;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Ignore
    @Test
    public void testSomething() throws Exception {
        Date d1 = new Date();
        Date d2 = new Date();
        log.i(str(d1), null);
        log.i(str(d2), null);
        log.i(format("%b", d1.equals(d2)), null);

        long l1 = currentTimeMillis();
        long l2 = currentTimeMillis();
        log.i(format("%d", l1), null);
        log.i(format("%d", l2), null);
        log.i(format("%b", l1 == l2), null);
        assertThat(l1).isEqualTo(l2);
    }


    public void testRE(String re, int groupCount, boolean matchWholeString, boolean shouldMatch, String... strings) {
        Pattern pattern = Pattern.compile(re);
        for(String s: strings) {
            Matcher matcher = pattern.matcher(s);
            boolean matches = matchWholeString ? matcher.matches() : matcher.lookingAt();
            log.d(format("RE:\"%s\" %s %s text: \"%s\"", re, matches ? "matches" : "does not match", matchWholeString ? "whole" : "looking at", s), null);
            assertThat(matcher.groupCount()).isEqualTo(groupCount);
            assertThat(matches).isEqualTo(shouldMatch);
            if(matches) {
                for(int i = 0; i <= groupCount; ++i)
                    log.d(format("   group %d: \"%s\"", i, matcher.group(i)), null);
            }
        }
    }

    @Test
    public void testSimpleRE() throws Exception {
        String re = "^activity (?=\\S*\\.)([_\\./a-zA-Z0-9]*)";
        testRE(re, 1, true, true,
                "activity .MyActivity",
                "activity .My/Activity"
        );
        testRE(re, 1, true, false,
                "activity MyActivity"
        );

        RERule arule = RE_ACTIVITY_GROUP.getRules().get(0);
        String result = arule.apply("activity .MyActivity", log);
        log.w(format("Result: %s", result), null);

    }
    @Test
    public void testRE_ID() throws Exception {
        testRE(MyCommands.RE_ID, 1, true, true,
                "fdjskl",
                "RR989dla",
                "_222z",
                "_"
                );
        testRE(MyCommands.RE_ID, 1, true, false,
                " fdjskl",
                "a b",
                "9RR989dla",
                ""
        );
    }

    @Test
    public void testRE_MULTI_ID() throws Exception {

        testRE(MyCommands.RE_MULTI_ID, 4, true, true,
                "fdjskl/fdjks",
                "fdjskl.fdjks",
                "RR989dla",
                "a/b/c.d.e.f",
                ".RR989dla",
                "_222z.a.a.a.b.d",
                "._"
        );
        testRE(MyCommands.RE_MULTI_ID, 4, true, false,
                " fdjskl",
                "aaa.",
                "",
                "/dla",
                "a b",
                ".2fjdskl",
                ""
        );
    }

    @Test
    public void testRE_EXTRA_TYPE() throws Exception {

        testRE(MyCommands.RE_EXTRA_TYPE, 1, true, true,
                "string",
                "integer",
                "short"
        );
        testRE(MyCommands.RE_EXTRA_TYPE, 1, true, false,
                "object",
                "integer.string",
                ""
        );
    }

    @Test
    public void testRE_KEYWORD() throws Exception {

        testRE(MyCommands.RE_KEYWORD, 1, true, true,
                "start",
                "category",
                "extra"
        );
        testRE(MyCommands.RE_KEYWORD, 1, true, false,
                "object",
                "action start",
                " ",
                ""
        );
    }

    @Test
    public void testRE_VALUE() throws Exception {
        // we will try to show that our RE_VALUE is not greedy..
        String txt = "yyyyRyyyyRyyyyRyyyyRyyyy";
        String gval = "(.*)";
        String val = MyCommands.RE_VALUE;
        testRE(gval + "R" + gval + "R" + gval, 3, false, true, txt);
        testRE( val + "R" +  val + "R" +  val, 3, false, true, txt);
    }

    @Test
    public void testRE_END() throws Exception { //TODO SOMEDAY
    }

    @Test
    public void testRE_SEGMENT() throws Exception {

        testRE(MyCommands.RE_SEGMENT, 4, false, true,
                "extra bla ble bleee",
                "extra bla action x",
                "extra bla acton x",
                "extra ",
                "action xxx component a.b.c",
                "component a/b.c.d bleee",
                "component a/b.c.d start service"
        );
        testRE(MyCommands.RE_SEGMENT, 4, false, false,
                "extrrra bla ble bleee",
                "extra"
        );
    }

    @Test
    public void testRE_EXTRA_ELEM() throws Exception {

        testRE(MyCommands.RE_EXTRA_ELEM, 7, true, true,
                "integer bla ble bleee",
                "string action x",
                "integer val 7",
                "float f 222.333"
        );
        testRE(MyCommands.RE_EXTRA_ELEM, 7, true, false,
                "extrrra bla ble bleee",
                "extra integer val 7",
                "integer 7",
                ""
        );
    }

    @Test
    public void testRE_EXTRA_ELEM_TYPE_AND_KEY() throws Exception {

        testRE(MyCommands.RE_EXTRA_ELEM_TYPE_AND_KEY, 6, true, true,
                "integer bla",
                "string kkeeyy",
                "integer val",
                "float flf"
        );
        testRE(MyCommands.RE_EXTRA_ELEM_TYPE_AND_KEY, 6, true, false,
                "integer bla ble",
                "extrrra bla ble bleee",
                "integer val 7",
                "integer 7",
                ""
        );
    }


    @Test
    public void testRE_RULES() throws Exception {
        log.i(str(MyCommands.RE_RULES.get(0)), null);
        log.i(str(MyCommands.RE_RULES.get(1)), null);
        log.i(str(MyCommands.RE_RULES.get(2)), null);
        log.i(str(MyCommands.RE_RULES.get(3)), null);
        log.i(str(MyCommands.RE_RULES.get(4)), null);
        log.i(str(MyCommands.RE_RULES.get(5)), null);
        log.i(str(MyCommands.RE_RULES.get(6)), null);
    }


    public void testREGroupApplyAll(String input, String expected) {
        String command = MyCommands.REGroup.applyAll(MyCommands.RE_RULES, input, log);
        EXPECT.that(command).isEqualTo(expected);
    }

    public void multiTestREGroupApplyAll(List<String> inputs, String expected) {
        for(String input: inputs)
            testREGroupApplyAll(input, expected);
    }


    @Test
    public void testAlarmRules() throws Exception {

        String expected = "action android.intent.action.SET_ALARM extra integer android.intent.extra.alarm.HOUR 1 extra integer android.intent.extra.alarm.MINUTES 0";
        List<String> commands = Arrays.asList(
                "set alarm at 1 0",
                "set an alarm at 1 0",
                "set an alarm;for 1;0",
                "set alarm to 1:0",
                "set;an;alarm;to;1;0"
        );

        multiTestREGroupApplyAll(commands, expected);


        expected = "action android.intent.action.SET_ALARM extra integer android.intent.extra.alarm.HOUR 2 extra integer android.intent.extra.alarm.MINUTES 59 extra boolean android.intent.extra.alarm.SKIP_UI true";
        commands = Arrays.asList(
                "set alarm at 2 59 quickly",
                "set an alarm;for 2;59 quickly",
                "set;an;alarm;to;2;59;quickly"
        );

        multiTestREGroupApplyAll(commands, expected);

        expected = "action android.intent.action.SET_ALARM extra integer android.intent.extra.alarm.HOUR 2 extra integer android.intent.extra.alarm.MINUTES 59 extra string android.intent.extra.alarm.MESSAGE bla blee";
        commands = Arrays.asList(
                "set alarm at 2 59 with message bla blee",
                "set an alarm;for 2;59 with message bla blee",
                "set;an;alarm;to;2;59; with;message;bla;blee"
        );

        multiTestREGroupApplyAll(commands, expected);

    }

    @Test
    public void testTimerRules() throws Exception {
        String expected = "action android.intent.action.SET_TIMER extra integer android.intent.extra.alarm.LENGTH 30";
        List<String> commands = Arrays.asList(
                "set timer at 30",
                "set a timer for 30",
                "set;a;timer;for;30",
                "set timer to 30 seconds"
        );

        multiTestREGroupApplyAll(commands, expected);

    }

    @Test
    public void testActivityRules() throws Exception {

        testREGroupApplyAll("start activity component MyActivity", "start activity component MyActivity");
        testREGroupApplyAll("start activity component bla.MyActivity", "start activity component bla.MyActivity");
        testREGroupApplyAll("start activity component .MyActivity", "start activity component .MyActivity");
        testREGroupApplyAll("start activity MyActivity", "start activity MyActivity"); //WARNING: we do NOT recognize this kind of shortcut!
        testREGroupApplyAll("activity component MyActivity", "start activity component MyActivity");
        testREGroupApplyAll("activity data http://blabla", "start activity data http://blabla");
        testREGroupApplyAll("activity .MyActivity", "start activity component .MyActivity");
        testREGroupApplyAll("activity b.la action view", "start activity component b.la action android.intent.action.VIEW");
        testREGroupApplyAll("activity action view", "start activity action android.intent.action.VIEW");

    }


    @Test
    public void testFragmentRules() throws Exception {

        testREGroupApplyAll("start fragment component a.b.c.MyXFragment", "start fragment component a.b.c.MyXFragment");
        testREGroupApplyAll("fragment a.b.c.MyXFragment", "start fragment component a.b.c.MyXFragment");
        testREGroupApplyAll("fragment .bla action view", "start fragment component .bla action android.intent.action.VIEW");

    }

    @Test
    public void testParseCommand() throws Exception {
        Map<String, String> map = new HashMap<>(20);
        List<String> commands = Arrays.asList(
                "action b data d;",
                "action x.y.z",
                "extra integer a.b.c.d 667",
                "action send data http://blabla.blabla.com/path/to/something extra boolean checked true",
                "action a.b.X.Y extra string bla blabla extra integer satan 666 task hell"
        );
        for(String command: commands) {
            map.clear();
            parseCommand(command, map);
            log.i(format("parseCommand: %s", command), null);
            log.i(format("result: %s", str(map)), null);
        }

    }

    @Test
    public void testParseCommandExtraSegment() throws Exception {
        log.w(MyCommands.RE_EXTRA_ELEM, null);
        String extra = "integer android.intent.extra.alarm.LENGTH 5";
        Map<String, String> map = new HashMap<>(20);
        MyCommands.parseCommandExtraSegment(extra, map);
        log.w(str(map), null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseCommandExtraSegmentException() throws Exception {
        String extra = "untiger android.intent.extra.alarm.LENGTH 5";
        Map<String, String> map = new HashMap<>(20);
        MyCommands.parseCommandExtraSegment(extra, map);
    }

    @Test
    public void testSetIntentFromCommand() throws Exception { //TODO SOMEDAY
    }

}