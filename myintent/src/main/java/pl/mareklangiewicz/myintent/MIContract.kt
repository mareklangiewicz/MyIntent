package pl.mareklangiewicz.myintent

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import pl.mareklangiewicz.myutils.RERule
import pl.mareklangiewicz.myutils.kclear
import pl.mareklangiewicz.myutils.kinsert
import pl.mareklangiewicz.myutils.kquery
import pl.mareklangiewicz.myutils.str
import pl.mareklangiewicz.myutils.kgetString
import pl.mareklangiewicz.myutils.kgetInt

/**
 * Created by Marek Langiewicz on 07.10.15.
 */
object MIContract {

    val AUTH = BuildConfig.AUTHORITY
    val BASE_URI = Uri.parse("content://$AUTH")!!

    object CmdRecent {

        val PATH = "cmd/recent"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()!!

        val TYPE_DIR = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$AUTH/$PATH"
        val TYPE_ITEM = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$AUTH/$PATH"

        val TABLE_NAME = CmdRecent::class.java.simpleName!!

        val COL_COMMAND = "Command"
        val COL_TIME = "Time"

        fun clear(context: Context): Int = context.contentResolver.kclear(URI)

        fun insert(context: Context, command: String): Uri = context.contentResolver.kinsert(URI) {
            put(COL_COMMAND, command)
            put(COL_TIME, System.currentTimeMillis())
        }

        /**
         * limit <= 0 means no limit (default is 0 - so by default there is no limit clause)
         * returns true if success
         */
        fun load(context: Context, commands: MutableList<String>, limit: Int = 0): Boolean
                = context.contentResolver.kquery(
                uri = URI,
                sortOrder = " $COL_TIME DESC ${if (limit <= 0) "" else " LIMIT ${limit.str} "}"
        ) {
            if (it.moveToFirst())
                do commands.add(it.kgetString(COL_COMMAND))
                while (it.moveToNext())
            true
        }
    }


    object CmdExample {

        val PATH = "cmd/example"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()!!

        val TYPE_DIR = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$AUTH/$PATH"
        val TYPE_ITEM = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$AUTH/$PATH"

        val TABLE_NAME = CmdExample::class.java.simpleName!!

        val COL_COMMAND = "Command"
        val COL_PRIORITY = "Priority"

        fun clear(context: Context): Int = context.contentResolver.kclear(URI)

        fun insert(context: Context, command: String, priority: Long): Uri = context.contentResolver.kinsert(URI) {
            put(COL_COMMAND, command)
            put(COL_PRIORITY, priority)
        }

        fun load(context: Context, commands: MutableList<String>): Boolean = context.contentResolver.kquery(
                uri = URI,
                sortOrder = " $COL_PRIORITY DESC "
        ) {
            if (it.moveToFirst()) do commands.add(it.kgetString(COL_COMMAND))
            while (it.moveToNext())
            true
        }
    }


    object CmdSuggest {

        val PATH = "cmd/${SearchManager.SUGGEST_URI_PATH_QUERY}"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()!!

        val TYPE_DIR = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$AUTH/$PATH"

        val TABLE_NAME = CmdSuggest::class.java.simpleName!!

        val COL_QUERY = SearchManager.SUGGEST_COLUMN_QUERY
        val COL_TEXT = SearchManager.SUGGEST_COLUMN_TEXT_1
        val COL_ICON = SearchManager.SUGGEST_COLUMN_ICON_1
        val COL_PRIORITY = "Priority"
        val COL_TIME = "Time"
    }


    object RuleUser {

        val PATH = "rule/user"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()!!

        val TYPE_DIR = "${ContentResolver.CURSOR_DIR_BASE_TYPE}/$AUTH/$PATH"
        val TYPE_ITEM = "${ContentResolver.CURSOR_ITEM_BASE_TYPE}/$AUTH/$PATH"

        val TABLE_NAME = RuleUser::class.java.simpleName!!

        val COL_POSITION = "Position"
        val COL_EDITABLE = "Editable"
        val COL_NAME = "Name"
        val COL_DESCRIPTION = "Description"
        val COL_MATCH = "Match"
        val COL_REPLACE = "Replace"

        fun clear(context: Context): Int = context.contentResolver.kclear(URI)

        fun insert(context: Context, position: Int, rule: RERule): Uri = context.contentResolver.kinsert(URI) {
            put(COL_POSITION, position)
            put(COL_EDITABLE, rule.editable)
            put(COL_NAME, rule.name)
            put(COL_DESCRIPTION, rule.description)
            put(COL_MATCH, rule.match)
            put(COL_REPLACE, rule.replace)
        }

        fun save(context: Context, rules: List<RERule>) {
            for (i in rules.indices)
                insert(context, i, rules[i])
        }

        fun load(context: Context, rules: MutableList<RERule>): Boolean = context.contentResolver.kquery(
                uri = URI,
                sortOrder = " $COL_POSITION ASC "
        ) {
            if (it.moveToFirst()) do rules.add(
                    RERule(
                            it.kgetString(COL_MATCH),
                            it.kgetString(COL_REPLACE),
                            it.kgetString(COL_NAME),
                            it.kgetString(COL_DESCRIPTION),
                            it.kgetInt(COL_EDITABLE) > 0
                    )
            )
            while (it.moveToNext())
            true
        }
    }
}
