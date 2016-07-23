package pl.mareklangiewicz.myintent

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.net.Uri.parse
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.*
import java.lang.System.currentTimeMillis
import java.util.*

/**
 * Created by Marek Langiewicz on 10.10.15.
 * These tests are not automated unit/instrumentation tests.
 * They are just for me to run manually one by one and observe
 * what they log in logcat (android monitor).
 */
@RunWith(AndroidJUnit4::class)
class MIContentProviderTest {

    val log = MY_DEFAULT_ANDRO_LOGGER

    val context = InstrumentationRegistry.getContext()!!

    val BASE = MIContract.BASE_URI

    @Before fun setUp() {

    }


    @Test fun testGetType() {
        val cr = context.contentResolver!!
        log.i(cr.getType(Uri.parse("content://blabla")))
        log.i(cr.getType(Uri.parse("$BASE")))
        log.i(cr.getType(Uri.parse("$BASE/cmd")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/recent")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/recent/123")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/example")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/example/666")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/example/blabla")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/search_suggest_query")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/search_suggest_query/121")))
        log.i(cr.getType(Uri.parse("$BASE/cmd/search_suggest_query/blabla")))
        log.i(cr.getType(Uri.parse("$BASE/rule/user")))
        log.i(cr.getType(Uri.parse("$BASE/rule/user/123")))

    }

    fun testQueryUri(uri: Uri) {
        val cr = context.contentResolver!!
        log.w("testQueryUri: ${uri.str}")
        val success = cr.kquery(uri) { logCursor(it); true }
        assertThat(success).isTrue()
    }

    fun testQueryUri(uri: String) {
        testQueryUri(Uri.parse(uri))
    }

    @Test fun testQueryAllCmdRecent() {
        testQueryUri(MIContract.CmdRecent.URI)
    }

    @Test fun testQueryAllCmdExample() {
        testQueryUri(MIContract.CmdExample.URI)
    }

    @Test fun testQueryAllCmdExampleLimit5() {
        testQueryUri("$BASE/cmd/example?fjdskljfal&limit=5")
    }

    @Test fun testQueryAllCmdSuggest() {
        testQueryUri(MIContract.CmdSuggest.URI)
    }

    @Test fun testQueryAllCmdSuggestLike30() {
        testQueryUri("$BASE/cmd/search_suggest_query/30")
    }

    @Test fun testQueryAllCmdSuggestLike8_30() {
        testQueryUri("$BASE/cmd/search_suggest_query/8%2030")
    }

    @Test fun testQueryAllCmdSuggestLimit3() {
        testQueryUri("$BASE/cmd/search_suggest_query?limit=3")
    }

    @Test fun testQueryAllRuleUser() {
        testQueryUri(MIContract.RuleUser.URI)
    }


    @Test fun testQuery() {

        val uris = arrayOf(
                "$BASE/cmd/recent",
                "$BASE/cmd/example",
                "$BASE/cmd/example/3",
                "$BASE/cmd/search_suggest_query",
                "$BASE/cmd/search_suggest_query/set",
                "$BASE/cmd/search_suggest_query/alarm",
                "$BASE/rule/user",
                "$BASE/rule/user/0",
                "$BASE/rule/user/868"
        )

        for (uri in uris)
            testQueryUri(uri)
    }

    private fun logCursor(c: Cursor) {
        log.i("Cursor:")
        val columns = c.columnNames
        for (i in columns.indices)
            log.i("col $i: ${columns[i]}")
        val count = c.count
        if (count == 0) {
            log.i("Cursor is empty (0 rows).")
            return
        }

        val ok = c.moveToFirst()
        assertThat(ok).isTrue()
        do {
            log.i("Row ${c.position}:")
            for (i in 0..c.columnCount - 1)
                log.i("${c.getColumnName(i)}: ${c.getString(i)}")
        } while (c.moveToNext())
    }


    fun insertRowTo(values: ContentValues, uri: String) {

        val cr = context.contentResolver!!
        log.i(String.format("insert row to: %s", uri))
        val result = cr.insert(Uri.parse(uri), values)!!
        log.i(String.format("uri of inserted row: %s", result.toString()))

    }

    @Test fun testInsert6RowsToCmdRecent() {

        val values = ContentValues()
        val uri = "$BASE/cmd/recent"

        values.put(MIContract.CmdRecent.COL_COMMAND, "moja komenda..")
        values.put(MIContract.CmdRecent.COL_TIME, currentTimeMillis())
        insertRowTo(values, uri)

        values.put(MIContract.CmdRecent.COL_COMMAND, "blaaaaaa")
        values.put(MIContract.CmdRecent.COL_TIME, currentTimeMillis())
        insertRowTo(values, uri)

        values.put(MIContract.CmdRecent.COL_COMMAND, "blaa")
        values.put(MIContract.CmdRecent.COL_TIME, currentTimeMillis())
        insertRowTo(values, uri)

        values.put(MIContract.CmdRecent.COL_COMMAND, "cudza komenda..")
        values.put(MIContract.CmdRecent.COL_TIME, currentTimeMillis())
        insertRowTo(values, uri)

        var cmd = "piata komenda"
        log.i(String.format("CmdRecent.insert: %s", cmd))
        MIContract.CmdRecent.insert(context, cmd)
        log.i("done.")
        cmd = "szosta komenda"
        log.i(String.format("CmdRecent.insert: %s", cmd))
        MIContract.CmdRecent.insert(context, cmd)
        log.i("done.")

    }

    @Test fun testInsert6RowsToRuleUser() {

        val values = ContentValues()
        val uri = "$BASE/rule/user"

        values.put(MIContract.RuleUser.COL_POSITION, 666)
        values.put(MIContract.RuleUser.COL_EDITABLE, true)
        values.put(MIContract.RuleUser.COL_NAME, "bla")
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "desc bla")
        values.put(MIContract.RuleUser.COL_MATCH, "Maaatch %@$#")
        values.put(MIContract.RuleUser.COL_REPLACE, "Replace $1 $2 $667")
        insertRowTo(values, uri)

        values.put(MIContract.RuleUser.COL_POSITION, 667)
        values.put(MIContract.RuleUser.COL_EDITABLE, true)
        values.put(MIContract.RuleUser.COL_NAME, "ble")
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "desc ble")
        values.put(MIContract.RuleUser.COL_MATCH, "Maaatch %ble@$#")
        values.put(MIContract.RuleUser.COL_REPLACE, "fjdskalfjeplace $1 $2")
        insertRowTo(values, uri)

        values.put(MIContract.RuleUser.COL_POSITION, 668)
        values.put(MIContract.RuleUser.COL_EDITABLE, false)
        values.put(MIContract.RuleUser.COL_NAME, "blaaaa xxxxx")
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "desc xxxxxx")
        values.put(MIContract.RuleUser.COL_MATCH, "xxxxxmatchxxxx")
        values.put(MIContract.RuleUser.COL_REPLACE, "xxxxx replace xxxxx")
        insertRowTo(values, uri)

        values.put(MIContract.RuleUser.COL_POSITION, 13)
        values.put(MIContract.RuleUser.COL_EDITABLE, false)
        values.put(MIContract.RuleUser.COL_NAME, "y")
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "desc y")
        values.put(MIContract.RuleUser.COL_MATCH, "Maaatch y")
        values.put(MIContract.RuleUser.COL_REPLACE, "Replace y")
        insertRowTo(values, uri)

        var rule = RE_SETTINGS_GROUP.rules[1]
        log.i(String.format("RuleUser.insert: %s", rule.str))
        MIContract.RuleUser.insert(context, 77, rule)
        log.i("done.")

        rule = RE_SETTINGS_GROUP.rules[2]
        log.i(String.format("RuleUser.insert: %s", rule.str))
        MIContract.RuleUser.insert(context, 76, rule)
        log.i("done.")

    }

    @Test fun testUpdateCmdRecentLikeLa() {
        //it will "merge" all updated commands into one (UNIQUE ON CONFLICT REPLACE)
        val cr = context.contentResolver!!
        val values = ContentValues()
        values.put(MIContract.CmdRecent.COL_COMMAND, "UPDATED blaaaaaa!!!!")
        val uri = parse("$BASE/cmd/recent")
        log.i("update rows from cmd/recent where: Command LIKE '%%la%%'")
        val updated = cr.update(uri, values, " Command LIKE '%la%' ", null)
        log.i(String.format("%s rows updated.", updated))
    }

    @Test fun testUpdateCmdRecentTime() {
        //it change all recent commands time column to current.
        val cr = context.contentResolver!!
        val values = ContentValues()
        values.put(MIContract.CmdRecent.COL_TIME, currentTimeMillis())
        val uri = parse("$BASE/cmd/recent")
        log.i("update time of all rows from cmd/recent:")
        val updated = cr.update(uri, values, null, null)
        log.i(String.format("%s rows updated.", updated))
    }


    @Test fun testUpdateRuleUserLikeLa() {
        val cr = context.contentResolver!!
        val values = ContentValues()
        values.put(MIContract.RuleUser.COL_NAME, "UPDATED BLEEEEE!!!!")
        val uri = parse("$BASE/rule/user")
        log.i("update rows from rule/user where: Name LIKE '%%la%%'")
        val updated = cr.update(uri, values, " Name LIKE '%la%' ", null)
        log.i(String.format("%s rows updated.", updated))
    }

    @Test fun testDeleteRecentLikeLa() {
        val cr = context.contentResolver!!
        val uri = parse("$BASE/cmd/recent")
        log.i("delete rows from cmd/recent where: Command LIKE '%%la%%'")
        val deleted = cr.delete(uri, " Command LIKE '%la%' ", null)
        log.i(String.format("%s rows deleted.", deleted))
    }

    @Test fun testDeleteRuleUserLikeLa() {
        val cr = context.contentResolver!!
        val uri = parse("$BASE/rule/user")
        log.i("delete rows from rule/user where: Name LIKE '%%la%%'")
        val deleted = cr.delete(uri, " Name LIKE '%la%' ", null)
        log.i(String.format("%s rows deleted.", deleted))
    }


    @Test fun testDeleteAllRecentManually() {
        val cr = context.contentResolver!!
        val uri = parse("$BASE/cmd/recent")
        log.i("delete all rows from cmd/recent:")
        val deleted = cr.delete(uri, null, null)
        log.i(String.format("%s rows deleted.", deleted))
    }

    @Test fun testDeleteAllRuleUserManually() {
        val cr = context.contentResolver!!
        val uri = parse("$BASE/rule/user")
        log.i("delete all rows from rule/user:")
        val deleted = cr.delete(uri, null, null)
        log.i(String.format("%s rows deleted.", deleted))
    }

    @Test fun testClearRecent() {
        log.i("CmdRecent.clear:")
        MIContract.CmdRecent.clear(context)
        log.i("done.")
    }

    @Test fun testClearExample() {
        log.i("CmdExample.clear:")
        MIContract.CmdExample.clear(context)
        log.i("done.")
    }

    @Test fun testClearRuleUser() {
        log.i("RuleUser.clear:")
        MIContract.RuleUser.clear(context)
        log.i("done.")
    }

    @Test fun testSaveRuleUserAllUserRules3Times() {
        val rules = RE_USER_GROUP.rules
        log.i("RuleUser.save 1:")
        MIContract.RuleUser.save(context, rules)
        log.i("RuleUser.save 2:")
        MIContract.RuleUser.save(context, rules)
        log.i("RuleUser.save 3:")
        MIContract.RuleUser.save(context, rules)
        log.i("done.")
    }

    @Test fun testLoadRuleUser() {
        log.i("RuleUser.load")
        val rules = ArrayList<RERule>()
        val ok = MIContract.RuleUser.load(context, rules)
        assertThat(ok).isTrue()
        log.i("RuleUser.load results:")
        for (i in rules.indices)
            log.i(rules[i].str)
    }
}
