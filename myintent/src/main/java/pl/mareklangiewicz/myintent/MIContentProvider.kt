package pl.mareklangiewicz.myintent

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import pl.mareklangiewicz.myintent.MIContract.AUTH
import pl.mareklangiewicz.myintent.MIContract.CmdExample
import pl.mareklangiewicz.myintent.MIContract.CmdRecent
import pl.mareklangiewicz.myintent.MIContract.CmdSuggest
import pl.mareklangiewicz.myintent.MIContract.RuleUser

class MIContentProvider : ContentProvider() {



    private val MATCH_CMD_RECENT_DIR = 101
    private val MATCH_CMD_RECENT_ITEM = 102
    private val MATCH_CMD_EXAMPLE_DIR = 201
    private val MATCH_CMD_EXAMPLE_ITEM = 202
    private val MATCH_CMD_SUGGEST_DIR_ALL = 305
    private val MATCH_CMD_SUGGEST_DIR_LIKE = 306
    private val MATCH_RULE_USER_DIR = 401
    private val MATCH_RULE_USER_ITEM = 402

    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTH, CmdRecent.PATH, MATCH_CMD_RECENT_DIR)
        addURI(AUTH, CmdRecent.PATH + "/#", MATCH_CMD_RECENT_ITEM)
        addURI(AUTH, CmdExample.PATH, MATCH_CMD_EXAMPLE_DIR)
        addURI(AUTH, CmdExample.PATH + "/#", MATCH_CMD_EXAMPLE_ITEM)
        addURI(AUTH, CmdSuggest.PATH, MATCH_CMD_SUGGEST_DIR_ALL)
        addURI(AUTH, CmdSuggest.PATH + "/*", MATCH_CMD_SUGGEST_DIR_LIKE)
        addURI(AUTH, RuleUser.PATH, MATCH_RULE_USER_DIR)
        addURI(AUTH, RuleUser.PATH + "/#", MATCH_RULE_USER_ITEM)
    }

    private lateinit var dbhelper: MIDBHelper

    override fun onCreate(): Boolean {
        dbhelper = MIDBHelper(context!!)
        return true
    }

    // No need to call this method. This to assist the testing framework in running smoothly.
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    override fun shutdown() {
        dbhelper.close()
        super.shutdown()
    }


    @Synchronized override fun getType(uri: Uri): String? {
        return when (matcher.match(uri)) {
            MATCH_CMD_RECENT_DIR -> CmdRecent.TYPE_DIR
            MATCH_CMD_RECENT_ITEM -> CmdRecent.TYPE_ITEM
            MATCH_CMD_EXAMPLE_DIR -> CmdExample.TYPE_DIR
            MATCH_CMD_EXAMPLE_ITEM -> CmdExample.TYPE_ITEM
            MATCH_CMD_SUGGEST_DIR_ALL, MATCH_CMD_SUGGEST_DIR_LIKE -> CmdSuggest.TYPE_DIR
            MATCH_RULE_USER_DIR -> RuleUser.TYPE_DIR
            MATCH_RULE_USER_ITEM -> RuleUser.TYPE_ITEM
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
    }


    @Synchronized override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {

        val db = dbhelper.readableDatabase
        val match = matcher.match(uri)

        val limit = uri.getQueryParameter("limit")

        return when (match) {

             // WARNING: selection and selectionArgs are ignored in this case!
            MATCH_CMD_RECENT_ITEM -> db.query(
                    CmdRecent.TABLE_NAME, projection,
                    " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment),
                    null, null, sortOrder, limit
            )

            MATCH_CMD_RECENT_DIR -> return db.query(
                    CmdRecent.TABLE_NAME, projection,
                    selection, selectionArgs,
                    null, null, sortOrder, limit
            )

             // WARNING: selection and selectionArgs are ignored in this case!
            MATCH_CMD_EXAMPLE_ITEM -> db.query(
                    CmdExample.TABLE_NAME, projection,
                    " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment),
                    null, null, sortOrder, limit
            )

            MATCH_CMD_EXAMPLE_DIR -> db.query(
                    CmdExample.TABLE_NAME, projection,
                    selection, selectionArgs,
                    null, null, sortOrder, limit
            )

            // WARNING: selection and selectionArgs and sortOrder are ignored in this case!
            MATCH_CMD_SUGGEST_DIR_LIKE -> db.query(
                    CmdSuggest.TABLE_NAME, projection,
                    "${SearchManager.SUGGEST_COLUMN_TEXT_1} LIKE ? ", arrayOf("%${Uri.decode(uri.lastPathSegment)}%"),
                    null, null, " ${CmdSuggest.COL_PRIORITY} DESC , ${CmdSuggest.COL_TIME} DESC ", limit
            )

             // WARNING: sortOrder is ignored in this case!
            MATCH_CMD_SUGGEST_DIR_ALL -> db.query(
                    CmdSuggest.TABLE_NAME, projection,
                    selection, selectionArgs,
                    null, null, " ${CmdSuggest.COL_PRIORITY} DESC , ${CmdSuggest.COL_TIME} DESC ", limit
            )

             // WARNING: selection and selectionArgs are ignored in this case!
            MATCH_RULE_USER_ITEM -> db.query(
                    RuleUser.TABLE_NAME, projection,
                    " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment),
                    null, null, sortOrder, limit
            )

            MATCH_RULE_USER_DIR -> db.query(
                    RuleUser.TABLE_NAME, projection,
                    selection, selectionArgs,
                    null, null, sortOrder, limit
            )

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    @Synchronized override fun insert(uri: Uri, values: ContentValues?): Uri? {

        val db = dbhelper.writableDatabase
        val table = when (matcher.match(uri)) {
            MATCH_CMD_RECENT_DIR -> CmdRecent.TABLE_NAME
            MATCH_CMD_EXAMPLE_DIR -> CmdExample.TABLE_NAME
            MATCH_RULE_USER_DIR -> RuleUser.TABLE_NAME
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        val id = db.insert(table, null, values)
        if (id > 0)
            return ContentUris.withAppendedId(uri, id)
        else
            throw android.database.SQLException("Failed to insert row into $uri")
    }



    @Synchronized override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        val db = dbhelper.writableDatabase

        return when (matcher.match(uri)) {
            MATCH_CMD_RECENT_ITEM -> db.delete(CmdRecent.TABLE_NAME, " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment))
            MATCH_CMD_RECENT_DIR -> db.delete(CmdRecent.TABLE_NAME, selection, selectionArgs)
            MATCH_CMD_EXAMPLE_ITEM -> db.delete(CmdExample.TABLE_NAME, " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment))
            MATCH_CMD_EXAMPLE_DIR -> db.delete(CmdExample.TABLE_NAME, selection, selectionArgs)
            MATCH_RULE_USER_ITEM -> db.delete(RuleUser.TABLE_NAME, " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment))
            MATCH_RULE_USER_DIR -> return db.delete(RuleUser.TABLE_NAME, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    @Synchronized override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {

        val db = dbhelper.writableDatabase

        return when (matcher.match(uri)) {
            MATCH_CMD_RECENT_ITEM -> db.update(CmdRecent.TABLE_NAME, values, " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment))
            MATCH_CMD_RECENT_DIR -> db.update(CmdRecent.TABLE_NAME, values, selection, selectionArgs)
            MATCH_CMD_EXAMPLE_ITEM -> db.update(CmdExample.TABLE_NAME, values, " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment))
            MATCH_CMD_EXAMPLE_DIR -> db.update(CmdExample.TABLE_NAME, values, selection, selectionArgs)
            MATCH_RULE_USER_ITEM -> db.update(RuleUser.TABLE_NAME, values, " ${BaseColumns._ID} = ? ", arrayOf(uri.lastPathSegment))
            MATCH_RULE_USER_DIR -> db.update(RuleUser.TABLE_NAME, values, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }
}

