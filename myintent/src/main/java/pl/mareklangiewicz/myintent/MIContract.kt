package pl.mareklangiewicz.myintent

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import clear
import getInt
import getString
import insert
import pl.mareklangiewicz.myutils.MyCommands
import pl.mareklangiewicz.myutils.str

/**
 * Created by Marek Langiewicz on 07.10.15.
 */
object MIContract {

    val AUTH = "pl.mareklangiewicz.myintent.provider"
    val BASE_URI = Uri.parse("content://" + AUTH)

    object CmdRecent {

        val PATH = "cmd/recent"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()

        val TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH
        val TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTH + "/" + PATH

        val TABLE_NAME = CmdRecent::class.java.simpleName

        val COL_COMMAND = "Command"
        val COL_TIME = "Time"

        fun clear(context: Context): Int = context.contentResolver.clear(URI)

        fun insert(context: Context, command: String): Uri = context.contentResolver.insert(URI) {
            put(COL_COMMAND, command)
            put(COL_TIME, System.currentTimeMillis())
        }

        /**
         * limit <= 0 means no limit (default is 0 - so by default there is no limit clause)
         * returns true if success
         */
        fun load(context: Context, commands: MutableList<String>, limit: Int = 0): Boolean {

            val slimit = if (limit <= 0) "" else " LIMIT ${limit.str} "
            val cursor = context.contentResolver.query(URI, null, null, null, " $COL_TIME DESC $slimit") ?: return false
            cursor.use {
                if (!it.moveToFirst()) return true // empty.
                do commands.add(it.getString(COL_COMMAND))
                while (it.moveToNext())
                return true
            }
        }

    }


    object CmdExample {

        val PATH = "cmd/example"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()

        val TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH
        val TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTH + "/" + PATH

        val TABLE_NAME = CmdExample::class.java.simpleName

        val COL_COMMAND = "Command"
        val COL_PRIORITY = "Priority"

        fun clear(context: Context): Int = context.contentResolver.clear(URI)

        fun insert(context: Context, command: String, priority: Long): Uri = context.contentResolver.insert(URI) {
            put(COL_COMMAND, command)
            put(COL_PRIORITY, priority)
        }

        fun load(context: Context, commands: MutableList<String>): Boolean {

            val cursor = context.contentResolver.query(URI, null, null, null, " $COL_PRIORITY DESC ") ?: return false
            cursor.use {
                if (!it.moveToFirst()) return true // empty.
                do commands.add(it.getString(COL_COMMAND))
                while (it.moveToNext())
                return true
            }
        }
    }


    object CmdSuggest {

        val PATH = "cmd/" + SearchManager.SUGGEST_URI_PATH_QUERY

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()

        val TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH

        val TABLE_NAME = CmdSuggest::class.java.simpleName

        val COL_QUERY = SearchManager.SUGGEST_COLUMN_QUERY
        val COL_TEXT = SearchManager.SUGGEST_COLUMN_TEXT_1
        val COL_ICON = SearchManager.SUGGEST_COLUMN_ICON_1
        val COL_PRIORITY = "Priority"
        val COL_TIME = "Time"
    }


    object RuleUser {

        val PATH = "rule/user"

        val URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build()

        val TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH
        val TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTH + "/" + PATH

        val TABLE_NAME = RuleUser::class.java.simpleName

        val COL_POSITION = "Position"
        val COL_EDITABLE = "Editable"
        val COL_NAME = "Name"
        val COL_DESCRIPTION = "Description"
        val COL_MATCH = "Match"
        val COL_REPLACE = "Replace"

        fun clear(context: Context): Int = context.contentResolver.clear(URI)

        fun insert(context: Context, position: Int, rule: MyCommands.RERule): Uri = context.contentResolver.insert(URI) {
            put(COL_POSITION, position)
            put(COL_EDITABLE, rule.editable)
            put(COL_NAME, rule.name)
            put(COL_DESCRIPTION, rule.description)
            put(COL_MATCH, rule.match)
            put(COL_REPLACE, rule.replace)
        }

        fun save(context: Context, rules: List<MyCommands.RERule>) {
            for (i in rules.indices)
                insert(context, i, rules[i])
        }

        fun load(context: Context, rules: MutableList<MyCommands.RERule>): Boolean {

            val cursor = context.contentResolver.query(URI, null, null, null, " $COL_POSITION ASC ") ?: return false
            cursor.use {
                if (!it.moveToFirst()) return true // empty.
                do rules.add(
                        MyCommands.RERule(
                                it.getInt(COL_EDITABLE) > 0,
                                it.getString(COL_NAME),
                                it.getString(COL_DESCRIPTION),
                                it.getString(COL_MATCH),
                                it.getString(COL_REPLACE)
                        )
                )
                while (it.moveToNext())
                return true
            }
        }
    }
}
