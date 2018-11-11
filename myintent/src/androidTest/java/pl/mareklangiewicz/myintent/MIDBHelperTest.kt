package pl.mareklangiewicz.myintent

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.i

/**
 * Created by Marek Langiewicz on 09.10.15.
 */
@RunWith(AndroidJUnit4::class)
class MIDBHelperTest {

    val log = MY_DEFAULT_ANDRO_LOGGER

    val context = InstrumentationRegistry.getTargetContext()!!

    private var mMIDBHelper: MIDBHelper? = null

    @Before fun setUp() {
        context.deleteDatabase(MIDBHelper.DATABASE_NAME)
        mMIDBHelper = MIDBHelper(context)

    }

    @After fun tearDown() {
        mMIDBHelper!!.close()
        mMIDBHelper = null
        context.deleteDatabase(MIDBHelper.DATABASE_NAME)
    }

    @Test fun testGetReadableDatabase() {
        val db = mMIDBHelper!!.readableDatabase!!
        assertThat(db.isOpen).isTrue()
    }

    @Test fun testGetWritableDatabase() {
        val db = mMIDBHelper!!.writableDatabase!!
        assertThat(db.isOpen).isTrue()
    }


    fun logCursor(c: Cursor) {
        val columns = c.columnNames
        log.i("column names:")
        for (i in columns.indices) {
            log.i("col $i: ${columns[i]}")
        }
        var r = 0
        c.moveToFirst()
        while (!c.isAfterLast) {
            log.i("row $r:")
            for (i in 0..c.columnCount - 1) {
                log.i("val ${columns[i]}: ${c.getString(i)}")
            }
            r++
            c.moveToNext()
        }
    }

    fun logTable(db: SQLiteDatabase, table: String) {
        log.i("Table: $table")
        val c = db.rawQuery("SELECT * FROM $table", null)
        logCursor(c)
        c.close()
    }

    @Test fun testCmdRecent() {
        val db = mMIDBHelper!!.writableDatabase!!
        val values = ContentValues()
        values.put(MIContract.CmdRecent.COL_COMMAND, "bla bla commbla")
        values.put(MIContract.CmdRecent.COL_TIME, System.currentTimeMillis())
        var rowid = db.insert(MIContract.CmdRecent.TABLE_NAME, null, values)
        assertThat(rowid).isAtLeast(0)
        values.put(MIContract.CmdRecent.COL_COMMAND, "ble ble bleeee")
        values.put(MIContract.CmdRecent.COL_TIME, System.currentTimeMillis())
        rowid = db.insert(MIContract.CmdRecent.TABLE_NAME, null, values)
        assertThat(rowid).isAtLeast(0)
        logTable(db, "CmdRecent")
        logTable(db, "CmdSuggest")
    }

    @Test fun testRuleUser() {
        val db = mMIDBHelper!!.writableDatabase!!
        val values = ContentValues()
        values.put(MIContract.RuleUser.COL_POSITION, 666)
        values.put(MIContract.RuleUser.COL_EDITABLE, true)
        values.put(MIContract.RuleUser.COL_NAME, "Test rule naaaame")
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "Test rule description")
        values.put(MIContract.RuleUser.COL_MATCH, "Matchfdjsklafj")
        values.put(MIContract.RuleUser.COL_REPLACE, "REplace")
        var rowid = db.insert(MIContract.RuleUser.TABLE_NAME, null, values)
        assertThat(rowid).isAtLeast(0)
        values.put(MIContract.RuleUser.COL_POSITION, 667)
        values.put(MIContract.RuleUser.COL_EDITABLE, false)
        values.put(MIContract.RuleUser.COL_NAME, "second name")
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "second description")
        values.put(MIContract.RuleUser.COL_MATCH, "Match second rule..")
        values.put(MIContract.RuleUser.COL_REPLACE, "Replace second rule")
        rowid = db.insert(MIContract.RuleUser.TABLE_NAME, null, values)
        assertThat(rowid).isAtLeast(0)
        logTable(db, "RuleUser")
    }

    @Test fun testLogTables() {
        val db = mMIDBHelper!!.readableDatabase!!
        logTable(db, "sqlite_master")
        logTable(db, "CmdRecent")
        logTable(db, "CmdExample")
        logTable(db, "CmdSuggest")
        logTable(db, "RuleUser")
        logTable(db, "android_metadata")
    }

}