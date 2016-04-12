package pl.mareklangiewicz.myutils

import android.content.ComponentName
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.MediaStore
import java.util.*

/**
 * Created by Marek Langiewicz on 29.09.15.
 */

/*
private static final boolean V = BuildConfig.VERBOSE;
private static final boolean VV = BuildConfig.VERY_VERBOSE;

FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
Now it is always 'release' in libraries.. see:
https://code.google.com/p/android/issues/detail?id=52962
http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication
*/
private val V = true
private val VV = false


data class RERule private constructor(
        private var rmatch: Regex,
        var replace: String,
        var name: String,
        var description: String,
        var editable: Boolean) {

    constructor(match: String, replace: String, name: String = "", description: String = "", editable: Boolean = false)
    : this(Regex(match), replace, name, description, editable)

    var match: String
        get() = rmatch.pattern
        set(value) { rmatch = Regex(value) }

    val str: String get() = "$name: \"$match\" -> \"$replace\""

    /**
     * Checks if the rule matches ANYWHERE in given command
     */
    fun test(cmd: String): Boolean = rmatch.containsMatchIn(cmd)

    /**
     * Applying a rule means matching and replacing ALL occurrences of re match with replace
     */
    fun apply(cmd: String, log: Function1<MyLogEntry, Unit>): String {
        var vcmd = cmd
        if(test(vcmd)) {
            vcmd = rmatch.replace(vcmd, replace)
            // Note: this solution: test(using:containsMatchIn) and then replace - is slower than using native java Pattern and/or Matcher directly
            // (we create and use underlying matcher twice), but I want to stick with Kotlin abstraction layer.
            if (V) {
                log.v("rule matched:")
                log.d("rule $str")
                log.i("= cmd: $vcmd")
            }
        }
        else {
            if (VV) {
                log.v("rule NOT matched:")
                log.v("rule $str")
                log.v("= cmd: $vcmd")
            }
        }
        return vcmd
    }
}

fun Iterable<RERule>.applyAllRules(cmd: String, log: Function1<MyLogEntry, Unit>) = this.fold(cmd) { c, rule -> rule.apply(c, log) }


data class REGroup private constructor(
        private var rmatch: Regex,
        private var alrules: ArrayList<RERule>,
        var name: String,
        var description: String,
        var editable: Boolean) {


    constructor(match: String, name: String = "", description: String = "", editable: Boolean = false, vararg varules: RERule)
    : this(Regex(match), arrayListOf(*varules), name, description, editable)

    var match: String
        get() = rmatch.pattern
        set(value) { rmatch = Regex(value) }

    val rules: MutableList<RERule> get() = alrules

    val str: String get() = "$name: \"$match\""

    /**
     * Checks if the group match field matches ANYWHERE in given command
     */
    fun test(cmd: String): Boolean = rmatch.containsMatchIn(cmd)

    /**
     * It first check if the group match field matches ANYWHERE in given command, and if it does:
     * It applies all rules in this group one by one in order to given command.
     * Otherwise it just returns given command.
     */
    fun apply(cmd: String, log: Function1<MyLogEntry, Unit>): String {
        if (!test(cmd)) {
            if (VV) {
                log.v("group NOT matched:")
                log.v("group $str")
            }
            return cmd
        }
        if (V) {
            log.v("group matched:")
            log.d("group $str")
        }
        return rules.applyAllRules(cmd, log)
    }
}

fun Iterable<REGroup>.applyAllGroups(cmd: String, log: Function1<MyLogEntry, Unit>): String {

    if (V) {
        log.v("Applying all matching RE rules to:")
        log.i("> cmd: $cmd")
    } else
        log.v("> cmd: $cmd")

    val res = fold(cmd) { c, group -> group.apply(c, log) }

    if (V) {
        log.v("All matching RE rules applied. Result:")
        log.i("< cmd: $res")
    } else
        log.v("< cmd: $res")

    return res
}


val CMD_ACTIVITY = "activity"
val CMD_SERVICE = "service"
val CMD_BROADCAST = "broadcast"
val CMD_FRAGMENT = "fragment"
val CMD_CUSTOM = "custom"
val CMD_NOTHING = "nothing" // just for dry testing purposes


val DEFAULT_EXAMPLE_COMMANDS = listOf(
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
        "start nothing")


val DEFAULT_EXAMPLE_RULES = listOf<RERule>()//someday maybe we will add something here.


/**
 * $1: id
 */
val RE_ID = "([_a-zA-Z][_a-zA-Z0-9]*)"

/**
 * $1: whole multi id (a multipart ident. divided by dots and/or slashes; can start with dot)
 * $2: id (first part - until first divider)
 * $3: rest (all after id - starting with divider - if not empty)
 * $4: subid (last part of multi id - after last divider)
 */
val RE_MULTI_ID = "(\\.?$RE_ID((?:(?:\\.|/)$RE_ID)*))"

/**
 * $1: datatype
 */
val RE_EXTRA_TYPE = "((?:string)|(?:boolean)|(?:byte)|(?:char)|(?:double)|(?:float)|(?:integer)|(?:long)|(?:short))"

/**
 * $1: keyword
 */
val RE_KEYWORD = "((?:start)|(?:action)|(?:category)|(?:type)|(?:data)|(?:flags)|(?:package)|(?:component)|(?:scheme)|(?:bounds)|(?:extra))"

/**
 * $1: value
 */
val RE_VALUE = "(.*?)"


val RE_END = "(?: |\\Z)"

/**
 * $1: segment
 * $2: keyword
 * $3: value
 * $4: next keyword or the end (empty string) - probably not important for us.
 */
val RE_SEGMENT = "($RE_KEYWORD $RE_VALUE$RE_END(?=\\Z|$RE_KEYWORD))"

/**
 * $1: whole key
 * $2: first part - until first dot
 * $3: rest
 * $4: last part - not important here..
 */
val RE_EXTRA_KEY = RE_MULTI_ID

/**
 * $1: whole extra elem
 * $2: datatype
 * $3: extra key
 * $4: extra key - first part - not important
 * $5: extra key - the rest - not important
 * $6: extra key - last part - not important
 * $7: value
 */
val RE_EXTRA_ELEM = "($RE_EXTRA_TYPE $RE_EXTRA_KEY $RE_VALUE)"

/**
 * $1: whole thing
 * $2: datatype
 * $3: extra key
 * $4: extra key - first part - not important
 * $5: extra key - the rest - not important
 * $6: extra key - last part - not important
 */
val RE_EXTRA_ELEM_TYPE_AND_KEY = "($RE_EXTRA_TYPE $RE_EXTRA_KEY)"


// Not all android constants are available in our unit tests, so we create our own.
val EX_TEXT = "android.intent.extra.TEXT"
val EX_HOUR = AlarmClock.EXTRA_HOUR
val EX_MINUTES = AlarmClock.EXTRA_MINUTES
val EX_MESSAGE = AlarmClock.EXTRA_MESSAGE
val EX_LENGTH = "android.intent.extra.alarm.LENGTH"
val EX_SKIP_UI = AlarmClock.EXTRA_SKIP_UI
val EX_COMMAND = "android.intent.extra.pl.mareklangiewicz.COMMAND"
val ACT_SET_ALARM = AlarmClock.ACTION_SET_ALARM
val ACT_SET_TIMER = "android.intent.action.SET_TIMER"
val ACT_SHOW_ALARMS = "android.intent.action.SHOW_ALARMS"
val ACT_INSERT = Intent.ACTION_INSERT
val ACT_SEARCH = "android.intent.action.SEARCH"
val ACT_WEB_SEARCH = "android.intent.action.WEB_SEARCH"
val ACT_GMS_SEARCH = "com.google.android.gms.actions.SEARCH_ACTION"
val ACT_PLAY_FROM_SEARCH = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
val ACT_CREATE_NOTE = "com.google.android.gms.actions.CREATE_NOTE"
val ACT_DIAL = "android.intent.action.DIAL"
val ACT_CALL = "android.intent.action.CALL"
val DAT_CAL_EVENTS = "content://com.android.calendar/events"
val TYPE_CONTACTS = ContactsContract.Contacts.CONTENT_TYPE


val RE_INITIAL_GROUP = REGroup(
        "^.+",
        "initial",
        "First group that unifies separators. All semicolons and whitespaces are changed to single spaces. "
                + "This is useful if command comes from URL, so you can use semicolons as spaces.",
        false,
        RERule("(?:\\s|;)+", " ", "unify separators", "Replace semicolons and whitespaces with single spaces."))


val RE_USER_GROUP = REGroup("^.+", "user", "User rules. This group can be modified by user.", true)

val RE_PHONE_GROUP = REGroup("^(dial|call)", "phone", "Phone related shortcut rules.", false,
        RERule("^dial (.*)$", "action dial data tel:$1"),
        RERule("^call (.*)$", "action call data tel:$1")
)

val RE_SEARCH_GROUP = REGroup("^((web )?search)", "search", "Search related shortcut rules.", false,
        RERule("^search (.*)$", "action search extra string query $1"),
        RERule("^web search (.*)$", "action web search extra string query $1")
)

val RE_ALARM_GROUP = REGroup("^(wake me)|(set( an | the | )alarm)", "alarm", "Alarm related shortcut rules.", false,
        RERule("^wake me( up)?", "set alarm", "wake me", "Recognizes \"wake me\" as \"set alarm\" command."),
        RERule("^set (an|the) alarm", "set alarm", "", "Removes optional articles."),
        RERule("^set alarm (for|to|at)", "set alarm", "", "Removes optional prepositions."),
        RERule("^set alarm (\\d+)(?::| )(\\d+)", "set alarm extra hour $1 extra minutes $2", "", "Detects hours and minutes."),
        RERule("^set alarm (\\d+)", "set alarm extra hour $1", "", "Detects hours."),
        RERule("^set alarm", "action set alarm", "", "Adds \"action\" keyword."),
        RERule("^action set alarm (.*) with message (.*?)$", "action set alarm $1 extra message $2"),
        RERule("(.*) quickly$", "$1 extra quickly true")
)



val RE_TIMER_GROUP = REGroup("^set( a | the | )timer", "timer", "Timer related shortcut rules.", false,
        RERule("^set (a|the) timer", "set timer", "", "Removes optional articles."),
        RERule("^set timer (for|to|at)", "set timer", "", "Removes optional prepositions."),
        RERule("^set timer (\\d+)( seconds)?", "set timer extra length $1", "", "Detects number of seconds."),
        RERule("^set timer", "action set timer", "", "Adds \"action\" keyword."),
        RERule("^action set timer (.*) with message (.*?)$", "action set timer $1 extra message $2"),
        RERule("(.*) quickly$", "$1 extra quickly true")
)


val RE_MULTIMEDIA_GROUP = REGroup("^(take|record|play)", "multimedia", "Multimedia related shortcut rules.", false,
        RERule("^take( a | an | the )?(photo|picture|selfie|image)", "action android.media.action.STILL_IMAGE_CAMERA"),
        RERule("^record( a | an | the )?(movie|video|film)", "action android.media.action.VIDEO_CAMERA")
)


val RE_SETTINGS_GROUP = REGroup("^settings?", "settings", "Settings related shortcut rules.", false,
        RERule("^settings? bluetooth$", "action android.settings.BLUETOOTH_SETTINGS"),
        RERule("^settings? roaming$", "action android.settings.DATA_ROAMING_SETTINGS"),
        RERule("^settings? display$", "action android.settings.DISPLAY_SETTINGS"),
        RERule("^settings? internal storage$", "action android.settings.INTERNAL_STORAGE_SETTINGS"),
        RERule("^settings? location$", "action android.settings.LOCATION_SOURCE_SETTINGS"),
        RERule("^settings? apps?$", "action android.settings.MANAGE_APPLICATIONS_SETTINGS"),
        RERule("^settings? memory cards?$", "action android.settings.MEMORY_CARD_SETTINGS"),
        RERule("^settings? network$", "action android.settings.NETWORK_OPERATOR_SETTINGS"),
        RERule("^settings? nfc$", "action android.settings.NFC_SETTINGS"),
        RERule("^settings? privacy$", "action android.settings.PRIVACY_SETTINGS"),
        RERule("^settings? search$", "action android.search.action.SEARCH_SETTINGS"),
        RERule("^settings? security$", "action android.settings.SECURITY_SETTINGS"),
        RERule("^settings? sounds?$", "action android.settings.SOUND_SETTINGS"),
        RERule("^settings? sync$", "action android.settings.SYNC_SETTINGS"),
        RERule("^settings? wi-?fi$", "action android.settings.WIFI_SETTINGS"),
        RERule("^settings? wireless$", "action android.settings.WIRELESS_SETTINGS"),
        RERule("^settings?$", "action android.settings.SETTINGS")
)




val RE_ACTIVITY_GROUP = REGroup("^activity", "activity", "Activity related shortcut rules.", false,
        RERule("^activity (?=\\S*\\.)([_\\./a-zA-Z0-9]*)", "start activity component $1",
                "", "Allows shortcut commands like \"activity .MyGreatActivity\" instead of \"start activity component .MyGreatActivity\"."
        ),
        RERule("^activity", "start activity", "", "Adds \"start\" prefix in other cases (for implicit intents).")
)

val RE_FRAGMENT_GROUP = REGroup("^fragment", "fragment", "Fragment related shortcut rules.", false,
        RERule("^fragment (?=\\S*\\.)([_\\./a-zA-Z0-9]*)", "start fragment component $1",
                "", "Allows shortcut commands like \"fragment .MyGreatFragment\" instead of \"start fragment component .MyGreatFragment\"."
        )
)

val RE_OTHER_GROUP = REGroup("^.+", "other", "Other rules.", false,
        RERule("^((cancel)|(no(thing)?))\\b", "start nothing", "nothing", "Things you can say if you don't want anything to happen"),
        RERule("^note (.*)$", "action create note type text/plain extra text $1", "note", "Adds any given note to your note app")
)

val RE_ACTION_GROUP = REGroup("\\baction ", "action", "These rules replace short action names to full ones", false,
        RERule("\\baction main\\b", "action " + Intent.ACTION_MAIN, "", "Replaces \"action main\" with full action name."),
        RERule("\\baction view\\b", "action " + Intent.ACTION_VIEW, "", "Replaces \"action view\" with full action name."),
        RERule("\\baction send to\\b", "action " + Intent.ACTION_SENDTO, "", "Replaces \"action send to\" with full action name."),
        RERule("\\baction send\\b", "action " + Intent.ACTION_SEND, "", "Replaces \"action send\" with full action name."),
        RERule("\\baction edit\\b", "action " + Intent.ACTION_EDIT, "", "Replaces \"action edit\" with full action name."),
        RERule("\\baction set alarm\\b", "action " + ACT_SET_ALARM, "", "Replaces \"action set alarm\" with full action name."),
        RERule("\\baction set timer\\b", "action " + ACT_SET_TIMER, "", "Replaces \"action set timer\" with full action name."),
        RERule("\\baction show alarms\\b", "action " + ACT_SHOW_ALARMS, "", "Replaces \"action show alarms\" with full action name."),
        RERule("\\baction insert\\b", "action " + ACT_INSERT, "Replaces \"action insert\" with full action name."),
        RERule("\\baction search\\b", "action " + ACT_SEARCH),
        RERule("\\baction web search\\b", "action " + ACT_WEB_SEARCH),
        RERule("\\baction gms search\\b", "action " + ACT_GMS_SEARCH),
        RERule("\\baction play from search\\b", "action " + ACT_PLAY_FROM_SEARCH),
        RERule("\\baction create note\\b", "action " + ACT_CREATE_NOTE),
        RERule("\\baction dial\\b", "action " + ACT_DIAL),
        RERule("\\baction call\\b", "action " + ACT_CALL)
)

val RE_DATA_GROUP = REGroup("\\bdata ", "data", "These rules replace data shortcuts to full ones", false,
        RERule("\\bdata calendar events\\b", "data " + DAT_CAL_EVENTS, "", "Replaces \"data calendar events\" with full URL.")
)

val RE_TYPE_GROUP = REGroup("\\btype ", "type", "These rules replace type shortcuts to full ones", false,
        RERule("\\btype contacts\\b", "type " + TYPE_CONTACTS, "", "Replaces \"type contacts\" with full datatype for contacts.")
)

val RE_CATEGORY_GROUP = REGroup("\\bcategory ", "category", "These rules replace category shortcuts to full ones", false,
        RERule("\\bcategory browser\\b", "category " + Intent.CATEGORY_APP_BROWSER),
        RERule("\\bcategory calculator\\b", "category " + Intent.CATEGORY_APP_CALCULATOR),
        RERule("\\bcategory calendar\\b", "category " + Intent.CATEGORY_APP_CALENDAR),
        RERule("\\bcategory contacts\\b", "category " + Intent.CATEGORY_APP_CONTACTS),
        RERule("\\bcategory email\\b", "category " + Intent.CATEGORY_APP_EMAIL),
        RERule("\\bcategory gallery\\b", "category " + Intent.CATEGORY_APP_GALLERY),
        RERule("\\bcategory maps\\b", "category " + Intent.CATEGORY_APP_MAPS),
        RERule("\\bcategory market\\b", "category " + Intent.CATEGORY_APP_MARKET),
        RERule("\\bcategory messaging\\b", "category " + Intent.CATEGORY_APP_MESSAGING),
        RERule("\\bcategory music\\b", "category " + Intent.CATEGORY_APP_MUSIC),
        RERule("\\bcategory default\\b", "category " + Intent.CATEGORY_DEFAULT)
)

val RE_EXTRA_GROUP = REGroup("\\bextra ", "extra", "These rules replace extra shortcuts to full ones", false,
        RERule("\\bextra email\\b", "extra string " + Intent.EXTRA_EMAIL),
        RERule("\\bextra text\\b", "extra string " + EX_TEXT),
        RERule("\\bextra hour\\b", "extra integer " + EX_HOUR),
        RERule("\\bextra minutes\\b", "extra integer " + EX_MINUTES),
        RERule("\\bextra message\\b", "extra string " + EX_MESSAGE),
        RERule("\\bextra length\\b", "extra integer " + EX_LENGTH),
        RERule("\\bextra quickly\\b", "extra boolean " + EX_SKIP_UI),
        RERule("\\bextra command\\b", "extra string " + EX_COMMAND)
)


val RE_RULES = listOf(
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
)


class MyCommand(cmd: String = "") : HashMap<String, String>(20) {

    init {
        if(!cmd.isEmpty())
            parse(cmd)
    }

    constructor(cmd: String, rules: Iterable<REGroup>, log: Function1<MyLogEntry, Unit>) : this(rules.applyAllGroups(cmd, log))

    fun parse(cmd: String) {

        var shouldMatchAt = 0
        for(re in Regex(RE_SEGMENT).findAll(cmd)) {
            if(re.range.start != shouldMatchAt)
                throw IllegalArgumentException("Invalid command: $cmd")
            shouldMatchAt = re.range.endInclusive + 1
            val keyword = re.groupValues[2]
            val value = re.groupValues[3]
            when (keyword) {
                "extra" -> parseExtra(value)
                "flags" -> put(keyword, value) // TODO SOMEDAY: what about multiple flags? add some symbolic multiple flags implementation
                else -> put(keyword, value)
            }
        }
        if(shouldMatchAt != cmd.length)
            throw IllegalArgumentException("Invalid command: $cmd")
    }

    private fun parseExtra(extra: String) {
        val re = Regex(RE_EXTRA_ELEM).matchEntire(extra) ?: throw IllegalArgumentException("invalid \"extra\" format")
        val type = re.groupValues[2]
        val key = re.groupValues[3]
        val value = re.groupValues[7]
        put("extra $type $key", value)
    }
}


fun MyCommand.toIntent(): Intent {

    val intent = Intent();
    val bundle = Bundle()

    for ((key, value) in this) {
        when (key) {
            "start" -> {
                // we don't care here. Should be checked on higher level
            }
            "action" -> intent.action = value
            "category" -> intent.addCategory(value)
            "type" -> {
                val data = intent.data
                if (data == null)
                    intent.type = value
                else
                    intent.setDataAndType(data, value)
            }
            "data" -> {
                val type = intent.type
                if (type == null)
                    intent.data = Uri.parse(value)
                else
                    intent.setDataAndType(Uri.parse(value), type)
            }
            "flags" -> intent.addFlags(Integer.decode(value)!!) //TODO SOMEDAY: symbolic multiple flags implementation
            "package" -> intent.`package` = value
            "component" -> intent.component = ComponentName.unflattenFromString(value) ?: throw IllegalArgumentException("Illegal component name: $value")
            "scheme" -> intent.data = Uri.parse(value + ":")
            "bounds" -> intent.sourceBounds = Rect.unflattenFromString(value)
            else -> if (key.startsWith("extra "))
                bundle.putExtra(key.removePrefix("extra "), value)
            else
                throw IllegalArgumentException("Illegal intent parameter: $key")
        }
    }

    if(!bundle.isEmpty)
        intent.putExtras(bundle)

    return intent
}


private fun Bundle.putExtra(fullkey: String, value: String) {

    val regex = Regex(RE_EXTRA_ELEM_TYPE_AND_KEY)
    val re = regex.matchEntire(fullkey) ?: throw IllegalArgumentException("Illegal extra type or key: $fullkey")
    val type = re.groupValues[2]
    val key = re.groupValues[3]

    when (type) {
        "string" -> putString(key, value)
        "boolean" -> putBoolean(key, java.lang.Boolean.parseBoolean(value))
        "byte" -> putByte(key, java.lang.Byte.parseByte(value))
        "char" -> putChar(key, value[0])
        "double" -> putDouble(key, java.lang.Double.parseDouble(value))
        "float" -> putFloat(key, java.lang.Float.parseFloat(value))
        "integer" -> putInt(key, Integer.parseInt(value))
        "long" -> putLong(key, java.lang.Long.parseLong(value))
        "short" -> putShort(key, java.lang.Short.parseShort(value))
        else -> throw IllegalArgumentException("Illegal extra segment type: $type")
    }
}


fun MyCommand.toExtrasBundle(): Bundle {
    val bundle = Bundle()
    for ((key, value) in this) if (key.startsWith("extra ")) bundle.putExtra(key.removePrefix("extra "), value)
    return bundle
}

