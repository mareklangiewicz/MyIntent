package pl.mareklangiewicz.myutils

import android.content.ComponentName
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.MediaStore
import java.lang.String.format
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Marek Langiewicz on 29.09.15.
 */
object MyCommands {


    class RERule(
            var editable: Boolean = true,
            var name: String = "",
            var description: String = "",
            amatch: String,
            var replace: String) {

        var match: String = amatch
            set(value) {
                this.match = value
                mPattern = Pattern.compile(value)
            }

        private var mPattern: Pattern = Pattern.compile(amatch)

        override fun toString(): String {
            return "$name: \"$match\" -> \"$replace\""
        }

        /**
         * Checks if the rule matches ANYWHERE in given command
         */
        fun matches(cmd: String): Boolean {
            return mPattern.matcher(cmd).find(0)
        }

        /**
         * Applying a rule means matching and replacing ALL occurrences of re mMatch with mReplace
         */
        fun apply(acmd: String, log: IMyLogger): String {
            var cmd = acmd

            val matcher = mPattern.matcher(cmd)

            if (matcher.find(0)) {
                cmd = matcher.replaceAll(replace)
                if (V) {
                    log.v("rule matched:")
                    log.d(format("rule %s", toString()))
                    log.i(format("= cmd: %s", cmd))
                }
                return cmd
            } else {
                if (VV) {
                    log.v("rule NOT matched:")
                    log.v(format("rule %s", toString()))
                    log.v(format("= cmd: %s", cmd))
                }
                return cmd
            }
        }

        companion object {

            fun applyAll(rules: Iterable<RERule>, acmd: String, log: IMyLogger): String {
                var cmd = acmd
                for (rule in rules) {
                    cmd = rule.apply(cmd, log)
                }
                return cmd
            }
        }
    }

    class REGroup(
            var editable: Boolean = false,
            var name: String = "",
            var description: String = "",
            amatch: String,
            vararg arules: RERule) {

        var match: String = amatch
            set(value) {
                this.match = value
                mPattern = Pattern.compile(value)
            }

        private var mPattern: Pattern = Pattern.compile(amatch)

        private val mRules: MutableList<RERule> = ArrayList<RERule>(arules.size).apply { addAll(Arrays.asList(*arules)) }

        val rules: MutableList<RERule>
            get() = if (editable) mRules else Collections.unmodifiableList(mRules)

        override fun toString(): String {
            return name + ": \"" + match + "\""
        }

        /**
         * Checks if the group match field matches ANYWHERE in given command
         */
        fun matches(cmd: String): Boolean {
            return mPattern.matcher(cmd).find(0)
        }

        /**
         * It first check if the group match field matches ANYWHERE in given command, and if it does:
         * It applies all rules in this group one by one in order to given command.
         * Otherwise it just returns given command.
         */
        fun apply(cmd: String, log: IMyLogger): String {

            if (!matches(cmd)) {
                if (VV) {
                    log.v("group NOT matched:")
                    log.v(format("group %s", toString()))
                }
                return cmd
            }

            if (V) {
                log.v("group matched:")
                log.d(format("group %s", toString()))
            }
            return RERule.applyAll(rules, cmd, log)
        }

        companion object {

            fun applyAll(groups: Iterable<REGroup>, acmd: String, log: IMyLogger): String {
                var cmd = acmd

                if (V) {
                    log.v("Applying all matching RE rules to:")
                    log.i(format("> cmd: %s", cmd))
                } else
                    log.v(format("> cmd: %s", cmd))

                for (group in groups) {
                    cmd = group.apply(cmd, log)
                }

                if (V) {
                    log.v("All matching RE rules applied. Result:")
                    log.i(format("< cmd: %s", cmd))
                } else
                    log.v(format("< cmd: %s", cmd))

                return cmd
            }
        }
    }


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


    var sUT = false // FIXME SOMEDAY: this is temporary hack to detect unit tests.. remove it.


    val CMD_ACTIVITY = "activity"
    val CMD_SERVICE = "service"
    val CMD_BROADCAST = "broadcast"
    val CMD_FRAGMENT = "fragment"
    val CMD_CUSTOM = "custom"
    val CMD_NOTHING = "nothing" // just for dry testing purposes

    val DEFAULT_EXAMPLE_COMMANDS = Arrays.asList(
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

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    val DEFAULT_EXAMPLE_RULES = Arrays.asList<RERule>()//someday maybe we will add something here.


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
            false,
            "initial",
            "First group that unifies separators. All semicolons and whitespaces are changed to single spaces. " + "This is useful if command comes from URL, so you can use semicolons as spaces.",
            "^.+",
            RERule(false, "unify separators", "Replace semicolons and whitespaces with single spaces.", "(?:\\s|;)+", " "))


    val RE_USER_GROUP = REGroup(
            true,
            "user",
            "User rules. This group can be modified by user.",
            "^.+")

    val RE_PHONE_GROUP = REGroup(
            false,
            "phone",
            "Phone related shortcut rules.",
            "^(dial|call)",
            RERule(false, "", "", "^dial (.*)$", "action dial data tel:$1"),
            RERule(false, "", "", "^call (.*)$", "action call data tel:$1"))

    val RE_SEARCH_GROUP = REGroup(
            false,
            "search",
            "Search related shortcut rules.",
            "^((web )?search)",
            RERule(false, "", "", "^search (.*)$", "action search extra string query $1"),
            RERule(false, "", "", "^web search (.*)$", "action web search extra string query $1"))
    val RE_ALARM_GROUP = REGroup(
            false,
            "alarm",
            "Alarm related shortcut rules.",
            "^(wake me)|(set( an | the | )alarm)",
            RERule(false, "wake me", "Recognizes \"wake me\" as \"set alarm\" command.", "^wake me( up)?", "set alarm"),
            RERule(false, "", "Removes optional articles.", "^set (an|the) alarm", "set alarm"),
            RERule(false, "", "Removes optional prepositions.", "^set alarm (for|to|at)", "set alarm"),
            RERule(false, "", "Detects hours and minutes.", "^set alarm (\\d+)(?::| )(\\d+)", "set alarm extra hour $1 extra minutes $2"),
            RERule(false, "", "Detects hours.", "^set alarm (\\d+)", "set alarm extra hour $1"),
            RERule(false, "", "Adds \"action\" keyword.", "^set alarm", "action set alarm"),
            RERule(false, "", "", "^action set alarm (.*) with message (.*?)$", "action set alarm $1 extra message $2"),
            RERule(false, "", "", "(.*) quickly$", "$1 extra quickly true"))


    val RE_TIMER_GROUP = REGroup(
            false,
            "timer",
            "Timer related shortcut rules.",
            "^set( a | the | )timer",
            RERule(false, "", "Removes optional articles.", "^set (a|the) timer", "set timer"),
            RERule(false, "", "Removes optional prepositions.", "^set timer (for|to|at)", "set timer"),
            RERule(false, "", "Detects number of seconds.", "^set timer (\\d+)( seconds)?", "set timer extra length $1"),
            RERule(false, "", "Adds \"action\" keyword.", "^set timer", "action set timer"),
            RERule(false, "", "", "^action set timer (.*) with message (.*?)$", "action set timer $1 extra message $2"),
            RERule(false, "", "", "(.*) quickly$", "$1 extra quickly true"))


    val RE_MULTIMEDIA_GROUP = REGroup(
            false,
            "multimedia",
            "Multimedia related shortcut rules.",
            "^(take|record|play)",
            RERule(false, "", "", "^take( a | an | the )?(photo|picture|selfie|image)", "action android.media.action.STILL_IMAGE_CAMERA"),
            RERule(false, "", "", "^record( a | an | the )?(movie|video|film)", "action android.media.action.VIDEO_CAMERA"))

    val RE_SETTINGS_GROUP = REGroup(
            false,
            "settings",
            "Settings related shortcut rules.",
            "^settings?",
            RERule(false, "", "", "^settings? bluetooth$", "action android.settings.BLUETOOTH_SETTINGS"),
            RERule(false, "", "", "^settings? roaming$", "action android.settings.DATA_ROAMING_SETTINGS"),
            RERule(false, "", "", "^settings? display$", "action android.settings.DISPLAY_SETTINGS"),
            RERule(false, "", "", "^settings? internal storage$", "action android.settings.INTERNAL_STORAGE_SETTINGS"),
            RERule(false, "", "", "^settings? location$", "action android.settings.LOCATION_SOURCE_SETTINGS"),
            RERule(false, "", "", "^settings? apps?$", "action android.settings.MANAGE_APPLICATIONS_SETTINGS"),
            RERule(false, "", "", "^settings? memory cards?$", "action android.settings.MEMORY_CARD_SETTINGS"),
            RERule(false, "", "", "^settings? network$", "action android.settings.NETWORK_OPERATOR_SETTINGS"),
            RERule(false, "", "", "^settings? nfc$", "action android.settings.NFC_SETTINGS"),
            RERule(false, "", "", "^settings? privacy$", "action android.settings.PRIVACY_SETTINGS"),
            RERule(false, "", "", "^settings? search$", "action android.search.action.SEARCH_SETTINGS"),
            RERule(false, "", "", "^settings? security$", "action android.settings.SECURITY_SETTINGS"),
            RERule(false, "", "", "^settings? sounds?$", "action android.settings.SOUND_SETTINGS"),
            RERule(false, "", "", "^settings? sync$", "action android.settings.SYNC_SETTINGS"),
            RERule(false, "", "", "^settings? wi-?fi$", "action android.settings.WIFI_SETTINGS"),
            RERule(false, "", "", "^settings? wireless$", "action android.settings.WIRELESS_SETTINGS"),
            RERule(false, "", "", "^settings?$", "action android.settings.SETTINGS"))
    val RE_ACTIVITY_GROUP = REGroup(
            false,
            "activity",
            "Activity related shortcut rules.",
            "^activity",
            RERule(false, "",
                    "Allows shortcut commands like \"activity .MyGreatActivity\" instead of \"start activity component .MyGreatActivity\".",
                    "^activity (?=\\S*\\.)([_\\./a-zA-Z0-9]*)", "start activity component $1"),
            RERule(false, "", "Adds \"start\" prefix in other cases (for implicit intents).", "^activity", "start activity"))

    val RE_FRAGMENT_GROUP = REGroup(
            false,
            "fragment",
            "Fragment related shortcut rules.",
            "^fragment",
            RERule(false, "",
                    "Allows shortcut commands like \"fragment .MyGreatFragment\" instead of \"start fragment component .MyGreatFragment\".",
                    "^fragment (?=\\S*\\.)([_\\./a-zA-Z0-9]*)", "start fragment component $1"))

    val RE_OTHER_GROUP = REGroup(
            false,
            "other",
            "Other rules.",
            "^.+",
            RERule(false, "nothing", "Things you can say if you don't want anything to happen", "^((cancel)|(no(thing)?))\\b", "start nothing"),
            RERule(false, "note", "Adds any given note to your note app", "^note (.*)$", "action create note type text/plain extra text $1"))
    val RE_ACTION_GROUP = REGroup(
            false,
            "action",
            "These rules replace short action names to full ones", "\\baction ",
            RERule(false, "", "Replaces \"action main\" with full action name.", "\\baction main\\b", "action " + Intent.ACTION_MAIN),
            RERule(false, "", "Replaces \"action view\" with full action name.", "\\baction view\\b", "action " + Intent.ACTION_VIEW),
            RERule(false, "", "Replaces \"action send to\" with full action name.", "\\baction send to\\b", "action " + Intent.ACTION_SENDTO),
            RERule(false, "", "Replaces \"action send\" with full action name.", "\\baction send\\b", "action " + Intent.ACTION_SEND),
            RERule(false, "", "Replaces \"action edit\" with full action name.", "\\baction edit\\b", "action " + Intent.ACTION_EDIT),
            RERule(false, "", "Replaces \"action set alarm\" with full action name.", "\\baction set alarm\\b", "action " + ACT_SET_ALARM),
            RERule(false, "", "Replaces \"action set timer\" with full action name.", "\\baction set timer\\b", "action " + ACT_SET_TIMER),
            RERule(false, "", "Replaces \"action show alarms\" with full action name.", "\\baction show alarms\\b", "action " + ACT_SHOW_ALARMS),
            RERule(false, "", "Replaces \"action insert\" with full action name.", "\\baction insert\\b", "action " + ACT_INSERT),
            RERule(false, "", "", "\\baction search\\b", "action " + ACT_SEARCH),
            RERule(false, "", "", "\\baction web search\\b", "action " + ACT_WEB_SEARCH),
            RERule(false, "", "", "\\baction gms search\\b", "action " + ACT_GMS_SEARCH),
            RERule(false, "", "", "\\baction play from search\\b", "action " + ACT_PLAY_FROM_SEARCH),
            RERule(false, "", "", "\\baction create note\\b", "action " + ACT_CREATE_NOTE),
            RERule(false, "", "", "\\baction dial\\b", "action " + ACT_DIAL),
            RERule(false, "", "", "\\baction call\\b", "action " + ACT_CALL))

    val RE_DATA_GROUP = REGroup(
            false,
            "data",
            "These rules replace data shortcuts to full ones", "\\bdata ",
            RERule(false, "", "Replaces \"data calendar events\" with full URL.", "\\bdata calendar events\\b", "data " + DAT_CAL_EVENTS))

    val RE_TYPE_GROUP = REGroup(
            false,
            "type",
            "These rules replace type shortcuts to full ones", "\\btype ",
            RERule(false, "", "Replaces \"type contacts\" with full datatype for contacts.", "\\btype contacts\\b", "type " + TYPE_CONTACTS))

    val RE_CATEGORY_GROUP = REGroup(
            false,
            "category",
            "These rules replace category shortcuts to full ones", "\\bcategory ",
            RERule(false, "", "", "\\bcategory browser\\b", "category " + Intent.CATEGORY_APP_BROWSER),
            RERule(false, "", "", "\\bcategory calculator\\b", "category " + Intent.CATEGORY_APP_CALCULATOR),
            RERule(false, "", "", "\\bcategory calendar\\b", "category " + Intent.CATEGORY_APP_CALENDAR),
            RERule(false, "", "", "\\bcategory contacts\\b", "category " + Intent.CATEGORY_APP_CONTACTS),
            RERule(false, "", "", "\\bcategory email\\b", "category " + Intent.CATEGORY_APP_EMAIL),
            RERule(false, "", "", "\\bcategory gallery\\b", "category " + Intent.CATEGORY_APP_GALLERY),
            RERule(false, "", "", "\\bcategory maps\\b", "category " + Intent.CATEGORY_APP_MAPS),
            RERule(false, "", "", "\\bcategory market\\b", "category " + Intent.CATEGORY_APP_MARKET),
            RERule(false, "", "", "\\bcategory messaging\\b", "category " + Intent.CATEGORY_APP_MESSAGING),
            RERule(false, "", "", "\\bcategory music\\b", "category " + Intent.CATEGORY_APP_MUSIC),
            RERule(false, "", "", "\\bcategory default\\b", "category " + Intent.CATEGORY_DEFAULT))
    val RE_EXTRA_GROUP = REGroup(
            false,
            "extra",
            "These rules replace extra shortcuts to full ones", "\\bextra ",
            RERule(false, "", "", "\\bextra email\\b", "extra string " + Intent.EXTRA_EMAIL),
            RERule(false, "", "", "\\bextra text\\b", "extra string " + EX_TEXT),
            RERule(false, "", "", "\\bextra hour\\b", "extra integer " + EX_HOUR),
            RERule(false, "", "", "\\bextra minutes\\b", "extra integer " + EX_MINUTES),
            RERule(false, "", "", "\\bextra message\\b", "extra string " + EX_MESSAGE),
            RERule(false, "", "", "\\bextra length\\b", "extra integer " + EX_LENGTH),
            RERule(false, "", "", "\\bextra quickly\\b", "extra boolean " + EX_SKIP_UI),
            RERule(false, "", "", "\\bextra command\\b", "extra string " + EX_COMMAND))


    val RE_RULES = Arrays.asList(
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
            RE_EXTRA_GROUP)

    fun parseCommand(`in`: String, out: MutableMap<String, String>) {
        val length = `in`.length
        val pattern = Pattern.compile(RE_SEGMENT)
        val matcher = pattern.matcher(`in`)
        while (true) {
            val ok = matcher.lookingAt()
            if (!ok)
                throw IllegalArgumentException("Illegal command format: " + `in`)

            val N = matcher.groupCount()

            //            for(int i = 0; i <= N; ++i) log.d("%d: %s", i, matcher.group(i));

            if (N != 4)
                throw InternalError()

            var keyword = matcher.group(2)
            var value = matcher.group(3)

            if (!sUT) {
                // FIXME NOW: this 'if' is temporary hack
                keyword = Uri.decode(keyword)
                value = Uri.decode(value)
            }

            when (keyword) {
                "extra" -> parseCommandExtraSegment(value, out)
                "flags" -> out.put(keyword, value)
                else -> out.put(keyword, value)
            }//TODO SOMEDAY: what about multiple flags? add some symbolic multiple flags implementation

            val end = matcher.end()
            if (end == length)
                break
            matcher.region(end, length)
        }

    }

    fun parseCommandExtraSegment(extra: String, out: MutableMap<String, String>) {
        val pattern = Pattern.compile(RE_EXTRA_ELEM)
        val matcher = pattern.matcher(extra)
        val ok = matcher.matches()
        if (!ok)
            throw IllegalArgumentException("Illegal extra segment: " + extra)
        val N = matcher.groupCount()
        if (N != 7)
            throw InternalError()

        val type = matcher.group(2)
        val key = matcher.group(3)
        val value = matcher.group(7)

        out.put("extra $type $key", value)

    }

    fun setIntentFromCommand(intent: Intent, cmd: Map<String, String>, log: IMyLogger) {

        for (key in cmd.keys) {
            val value = cmd[key]
            when (key) {
                "start" -> {
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
                "component" -> {
                    val cn = ComponentName.unflattenFromString(value)
                    if (cn == null)
                        log.e(format("Illegal component name: %s", value))
                    intent.component = cn
                }
                "scheme" -> intent.data = Uri.parse(value + ":")
                "bounds" -> intent.sourceBounds = Rect.unflattenFromString(value)
                else -> if (key.startsWith("extra "))
                    setIntentExtra(intent, key.substring("extra ".length), value!!)
                else
                    throw IllegalArgumentException("Illegal intent parameter:" + key)
            }// we don't care here. Should be checked on higher level
        }
    }

    private fun setIntentExtra(intent: Intent, akey: String, value: String) {
        var key = akey
        val pattern = Pattern.compile(RE_EXTRA_ELEM_TYPE_AND_KEY)
        val matcher = pattern.matcher(key)
        val ok = matcher.matches()
        if (!ok)
            throw IllegalArgumentException("Illegal extra type or key: " + key)
        val N = matcher.groupCount()
        if (N != 6)
            throw InternalError()

        val type = matcher.group(2)
        key = matcher.group(3) // now key contains only key (no type)

        when (type) {
            "string" -> intent.putExtra(key, value)
            "boolean" -> intent.putExtra(key, java.lang.Boolean.parseBoolean(value))
            "byte" -> intent.putExtra(key, java.lang.Byte.parseByte(value))
            "char" -> intent.putExtra(key, value[0])
            "double" -> intent.putExtra(key, java.lang.Double.parseDouble(value))
            "float" -> intent.putExtra(key, java.lang.Float.parseFloat(value))
            "integer" -> intent.putExtra(key, Integer.parseInt(value))
            "long" -> intent.putExtra(key, java.lang.Long.parseLong(value))
            "short" -> intent.putExtra(key, java.lang.Short.parseShort(value))
            else -> throw IllegalArgumentException("Illegal extra segment type: " + type)
        }
    }

    fun setBundleFromCommandExtras(bundle: Bundle, cmd: Map<String, String>) {

        for (key in cmd.keys) {
            val value = cmd[key]
            if (key.startsWith("extra ")) {
                setBundleFromExtra(bundle, key.substring("extra ".length), value!!)
            }
        }
    }

    private fun setBundleFromExtra(bundle: Bundle, akey: String, value: String) {
        var key = akey
        val pattern = Pattern.compile(RE_EXTRA_ELEM_TYPE_AND_KEY)
        val matcher = pattern.matcher(key)
        val ok = matcher.matches()
        if (!ok)
            throw IllegalArgumentException("Illegal extra type or key: " + key)
        val N = matcher.groupCount()
        if (N != 6)
            throw InternalError()

        val type = matcher.group(2)
        key = matcher.group(3) // now key contains only key (no type)

        when (type) {
            "string" -> bundle.putString(key, value)
            "boolean" -> bundle.putBoolean(key, java.lang.Boolean.parseBoolean(value))
            "byte" -> bundle.putByte(key, java.lang.Byte.parseByte(value))
            "char" -> bundle.putChar(key, value[0])
            "double" -> bundle.putDouble(key, java.lang.Double.parseDouble(value))
            "float" -> bundle.putFloat(key, java.lang.Float.parseFloat(value))
            "integer" -> bundle.putInt(key, Integer.parseInt(value))
            "long" -> bundle.putLong(key, java.lang.Long.parseLong(value))
            "short" -> bundle.putShort(key, java.lang.Short.parseShort(value))
            else -> throw IllegalArgumentException("Illegal extra segment type: " + type)
        }
    }
}
