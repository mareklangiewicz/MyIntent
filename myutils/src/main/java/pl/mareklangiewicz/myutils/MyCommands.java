package pl.mareklangiewicz.myutils;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.noveogroup.android.log.MyLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.google.common.base.MoreObjects;
//import org.javatuples.KeyValue;

/**
 * Created by Marek Langiewicz on 29.09.15.
 */
public final class MyCommands {

    /*
        private static final boolean V = BuildConfig.VERBOSE;
        private static final boolean VV = BuildConfig.VERY_VERBOSE;

        FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
        Now it is always 'release' in libraries.. see:
        https://code.google.com/p/android/issues/detail?id=52962
        http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
        http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication
    */
    private static final boolean V = true;
    private static final boolean VV = false;


    static boolean sUT = false; // FIXME SOMEDAY: this is temporary hack to detect unit tests.. remove it.


    public static final String CMD_ACTIVITY = "activity";
    public static final String CMD_SERVICE = "service";
    public static final String CMD_BROADCAST = "broadcast";
    public static final String CMD_FRAGMENT = "fragment";
    public static final String CMD_CUSTOM = "custom";
    public static final String CMD_NOTHING = "nothing"; // just for dry testing purposes


    static public final class RERule {

        private boolean mEditable;
        private String mName;
        private String mDescription;
        private String mMatch;
        private Pattern mPattern;
        private String mReplace;

        public RERule(boolean editable, @NonNull String name, @NonNull String description, @NonNull String match, @NonNull String replace) {
            setEditable(true);
            setName(name);
            setDescription(description);
            setMatch(match);
            setReplace(replace);
            setEditable(editable);
        }

        static public @NonNull String applyAll(@NonNull Iterable<RERule> rules, @NonNull String cmd, @NonNull MyLogger log) {
            for(RERule rule : rules) {
                cmd = rule.apply(cmd, log);
            }
            return cmd;
        }

        public boolean getEditable() { return mEditable; }

        void setEditable(boolean editable) { mEditable = editable; }

        public @NonNull String getName() { return mName; }

        public void setName(@NonNull String name) {
            if(!getEditable())
                throw new UnsupportedOperationException("RERule is not editable.");
            mName = name;
        }

        public @NonNull String getDescription() { return mDescription; }

        public void setDescription(@NonNull String description) {
            if(!getEditable())
                throw new UnsupportedOperationException("RERule is not editable.");
            mDescription = description;
        }

        public @NonNull String getMatch() { return mMatch; }

        public void setMatch(@NonNull String match) {
            if(!getEditable())
                throw new UnsupportedOperationException("RERule is not editable.");
            mMatch = match;
            mPattern = Pattern.compile(match);
        }

        public @NonNull String getReplace() { return mReplace; }

        public void setReplace(@NonNull String replace) {
            if(!getEditable())
                throw new UnsupportedOperationException("RERule is not editable.");
            mReplace = replace;
        }

        @Override public String toString() {
            return mName + ": \"" + mMatch + "\" -> \"" + mReplace + "\"";
        }

        /**
         * Checks if the rule matches ANYWHERE in given command
         */
        public boolean matches(@NonNull String cmd) {
            return mPattern.matcher(cmd).find(0);
        }

        /**
         * Applying a rule means matching and replacing ALL occurrences of re mMatch with mReplace
         */
        public @NonNull String apply(@NonNull String cmd, @NonNull MyLogger log) {

            Matcher matcher = mPattern.matcher(cmd);

            if(matcher.find(0)) {
                cmd = matcher.replaceAll(mReplace);
                if(V) {
                    log.v("rule matched:");
                    log.d("rule %s", toString());
                    log.i("= cmd: %s", cmd);
                }
                return cmd;
            }
            else {
                if(VV) {
                    log.v("rule NOT matched:");
                    log.v("rule %s", toString());
                    log.v("= cmd: %s", cmd);
                }
                return cmd;
            }
        }
    }

    static public final class REGroup {

        private boolean mEditable;
        private String mName;
        private String mDescription;
        private String mMatch;
        private Pattern mPattern;
        private List<RERule> mRules;

        public REGroup(boolean editable, @NonNull String name, @NonNull String description, @NonNull String match, RERule... rules) {
            setEditable(true);
            setName(name);
            setDescription(description);
            setMatch(match);
            mRules = new ArrayList<>(rules.length);
            mRules.addAll(Arrays.asList(rules));
            setEditable(editable);
        }

        static public @NonNull String applyAll(@NonNull Iterable<REGroup> groups, @NonNull String cmd, @NonNull MyLogger log) {

            if(V) {
                log.v("Applying all matching RE rules to:");
                log.i("> cmd: %s", cmd);
            }
            else
                log.v("> cmd: %s", cmd);

            for(REGroup group : groups) {
                cmd = group.apply(cmd, log);
            }

            if(V) {
                log.v("All matching RE rules applied. Result:");
                log.i("< cmd: %s", cmd);
            }
            else
                log.v("< cmd: %s", cmd);

            return cmd;
        }

        public boolean getEditable() { return mEditable; }

        void setEditable(boolean editable) { mEditable = editable; }

        public @NonNull String getName() { return mName; }

        public void setName(@NonNull String name) {
            if(!getEditable())
                throw new UnsupportedOperationException("REGroup is not editable.");
            mName = name;
        }

        public @NonNull String getDescription() { return mDescription; }

        public void setDescription(@NonNull String description) {
            if(!getEditable())
                throw new UnsupportedOperationException("REGroup is not editable.");
            mDescription = description;
        }

        public @NonNull String getMatch() { return mMatch; }

        public void setMatch(@NonNull String match) {
            if(!getEditable())
                throw new UnsupportedOperationException("REGroup is not editable.");
            mMatch = match;
            mPattern = Pattern.compile(match);
        }

        public @NonNull List<RERule> getRules() {
            return getEditable() ? mRules : Collections.unmodifiableList(mRules);
        }

        @Override public String toString() {
            return mName + ": \"" + mMatch + "\"";
        }

        /**
         * Checks if the group match field matches ANYWHERE in given command
         */
        public boolean matches(@NonNull String cmd) {
            return mPattern.matcher(cmd).find(0);
        }

        /**
         * It first check if the group match field matches ANYWHERE in given command, and if it does:
         * It applies all rules in this group one by one in order to given command.
         * Otherwise it just returns given command.
         */
        public @NonNull String apply(@NonNull String cmd, MyLogger log) {

            if(!matches(cmd)) {
                if(VV) {
                    log.v("group NOT matched:");
                    log.v("group %s", toString());
                }
                return cmd;
            }

            if(V) {
                log.v("group matched:");
                log.d("group %s", toString());
            }
            return RERule.applyAll(getRules(), cmd, log);
        }
    }

    static public final List<String> DEFAULT_EXAMPLE_COMMANDS = Arrays.asList(
            "data http://mareklangiewicz.pl",
            "settings wi-fi",
            "dial 123456789",
            "call 123456789",
            "search king",
            "web search rxkotlin",
            "wake me up at 7 30",
            "set alarm to 9 15",
            "set timer for 40",
            "take a picture",
            "record a movie",
            "note buy a cat food",
            "start nothing"
    );

    static public final List<RERule> DEFAULT_EXAMPLE_RULES = Arrays.asList(
    );


    /**
     * $1: id
     */
    public static final String RE_ID = "([_a-zA-Z][_a-zA-Z0-9]*)";

    /**
     * $1: whole multi id (a multipart ident. divided by dots and/or slashes; can start with dot)
     * $2: id (first part - until first divider)
     * $3: rest (all after id - starting with divider - if not empty)
     * $4: subid (last part of multi id - after last divider)
     */
    public static final String RE_MULTI_ID = "(\\.?" + RE_ID + "((?:(?:\\.|/)" + RE_ID + ")*))";

    /**
     * $1: datatype
     */
    public static final String RE_EXTRA_TYPE = "((?:string)|(?:boolean)|(?:byte)|(?:char)|(?:double)|(?:float)|(?:integer)|(?:long)|(?:short))";

    /**
     * $1: keyword
     */
    public static final String RE_KEYWORD =
            "((?:start)|(?:action)|(?:category)|(?:type)|(?:data)|(?:flags)|(?:package)|(?:component)|(?:scheme)|(?:bounds)|(?:extra))";

    /**
     * $1: value
     */
    public static final String RE_VALUE = "(.*?)";


    public static final String RE_END = "(?: |\\Z)";

    /**
     * $1: segment
     * $2: keyword
     * $3: value
     * $4: next keyword or the end (empty string) - probably not important for us.
     */
    public static final String RE_SEGMENT = "(" + RE_KEYWORD + " " + RE_VALUE + RE_END + "(?=\\Z|" + RE_KEYWORD + "))";

    /**
     * $1: whole key
     * $2: first part - until first dot
     * $3: rest
     * $4: last part - not important here..
     */
    public static final String RE_EXTRA_KEY = RE_MULTI_ID;

    /**
     * $1: whole extra elem
     * $2: datatype
     * $3: extra key
     * $4: extra key - first part - not important
     * $5: extra key - the rest - not important
     * $6: extra key - last part - not important
     * $7: value
     */
    public static final String RE_EXTRA_ELEM = "(" + RE_EXTRA_TYPE + " " + RE_EXTRA_KEY + " " + RE_VALUE + ")";

    /**
     * $1: whole thing
     * $2: datatype
     * $3: extra key
     * $4: extra key - first part - not important
     * $5: extra key - the rest - not important
     * $6: extra key - last part - not important
     */
    public static final String RE_EXTRA_ELEM_TYPE_AND_KEY = "(" + RE_EXTRA_TYPE + " " + RE_EXTRA_KEY + ")";


    // Not all android constants are available in our unit tests, so we create our own.
    private static final String EX_TEXT = "android.intent.extra.TEXT";
    private static final String EX_HOUR = AlarmClock.EXTRA_HOUR;
    private static final String EX_MINUTES = AlarmClock.EXTRA_MINUTES;
    private static final String EX_MESSAGE = AlarmClock.EXTRA_MESSAGE;
    private static final String EX_LENGTH = "android.intent.extra.alarm.LENGTH";
    private static final String EX_SKIP_UI = AlarmClock.EXTRA_SKIP_UI;
    private static final String ACT_SET_ALARM = AlarmClock.ACTION_SET_ALARM;
    private static final String ACT_SET_TIMER = "android.intent.action.SET_TIMER";
    private static final String ACT_SHOW_ALARMS = "android.intent.action.SHOW_ALARMS";
    private static final String ACT_INSERT = Intent.ACTION_INSERT;
    private static final String ACT_SEARCH = "android.intent.action.SEARCH";
    private static final String ACT_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
    private static final String ACT_GMS_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION";
    private static final String ACT_PLAY_FROM_SEARCH = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH;
    private static final String ACT_CREATE_NOTE = "com.google.android.gms.actions.CREATE_NOTE";
    private static final String ACT_DIAL = "android.intent.action.DIAL";
    private static final String ACT_CALL = "android.intent.action.CALL";
    private static final String DAT_CAL_EVENTS = "content://com.android.calendar/events";
    private static final String TYPE_CONTACTS = ContactsContract.Contacts.CONTENT_TYPE;


    static public final REGroup RE_INITIAL_GROUP =
            new REGroup(
                    false,
                    "initial",
                    "First group that unifies separators. All semicolons and whitespaces are changed to single spaces. " +
                            "This is useful if command comes from URL, so you can use semicolons as spaces.",
                    "^.+",
                    new RERule(false, "unify separators", "Replace semicolons and whitespaces with single spaces.", "(?:\\s|;)+", " ")
            );


    static public final REGroup RE_USER_GROUP =
            new REGroup(
                    true,
                    "user",
                    "User rules. This group can be modified by user.",
                    "^.+"
            );

    static public final REGroup RE_PHONE_GROUP =
            new REGroup(
                    false,
                    "phone",
                    "Phone related shortcut rules.",
                    "^(dial|call)",
                    new RERule(false, "", "", "^dial (.*)$", "action dial data tel:$1"),
                    new RERule(false, "", "", "^call (.*)$", "action call data tel:$1")
            );

    static public final REGroup RE_SEARCH_GROUP =
            new REGroup(
                    false,
                    "search",
                    "Search related shortcut rules.",
                    "^((web )?search)",
                    new RERule(false, "", "", "^search (.*)$", "action search extra string query $1"),
                    new RERule(false, "", "", "^web search (.*)$", "action web search extra string query $1")
            );
    static public final REGroup RE_ALARM_GROUP =
            new REGroup(
                    false,
                    "alarm",
                    "Alarm related shortcut rules.",
                    "^(wake me)|(set( an | the | )alarm)",
                    new RERule(false, "wake me", "Recognizes \"wake me\" as \"set alarm\" command.", "^wake me( up)?", "set alarm"),
                    new RERule(false, "", "Removes optional articles.", "^set (an|the) alarm", "set alarm"),
                    new RERule(false, "", "Removes optional prepositions.", "^set alarm (for|to|at)", "set alarm"),
                    new RERule(false, "", "Detects hours and minutes.", "^set alarm (\\d+)(?::| )(\\d+)", "set alarm extra hour $1 extra minutes $2"),
                    new RERule(false, "", "Detects hours.", "^set alarm (\\d+)", "set alarm extra hour $1"),
                    new RERule(false, "", "Adds \"action\" keyword.", "^set alarm", "action set alarm"),
                    new RERule(false, "", "", "^action set alarm (.*) with message (.*?)$", "action set alarm $1 extra message $2"),
                    new RERule(false, "", "", "(.*) quickly$", "$1 extra quickly true")
            );


    static public final REGroup RE_TIMER_GROUP =
            new REGroup(
                    false,
                    "timer",
                    "Timer related shortcut rules.",
                    "^set( a | the | )timer",
                    new RERule(false, "", "Removes optional articles.", "^set (a|the) timer", "set timer"),
                    new RERule(false, "", "Removes optional prepositions.", "^set timer (for|to|at)", "set timer"),
                    new RERule(false, "", "Detects number of seconds.", "^set timer (\\d+)( seconds)?", "set timer extra length $1"),
                    new RERule(false, "", "Adds \"action\" keyword.", "^set timer", "action set timer"),
                    new RERule(false, "", "", "^action set timer (.*) with message (.*?)$", "action set timer $1 extra message $2"),
                    new RERule(false, "", "", "(.*) quickly$", "$1 extra quickly true")
            );


    static public final REGroup RE_MULTIMEDIA_GROUP =
            new REGroup(
                    false,
                    "multimedia",
                    "Multimedia related shortcut rules.",
                    "^(take|record|play)",
                    new RERule(false, "", "", "^take( a | an | the )?(photo|picture|selfie|image)", "action android.media.action.STILL_IMAGE_CAMERA"),
                    new RERule(false, "", "", "^record( a | an | the )?(movie|video|film)", "action android.media.action.VIDEO_CAMERA")
            );

    static public final REGroup RE_SETTINGS_GROUP =
            new REGroup(
                    false,
                    "settings",
                    "Settings related shortcut rules.",
                    "^settings?",
                    new RERule(false, "", "", "^settings? bluetooth$", "action android.settings.BLUETOOTH_SETTINGS"),
                    new RERule(false, "", "", "^settings? roaming$", "action android.settings.DATA_ROAMING_SETTINGS"),
                    new RERule(false, "", "", "^settings? display$", "action android.settings.DISPLAY_SETTINGS"),
                    new RERule(false, "", "", "^settings? internal storage$", "action android.settings.INTERNAL_STORAGE_SETTINGS"),
                    new RERule(false, "", "", "^settings? location$", "action android.settings.LOCATION_SOURCE_SETTINGS"),
                    new RERule(false, "", "", "^settings? apps?$", "action android.settings.MANAGE_APPLICATIONS_SETTINGS"),
                    new RERule(false, "", "", "^settings? memory cards?$", "action android.settings.MEMORY_CARD_SETTINGS"),
                    new RERule(false, "", "", "^settings? network$", "action android.settings.NETWORK_OPERATOR_SETTINGS"),
                    new RERule(false, "", "", "^settings? nfc$", "action android.settings.NFC_SETTINGS"),
                    new RERule(false, "", "", "^settings? privacy$", "action android.settings.PRIVACY_SETTINGS"),
                    new RERule(false, "", "", "^settings? search$", "action android.search.action.SEARCH_SETTINGS"),
                    new RERule(false, "", "", "^settings? security$", "action android.settings.SECURITY_SETTINGS"),
                    new RERule(false, "", "", "^settings? sounds?$", "action android.settings.SOUND_SETTINGS"),
                    new RERule(false, "", "", "^settings? sync$", "action android.settings.SYNC_SETTINGS"),
                    new RERule(false, "", "", "^settings? wi-?fi$", "action android.settings.WIFI_SETTINGS"),
                    new RERule(false, "", "", "^settings? wireless$", "action android.settings.WIRELESS_SETTINGS"),
                    new RERule(false, "", "", "^settings?$", "action android.settings.SETTINGS")
            );
    static public final REGroup RE_ACTIVITY_GROUP =
            new REGroup(
                    false,
                    "activity",
                    "Activity related shortcut rules.",
                    "^activity",
                    new RERule(false, "",
                            "Allows shortcut commands like \"activity .MyGreatActivity\" instead of \"start activity component .MyGreatActivity\".",
                            "^activity (?=\\S*\\.)([_\\./a-zA-Z0-9]*)", "start activity component $1"),
                    new RERule(false, "", "Adds \"start\" prefix in other cases (for implicit intents).", "^activity", "start activity")
            );

    static public final REGroup RE_FRAGMENT_GROUP =
            new REGroup(
                    false,
                    "fragment",
                    "Fragment related shortcut rules.",
                    "^fragment",
                    new RERule(false, "",
                            "Allows shortcut commands like \"fragment .MyGreatFragment\" instead of \"start fragment component .MyGreatFragment\".",
                            "^fragment (?=\\S*\\.)([_\\./a-zA-Z0-9]*)", "start fragment component $1")
            );

    static public final REGroup RE_OTHER_GROUP =
            new REGroup(
                    false,
                    "other",
                    "Other rules.",
                    "^.+",
                    new RERule(false, "nothing", "Things you can say if you don't want anything to happen", "^((cancel)|(no(thing)?))\\b", "start nothing"),
                    new RERule(false, "note", "Adds any given note to your note app", "^note (.*)$", "action create note type text/plain extra text $1")
            );
    static public final REGroup RE_ACTION_GROUP =
            new REGroup(
                    false,
                    "action",
                    "These rules replace short action names to full ones", "\\baction ",
                    new RERule(false, "", "Replaces \"action main\" with full action name.", "\\baction main\\b", "action " + Intent.ACTION_MAIN),
                    new RERule(false, "", "Replaces \"action view\" with full action name.", "\\baction view\\b", "action " + Intent.ACTION_VIEW),
                    new RERule(false, "", "Replaces \"action send to\" with full action name.", "\\baction send to\\b", "action " + Intent.ACTION_SENDTO),
                    new RERule(false, "", "Replaces \"action send\" with full action name.", "\\baction send\\b", "action " + Intent.ACTION_SEND),
                    new RERule(false, "", "Replaces \"action edit\" with full action name.", "\\baction edit\\b", "action " + Intent.ACTION_EDIT),
                    new RERule(false, "", "Replaces \"action set alarm\" with full action name.", "\\baction set alarm\\b", "action " + ACT_SET_ALARM),
                    new RERule(false, "", "Replaces \"action set timer\" with full action name.", "\\baction set timer\\b", "action " + ACT_SET_TIMER),
                    new RERule(false, "", "Replaces \"action show alarms\" with full action name.", "\\baction show alarms\\b", "action " + ACT_SHOW_ALARMS),
                    new RERule(false, "", "Replaces \"action insert\" with full action name.", "\\baction insert\\b", "action " + ACT_INSERT),
                    new RERule(false, "", "", "\\baction search\\b", "action " + ACT_SEARCH),
                    new RERule(false, "", "", "\\baction web search\\b", "action " + ACT_WEB_SEARCH),
                    new RERule(false, "", "", "\\baction gms search\\b", "action " + ACT_GMS_SEARCH),
                    new RERule(false, "", "", "\\baction play from search\\b", "action " + ACT_PLAY_FROM_SEARCH),
                    new RERule(false, "", "", "\\baction create note\\b", "action " + ACT_CREATE_NOTE),
                    new RERule(false, "", "", "\\baction dial\\b", "action " + ACT_DIAL),
                    new RERule(false, "", "", "\\baction call\\b", "action " + ACT_CALL)
            );

    static public final REGroup RE_DATA_GROUP =
            new REGroup(
                    false,
                    "data",
                    "These rules replace data shortcuts to full ones", "\\bdata ",
                    new RERule(false, "", "Replaces \"data calendar events\" with full URL.", "\\bdata calendar events\\b", "data " + DAT_CAL_EVENTS)
            );

    static public final REGroup RE_TYPE_GROUP =
            new REGroup(
                    false,
                    "type",
                    "These rules replace type shortcuts to full ones", "\\btype ",
                    new RERule(false, "", "Replaces \"type contacts\" with full datatype for contacts.", "\\btype contacts\\b", "type " + TYPE_CONTACTS)
            );

    static public final REGroup RE_CATEGORY_GROUP =
            new REGroup(
                    false,
                    "category",
                    "These rules replace category shortcuts to full ones", "\\bcategory ",
                    new RERule(false, "", "", "\\bcategory browser\\b", "category " + Intent.CATEGORY_APP_BROWSER),
                    new RERule(false, "", "", "\\bcategory calculator\\b", "category " + Intent.CATEGORY_APP_CALCULATOR),
                    new RERule(false, "", "", "\\bcategory calendar\\b", "category " + Intent.CATEGORY_APP_CALENDAR),
                    new RERule(false, "", "", "\\bcategory contacts\\b", "category " + Intent.CATEGORY_APP_CONTACTS),
                    new RERule(false, "", "", "\\bcategory email\\b", "category " + Intent.CATEGORY_APP_EMAIL),
                    new RERule(false, "", "", "\\bcategory gallery\\b", "category " + Intent.CATEGORY_APP_GALLERY),
                    new RERule(false, "", "", "\\bcategory maps\\b", "category " + Intent.CATEGORY_APP_MAPS),
                    new RERule(false, "", "", "\\bcategory market\\b", "category " + Intent.CATEGORY_APP_MARKET),
                    new RERule(false, "", "", "\\bcategory messaging\\b", "category " + Intent.CATEGORY_APP_MESSAGING),
                    new RERule(false, "", "", "\\bcategory music\\b", "category " + Intent.CATEGORY_APP_MUSIC),
                    new RERule(false, "", "", "\\bcategory default\\b", "category " + Intent.CATEGORY_DEFAULT)
            );
    static public final REGroup RE_EXTRA_GROUP =
            new REGroup(
                    false,
                    "extra",
                    "These rules replace extra shortcuts to full ones", "\\bextra ",
                    new RERule(false, "", "", "\\bextra email\\b", "extra string " + Intent.EXTRA_EMAIL),
                    new RERule(false, "", "", "\\bextra text\\b", "extra string " + EX_TEXT),
                    new RERule(false, "", "", "\\bextra hour\\b", "extra integer " + EX_HOUR),
                    new RERule(false, "", "", "\\bextra minutes\\b", "extra integer " + EX_MINUTES),
                    new RERule(false, "", "", "\\bextra message\\b", "extra string " + EX_MESSAGE),
                    new RERule(false, "", "", "\\bextra length\\b", "extra integer " + EX_LENGTH),
                    new RERule(false, "", "", "\\bextra quickly\\b", "extra boolean " + EX_SKIP_UI)
            );


    static public final List<REGroup> RE_RULES = Arrays.asList(
            RE_INITIAL_GROUP,
            RE_USER_GROUP,
            RE_PHONE_GROUP,
            RE_SEARCH_GROUP,
            RE_ALARM_GROUP,
            RE_TIMER_GROUP,
            RE_MULTIMEDIA_GROUP,
            RE_SETTINGS_GROUP,
            RE_ACTIVITY_GROUP,
            RE_FRAGMENT_GROUP,
            RE_OTHER_GROUP,
            RE_ACTION_GROUP,
            RE_DATA_GROUP,
            RE_TYPE_GROUP,
            RE_CATEGORY_GROUP,
            RE_EXTRA_GROUP
    );


    private MyCommands() {
        throw new AssertionError("MyCommands class is noninstantiable.");
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

            if(!sUT) { // FIXME SOMEDAY: this 'if' is temporary hack
                keyword = Uri.decode(keyword);
                value = Uri.decode(value);
            }

            switch(keyword) {
                case "extra":
                    parseCommandExtraSegment(value, out);
                    break;
                case "flags":
                    out.put(keyword, value);
                    //TODO SOMEDAY: what about multiple flags? add some symbolic multiple flags implementation
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

        for(String key : cmd.keySet()) {
            String value = cmd.get(key);
            switch(key) {
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
                    if(data == null)
                        intent.setType(value);
                    else
                        intent.setDataAndType(data, value);
                    break;
                case "data":
                    String type = intent.getType();
                    if(type == null)
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
            case "string":
                intent.putExtra(key, value);
                break;
            case "boolean":
                intent.putExtra(key, Boolean.parseBoolean(value));
                break;
            case "byte":
                intent.putExtra(key, Byte.parseByte(value));
                break;
            case "char":
                intent.putExtra(key, value.charAt(0));
                break;
            case "double":
                intent.putExtra(key, Double.parseDouble(value));
                break;
            case "float":
                intent.putExtra(key, Float.parseFloat(value));
                break;
            case "integer":
                intent.putExtra(key, Integer.parseInt(value));
                break;
            case "long":
                intent.putExtra(key, Long.parseLong(value));
                break;
            case "short":
                intent.putExtra(key, Short.parseShort(value));
                break;
            default:
                throw new IllegalArgumentException("Illegal extra segment type: " + type);
        }
    }

    public static void setBundleFromCommandExtras(Bundle bundle, Map<String, String> cmd) {

        for(String key : cmd.keySet()) {
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
            case "string":
                bundle.putString(key, value);
                break;
            case "boolean":
                bundle.putBoolean(key, Boolean.parseBoolean(value));
                break;
            case "byte":
                bundle.putByte(key, Byte.parseByte(value));
                break;
            case "char":
                bundle.putChar(key, value.charAt(0));
                break;
            case "double":
                bundle.putDouble(key, Double.parseDouble(value));
                break;
            case "float":
                bundle.putFloat(key, Float.parseFloat(value));
                break;
            case "integer":
                bundle.putInt(key, Integer.parseInt(value));
                break;
            case "long":
                bundle.putLong(key, Long.parseLong(value));
                break;
            case "short":
                bundle.putShort(key, Short.parseShort(value));
                break;
            default:
                throw new IllegalArgumentException("Illegal extra segment type: " + type);
        }
    }
}
