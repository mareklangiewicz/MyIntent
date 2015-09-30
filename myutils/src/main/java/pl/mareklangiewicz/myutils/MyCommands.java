package pl.mareklangiewicz.myutils;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.common.base.MoreObjects;
import com.noveogroup.android.log.MyLogger;

import org.javatuples.KeyValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marek Langiewicz on 29.09.15.
 *
 */
public class MyCommands {


    static boolean sUT = false; // FIXME LATER: this is temporary hack to detect unit tests.. remove it.

    static final boolean VERBOSE = false; //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false; //TODO LATER: implement it as a build time switch for user


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            CMD_ACTIVITY,
            CMD_SERVICE,
            CMD_BROADCAST,
            CMD_FRAGMENT,
            CMD_SCROLL,
            CMD_SAY
    })
    public @interface CommandName {}

    public static final String CMD_ACTIVITY = "activity";
    public static final String CMD_SERVICE = "service";
    public static final String CMD_BROADCAST = "broadcast";
    public static final String CMD_FRAGMENT = "fragment";
    public static final String CMD_SCROLL = "scroll";
    public static final String CMD_SAY = "say";



    public static @NonNull <T> T valOrDef(@Nullable T val,@NonNull T def){ return MoreObjects.firstNonNull(val, def); }
    public static @NonNull <T> T mapOrNot(@NonNull T val, @NonNull Map<T, T> map) { return valOrDef(map.get(val), val); }



    //TODO LATER: keep regular expressions precompiled already..


    public static final String RE_ID = "([_a-zA-Z][_a-zA-Z0-9]*)";
    // $1: id

    public static final String RE_MULTI_ID = "(\\.?" + RE_ID + "((?:(?:\\.|/)" + RE_ID + ")*))";
    // $1: whole multi id (a multipart ident. divided by dots and/or slashes; can start with dot)
    // $2: id (first part - until first divider)
    // $3: rest (all after id - starting with divider - if not empty)
    // $4: subid (last part of multi id - after last divider)



    public static final String RE_EXTRA_TYPE = "((?:string)|(?:boolean)|(?:byte)|(?:char)|(?:double)|(?:float)|(?:integer)|(?:long)|(?:short))";
    // $1: datatype

    public static final String RE_KEYWORD = "((?:start)|(?:action)|(?:category)|(?:type)|(?:data)|(?:flags)|(?:package)|(?:component)|(?:scheme)|(?:bounds)|" +
            "(?:extra))";
    // $1: keyword

    public static final String RE_VALUE = "(.*?)"; // TODO LATER: don't we need some always forbidden characters?
    // $1: value

    public static final String RE_END = "(?: |\\Z)";

    public static final String RE_SEGMENT = "(" + RE_KEYWORD + " " + RE_VALUE + RE_END + "(?=\\Z|" + RE_KEYWORD + "))";
    // $1: segment
    // $2: keyword
    // $3: value
    // $4: next keyword or the end (empty string) - probably not important for us.

    public static final String RE_EXTRA_KEY = RE_MULTI_ID;
    // $1: whole key
    // $2: first part - until first dot
    // $3: rest
    // $4: last part - not important here..

    public static final String RE_EXTRA_ELEM = "(" + RE_EXTRA_TYPE + " " + RE_EXTRA_KEY + " " + RE_VALUE + ")";
    // $1: whole extra elem
    // $2: datatype
    // $3: extra key
    // $4: extra key - first part - not important
    // $5: extra key - the rest - not important
    // $6: extra key - last part - not important
    // $7: value

    public static final String RE_EXTRA_ELEM_TYPE_AND_KEY = "(" + RE_EXTRA_TYPE + " " + RE_EXTRA_KEY + ")";
    // $1: whole thing
    // $2: datatype
    // $3: extra key
    // $4: extra key - first part - not important
    // $5: extra key - the rest - not important
    // $6: extra key - last part - not important





    static public final List<KeyValue<String, List<KeyValue<String, String>>>> RE_RULES = Arrays.asList(

            KeyValue.with(".+", Arrays.asList( // first rules for all nonempty commands
                            KeyValue.with("(?:\\s|;)+", " ") // change semicolons to spaces (set it to only one in a row)
                    )
            ),

            KeyValue.with("((wake me)|(set alarm)|(set an alarm)).*", Arrays.asList(
                            KeyValue.with("^wake me up", "set alarm"),
                            KeyValue.with("^set an alarm", "set alarm"),
                            KeyValue.with("^set alarm ((for)|(to)|(at))", "set alarm"),
                            KeyValue.with("^set alarm (\\d+)(?::| )(\\d+)", "set alarm extra hour $1 extra minutes $2"),
                            KeyValue.with("^set alarm (\\d+)", "set alarm extra hour $1"),
                            KeyValue.with("^set alarm", "action set alarm"),
                            KeyValue.with("\\bextra hour (\\d+)\\b", "extra integer " + AlarmClock.EXTRA_HOUR + " $1"),
                            KeyValue.with("\\bextra minutes (\\d+)\\b", "extra integer " + AlarmClock.EXTRA_MINUTES + " $1"),
                            KeyValue.with("^action set alarm (.*) with message (.*?)$", "action set alarm $1 extra string " + AlarmClock.EXTRA_MESSAGE + " $2"),
                            KeyValue.with("(.*) quickly$", "$1 extra boolean " + AlarmClock.EXTRA_SKIP_UI + " true")
                    )
            ),

            KeyValue.with("((set timer)|(set a timer)).*", Arrays.asList(
                            KeyValue.with("^set a timer", "set timer"),
                            KeyValue.with("^set timer ((for)|(to)|(at))", "set timer"),
                            KeyValue.with("^set timer (\\d+)( seconds)?", "set timer extra length $1"),
                            KeyValue.with("^set timer", "action set timer"),
                            KeyValue.with("\\bextra length (\\d+)\\b", "extra integer " + AlarmClock.EXTRA_LENGTH + " $1"),
                            KeyValue.with("^action set timer (.*) with message (.*?)$", "action set timer $1 extra string " + AlarmClock.EXTRA_MESSAGE + " $2"),
                            KeyValue.with("(.*) quickly$", "$1 extra boolean " + AlarmClock.EXTRA_SKIP_UI + " true")
                    )
            ),

            KeyValue.with("((activity)|(fragment)).*", Arrays.asList(
                            KeyValue.with("^((?:activity)|(?:fragment)) " + RE_KEYWORD, "start $1 $2"), //no keyword can be activity or fragment class name..
                            KeyValue.with("^((?:activity)|(?:fragment)) " + RE_MULTI_ID, "start $1 component $2"),
                            KeyValue.with("^((?:activity)|(?:fragment)) ", "start $1 ")
                    )
            ),

            KeyValue.with(".+", Arrays.asList( // last rules for all nonempty commands:
                            KeyValue.with("\\baction view\\b", "action " + Intent.ACTION_VIEW),
                            KeyValue.with("\\baction send\\b", "action " + Intent.ACTION_SEND),
                            KeyValue.with("\\baction set alarm\\b", "action " + AlarmClock.ACTION_SET_ALARM),
                            KeyValue.with("\\baction set timer\\b", "action " + AlarmClock.ACTION_SET_TIMER)
                    )
            )

    );



    /**
     * WARNING: It's NOT optimized for memory usage AT ALL, so DON't USE IT TOO OFTEN..
     */
    public static String applyRERulesList(String command, List<KeyValue<String, String>> rules, MyLogger log) {
        String oldcommand = null;
        for (KeyValue<String, String> rule : rules) {
            oldcommand = command;
            String key = rule.getKey();
            String val = rule.getValue();
            command = command.replaceAll(key, val);
            if(VERBOSE) {
                if (!command.equals(oldcommand)) {
                    log.v("    rule matched:");
                    log.d("            rule: \"%s\" -> \"%s\"", key, val);
                    log.i("         command: \"%s\"", command);
                }
                else if(VERY_VERBOSE){
                    log.v("rule NOT matched:");
                    log.v("            rule: \"%s\" -> \"%s\"", key, val);
                    log.v("         command: \"%s\"", command);
                }
            }
        }
        return command;
    }


    /**
     * WARNING: It's NOT optimized for memory usage AT ALL, so DON't USE IT TOO OFTEN..
     */
    public static String applyRERulesLists(String command, List<KeyValue<String, List<KeyValue<String, String>>>> rules, MyLogger log) {
        if(VERBOSE) {
            log.v("Applying all matching RE rules to:");
            log.w("    >>>  command: \"%s\"", command);
        }
        else
            log.v("> cmd: \"%s\"", command);
        for(KeyValue<String, List<KeyValue<String, String>>> category: rules) {
            String catkey = category.getKey();
            if(command.matches(catkey)) {
                if(VERBOSE) {
                    log.v("category matched:");
                    log.d("        category: \"%s\"", catkey);
                }
                command = applyRERulesList(command, category.getValue(), log);
            }
            else if(VERY_VERBOSE) {
                log.v("category NOT matched:");
                log.v("            category: \"%s\"", catkey);
            }
        }
        if(VERBOSE) {
            log.v("All matching RE rules applied. Result:");
            log.w("    <<<  command: \"%s\"", command);
        }
        else
            log.v("< cmd: \"%s\"", command);
        return command;
    }




    static public void parseCommand(String in, Map<String, String> out) {
        int length = in.length();
        Pattern pattern = Pattern.compile(RE_SEGMENT);
        Matcher matcher = pattern.matcher(in);
        while(true) {
            boolean ok = matcher.lookingAt();
            if(!ok)
                throw new IllegalArgumentException("Illegal command format: " + in);

            int N = matcher.groupCount();

//            for(int i = 0; i <= N; ++i) log.d("%d: %s", i, matcher.group(i));

            if(N != 4)
                throw new InternalError();

            String keyword = matcher.group(2);
            String value = matcher.group(3);

            if(!sUT) { // FIXME LATER: this 'if' is temporary hack
                keyword = Uri.decode(keyword); // TODO LATER: analyse if this is the best place do Uri.decode (propably yes)
                value = Uri.decode(value); // TODO LATER: analyse if this is the best place to Uri.decode (probably yes)
            }

            switch(keyword) {
                case "extra":
                    parseCommandExtraSegment(value, out);
                    break;
                case "flags":
                    out.put(keyword, value);
                    //TODO LATER: what about multiple flags? add some symbolic multiple flags implementation
                    break;
                default:
                    out.put(keyword, value);
            }


            int end = matcher.end();
            if(end == length)
                break;
            matcher.region(end, length);
        }

    }

    static public void parseCommandExtraSegment(String extra, Map<String, String> out) {
        Pattern pattern = Pattern.compile(RE_EXTRA_ELEM);
        Matcher matcher = pattern.matcher(extra);
        boolean ok = matcher.matches();
        if(!ok)
            throw new IllegalArgumentException("Illegal extra segment: " + extra);
        int N = matcher.groupCount();
        if(N != 7)
            throw new InternalError();

        String type = matcher.group(2);
        String key = matcher.group(3);
        String value = matcher.group(7);

        out.put("extra " + type + " " + key, value);

    }


    public static void setIntentFromCommand(Intent intent, Map<String, String> cmd, MyLogger log) {

        for(String key:cmd.keySet()) {
            String value = cmd.get(key);
            switch (key) {
                case "start":
                    // we don't care here. Should be checked on higher level
                    break;
                case "action":
                    intent.setAction(value);
                    break;
                case "category":
                    intent.addCategory(value);
                    break;
                case "type":
                    Uri data = intent.getData();
                    if (data == null)
                        intent.setType(value);
                    else
                        intent.setDataAndType(data, value);
                    break;
                case "data":
                    String type = intent.getType();
                    if (type == null)
                        intent.setData(Uri.parse(value));
                    else
                        intent.setDataAndType(Uri.parse(value), type);
                    break;
                case "flags":
                    intent.addFlags(Integer.decode(value)); //TODO LATER: symbolic multiple flags implementation
                    break;
                case "package":
                    intent.setPackage(value);
                    break;
                case "component":
                    ComponentName cn = ComponentName.unflattenFromString(value);
                    if(cn == null)
                        log.e("Illegal component name: %s", value);
                    intent.setComponent(cn);
                    break;
                case "scheme":
                    intent.setData(Uri.parse(value + ":"));
                    break;
                case "bounds":
                    intent.setSourceBounds(Rect.unflattenFromString(value));
                    break;
                default:
                    if(key.startsWith("extra "))
                        setIntentExtra(intent, key.substring("extra ".length()), value);
                    else
                        throw new IllegalArgumentException("Illegal intent parameter:" + key);
            }
        }
    }

    private static void setIntentExtra(Intent intent, String key, String value) {
        Pattern pattern = Pattern.compile(RE_EXTRA_ELEM_TYPE_AND_KEY);
        Matcher matcher = pattern.matcher(key);
        boolean ok = matcher.matches();
        if(!ok)
            throw new IllegalArgumentException("Illegal extra type or key: " + key);
        int N = matcher.groupCount();
        if(N != 6)
            throw new InternalError();

        String type = matcher.group(2);
        key = matcher.group(3); // now key contains only key (no type)

        switch(type) {
            case "string" :intent.putExtra(key, value); break;
            case "boolean":intent.putExtra(key, Boolean.parseBoolean(value)); break;
            case "byte"   :intent.putExtra(key, Byte.parseByte(value)); break;
            case "char"   :intent.putExtra(key, value.charAt(0)); break;
            case "double" :intent.putExtra(key, Double.parseDouble(value)); break;
            case "float"  :intent.putExtra(key, Float.parseFloat(value)); break;
            case "integer":intent.putExtra(key, Integer.parseInt(value)); break;
            case "long"   :intent.putExtra(key, Long.parseLong(value)); break;
            case "short"  :intent.putExtra(key, Short.parseShort(value)); break;
            default:
                throw new IllegalArgumentException("Illegal extra segment type: " + type);
        }
    }


    public static void setBundleFromCommandExtras(Bundle bundle, Map<String, String> cmd) {

        for(String key:cmd.keySet()) {
            String value = cmd.get(key);
            if(key.startsWith("extra ")) {
                setBundleFromExtra(bundle, key.substring("extra ".length()), value);
            }
        }
    }

    private static void setBundleFromExtra(Bundle bundle, String key, String value) {
        Pattern pattern = Pattern.compile(RE_EXTRA_ELEM_TYPE_AND_KEY);
        Matcher matcher = pattern.matcher(key);
        boolean ok = matcher.matches();
        if(!ok)
            throw new IllegalArgumentException("Illegal extra type or key: " + key);
        int N = matcher.groupCount();
        if(N != 6)
            throw new InternalError();

        String type = matcher.group(2);
        key = matcher.group(3); // now key contains only key (no type)

        switch(type) {
            case "string" :bundle.putString(key, value); break;
            case "boolean":bundle.putBoolean(key, Boolean.parseBoolean(value)); break;
            case "byte"   :bundle.putByte(key, Byte.parseByte(value)); break;
            case "char"   :bundle.putChar(key, value.charAt(0)); break;
            case "double" :bundle.putDouble(key, Double.parseDouble(value)); break;
            case "float"  :bundle.putFloat(key, Float.parseFloat(value)); break;
            case "integer":bundle.putInt(key, Integer.parseInt(value)); break;
            case "long"   :bundle.putLong(key, Long.parseLong(value)); break;
            case "short"  :bundle.putShort(key, Short.parseShort(value)); break;
            default:
                throw new IllegalArgumentException("Illegal extra segment type: " + type);
        }
    }
}
