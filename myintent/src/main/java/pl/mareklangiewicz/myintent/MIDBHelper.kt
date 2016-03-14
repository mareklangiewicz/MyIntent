package pl.mareklangiewicz.myintent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import createTable
import dropTable
import pl.mareklangiewicz.myintent.MIContract.CmdExample
import pl.mareklangiewicz.myintent.MIContract.CmdRecent
import pl.mareklangiewicz.myintent.MIContract.CmdSuggest
import pl.mareklangiewicz.myintent.MIContract.RuleUser


/**
 * Created by Marek Langiewicz on 07.10.15.
 */
internal class MIDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        const val DATABASE_NAME = "MyIntentData.db"

        private val DATABASE_VERSION = 2

        private val RES_ICON_RECENT = "'android.resource://pl.mareklangiewicz.myintent/drawable/mi_ic_recent_command_black_24dp'"
        private val RES_ICON_EXAMPLE = "'android.resource://pl.mareklangiewicz.myintent/drawable/mi_ic_example_command_black_24dp'"
        //FIXME SOMEDAY: is 24dp version correct size? looks ok on nexus4...

    }

    override fun onCreate(db: SQLiteDatabase) {

        db.createTable(CmdRecent.TABLE_NAME,
                BaseColumns._ID + " INTEGER PRIMARY KEY",
                CmdRecent.COL_COMMAND + " TEXT UNIQUE ON CONFLICT REPLACE",
                CmdRecent.COL_TIME + " LONG")

        db.createTable(CmdExample.TABLE_NAME,
                BaseColumns._ID + " INTEGER PRIMARY KEY",
                CmdExample.COL_COMMAND + " TEXT UNIQUE ON CONFLICT REPLACE",
                CmdExample.COL_PRIORITY + " LONG")

        val commands = MIExamples.EXAMPLE_COMMANDS

        for (iv in commands.withIndex()) db.insert(CmdExample.TABLE_NAME, null, ContentValues().apply {
            put(CmdExample.COL_COMMAND, iv.value)
            put(CmdExample.COL_PRIORITY, commands.size - iv.index)
        })

        val sql = "CREATE VIEW " + CmdSuggest.TABLE_NAME + " AS " +
                " SELECT " +
                BaseColumns._ID + " AS " + BaseColumns._ID + " , " +
                CmdRecent.COL_COMMAND + " AS " + CmdSuggest.COL_QUERY + " , " +
                CmdRecent.COL_COMMAND + " AS " + CmdSuggest.COL_TEXT + " , " +
                RES_ICON_RECENT + " AS " + CmdSuggest.COL_ICON + " , " +
                " 1000000 AS " + CmdSuggest.COL_PRIORITY + " , " +
                CmdRecent.COL_TIME + " AS " + CmdSuggest.COL_TIME +
                " FROM " + CmdRecent.TABLE_NAME +
                " UNION " +
                " SELECT " +
                BaseColumns._ID + " + 1000000 AS " + BaseColumns._ID + " , " +
                CmdExample.COL_COMMAND + " AS " + CmdSuggest.COL_QUERY + " , " +
                CmdExample.COL_COMMAND + " AS " + CmdSuggest.COL_TEXT + " , " +
                RES_ICON_EXAMPLE + " AS " + CmdSuggest.COL_ICON + " , " +
                CmdExample.COL_PRIORITY + " AS " + CmdSuggest.COL_PRIORITY + " , " +
                " 666 AS " + CmdSuggest.COL_TIME + // the old times...
                " FROM " + CmdExample.TABLE_NAME

        db.execSQL(sql)

        db.createTable(RuleUser.TABLE_NAME,
                BaseColumns._ID + " INTEGER PRIMARY KEY",
                RuleUser.COL_POSITION + " INTEGER",
                RuleUser.COL_EDITABLE + " INTEGER",
                RuleUser.COL_NAME + " TEXT",
                RuleUser.COL_DESCRIPTION + " TEXT",
                RuleUser.COL_MATCH + " TEXT",
                RuleUser.COL_REPLACE + " TEXT")

        val rules = MIExamples.EXAMPLE_RULES

        for (iv in rules.withIndex()) db.insert(RuleUser.TABLE_NAME, null, ContentValues().apply {
            put(RuleUser.COL_POSITION, iv.index)
            put(RuleUser.COL_EDITABLE, iv.value.editable)
            put(RuleUser.COL_NAME, iv.value.name)
            put(RuleUser.COL_DESCRIPTION, iv.value.description)
            put(RuleUser.COL_MATCH, iv.value.match)
            put(RuleUser.COL_REPLACE, iv.value.replace)
        })
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP VIEW " + CmdSuggest.TABLE_NAME)
        db.dropTable(CmdExample.TABLE_NAME)
        db.dropTable(CmdRecent.TABLE_NAME)
        db.dropTable(RuleUser.TABLE_NAME)
        onCreate(db)
    }

}
