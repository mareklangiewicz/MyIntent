package pl.mareklangiewicz.myintent

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.net.Uri.parse
import android.test.AndroidTestCase
import junit.framework.Assert
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.RERule
import pl.mareklangiewicz.myutils.RE_SETTINGS_GROUP
import pl.mareklangiewicz.myutils.RE_USER_GROUP
import pl.mareklangiewicz.myutils.str
import java.lang.System.currentTimeMillis
import java.util.*

/**
 * Created by Marek Langiewicz on 10.10.15.
 * These tests are not automated unit/instrumentation tests.
 * They are just for me to run manually one by one and observe
 * what they log in logcat (android monitor).

 * TODO LATER: use new testing support library
 */
class MIContentProviderTest : AndroidTestCase() {

    protected val log = MY_DEFAULT_ANDRO_LOGGER

    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()

    }

    @Throws(Exception::class)
    public override fun tearDown() {

    }

    @Throws(Exception::class)
    fun testGetType() {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        log.i(cr.getType(Uri.parse("content://blabla")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent/123")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example/666")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example/blabla")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/121")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/blabla")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/rule/user")))
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/rule/user/123")))

    }

    @Throws(Exception::class)
    fun testQueryUri(uri: Uri) {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        log.w(String.format("testQueryUri: %s", uri.toString()))
        val c = cr.query(uri, null, null, null, null)
        Assert.assertNotNull(c)
        logCursor(c)
        c.close()

    }

    @Throws(Exception::class)
    fun testQueryUri(uri: String) {
        testQueryUri(Uri.parse(uri))
    }

    @Throws(Exception::class)
    fun testQueryAllCmdRecent() {
        testQueryUri(MIContract.CmdRecent.URI)
    }

    @Throws(Exception::class)
    fun testQueryAllCmdExample() {
        testQueryUri(MIContract.CmdExample.URI)
    }

    @Throws(Exception::class)
    fun testQueryAllCmdExampleLimit5() {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/example?fjdskljfal&limit=5")
    }

    @Throws(Exception::class)
    fun testQueryAllCmdSuggest() {
        testQueryUri(MIContract.CmdSuggest.URI)
    }

    @Throws(Exception::class)
    fun testQueryAllCmdSuggestLike30() {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/30")
    }

    @Throws(Exception::class)
    fun testQueryAllCmdSuggestLike8_30() {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/8%2030")
    }

    @Throws(Exception::class)
    fun testQueryAllCmdSuggestLimit3() {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query?limit=3")
    }

    @Throws(Exception::class)
    fun testQueryAllRuleUser() {
        testQueryUri(MIContract.RuleUser.URI)
    }


    @Throws(Exception::class)
    fun testQuery() {

        val uris = arrayOf(
                "content://pl.mareklangiewicz.myintent.provider/cmd/recent",
                "content://pl.mareklangiewicz.myintent.provider/cmd/example",
                "content://pl.mareklangiewicz.myintent.provider/cmd/example/3",
                "content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query",
                "content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/set",
                "content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/alarm",
                "content://pl.mareklangiewicz.myintent.provider/rule/user",
                "content://pl.mareklangiewicz.myintent.provider/rule/user/0",
                "content://pl.mareklangiewicz.myintent.provider/rule/user/868"
        )

        for (uri in uris)
            testQueryUri(uri)
    }

    private fun logCursor(c: Cursor) {
        Assert.assertNotNull(c)
        log.i("Cursor:")
        val columns = c.columnNames
        for (i in columns.indices)
            log.i(String.format("col %d: %s", i, columns[i]))
        val count = c.count
        if (count == 0) {
            log.i("Cursor is empty (0 rows).")
            return
        }

        val ok = c.moveToFirst()
        Assert.assertTrue(ok)
        do {
            log.i(String.format("Row %d:", c.position))
            for (i in 0..c.columnCount - 1)
                log.i(String.format("%s: %s", c.getColumnName(i), c.getString(i)))
        } while (c.moveToNext())
    }


    fun insertRowTo(values: ContentValues, uri: String) {

        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        log.i(String.format("insert row to: %s", uri))
        val result = cr.insert(Uri.parse(uri), values)
        Assert.assertNotNull(result)
        log.i(String.format("uri of inserted row: %s", result.toString()))

    }

    @Throws(Exception::class)
    fun testInsert6RowsToCmdRecent() {

        val values = ContentValues()
        val uri = "content://pl.mareklangiewicz.myintent.provider/cmd/recent"

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

    @Throws(Exception::class)
    fun testInsert6RowsToRuleUser() {

        val values = ContentValues()
        val uri = "content://pl.mareklangiewicz.myintent.provider/rule/user"

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

    @Throws(Exception::class)
    fun testUpdateCmdRecentLikeLa() {
        //it will "merge" all updated commands into one (UNIQUE ON CONFLICT REPLACE)
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val values = ContentValues()
        values.put(MIContract.CmdRecent.COL_COMMAND, "UPDATED blaaaaaa!!!!")
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent")
        log.i("update rows from cmd/recent where: Command LIKE '%%la%%'")
        val updated = cr.update(uri, values, " Command LIKE '%la%' ", null)
        log.i(String.format("%s rows updated.", updated))
    }

    @Throws(Exception::class)
    fun testUpdateCmdRecentTime() {
        //it change all recent commands time column to current.
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val values = ContentValues()
        values.put(MIContract.CmdRecent.COL_TIME, currentTimeMillis())
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent")
        log.i("update time of all rows from cmd/recent:")
        val updated = cr.update(uri, values, null, null)
        log.i(String.format("%s rows updated.", updated))
    }


    @Throws(Exception::class)
    fun testUpdateRuleUserLikeLa() {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val values = ContentValues()
        values.put(MIContract.RuleUser.COL_NAME, "UPDATED BLEEEEE!!!!")
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/rule/user")
        log.i("update rows from rule/user where: Name LIKE '%%la%%'")
        val updated = cr.update(uri, values, " Name LIKE '%la%' ", null)
        log.i(String.format("%s rows updated.", updated))
    }

    @Throws(Exception::class)
    fun testDeleteRecentLikeLa() {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent")
        log.i("delete rows from cmd/recent where: Command LIKE '%%la%%'")
        val deleted = cr.delete(uri, " Command LIKE '%la%' ", null)
        log.i(String.format("%s rows deleted.", deleted))
    }

    @Throws(Exception::class)
    fun testDeleteRuleUserLikeLa() {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/rule/user")
        log.i("delete rows from rule/user where: Name LIKE '%%la%%'")
        val deleted = cr.delete(uri, " Name LIKE '%la%' ", null)
        log.i(String.format("%s rows deleted.", deleted))
    }


    @Throws(Exception::class)
    fun testDeleteAllRecentManually() {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent")
        log.i("delete all rows from cmd/recent:")
        val deleted = cr.delete(uri, null, null)
        log.i(String.format("%s rows deleted.", deleted))
    }

    @Throws(Exception::class)
    fun testDeleteAllRuleUserManually() {
        val cr = context.contentResolver
        Assert.assertNotNull(cr)
        val uri = parse("content://pl.mareklangiewicz.myintent.provider/rule/user")
        log.i("delete all rows from rule/user:")
        val deleted = cr.delete(uri, null, null)
        log.i(String.format("%s rows deleted.", deleted))
    }

    @Throws(Exception::class)
    fun testClearRecent() {
        log.i("CmdRecent.clear:")
        MIContract.CmdRecent.clear(context)
        log.i("done.")
    }

    @Throws(Exception::class)
    fun testClearExample() {
        log.i("CmdExample.clear:")
        MIContract.CmdExample.clear(context)
        log.i("done.")
    }

    @Throws(Exception::class)
    fun testClearRuleUser() {
        log.i("RuleUser.clear:")
        MIContract.RuleUser.clear(context)
        log.i("done.")
    }

    @Throws(Exception::class)
    fun testSaveRuleUserAllUserRules3Times() {
        val rules = RE_USER_GROUP.rules
        log.i("RuleUser.save 1:")
        MIContract.RuleUser.save(context, rules)
        log.i("RuleUser.save 2:")
        MIContract.RuleUser.save(context, rules)
        log.i("RuleUser.save 3:")
        MIContract.RuleUser.save(context, rules)
        log.i("done.")
    }

    @Throws(Exception::class)
    fun testLoadRuleUser() {
        log.i("RuleUser.load")
        val rules = ArrayList<RERule>()
        val ok = MIContract.RuleUser.load(context, rules)
        Assert.assertTrue(ok)
        log.i("RuleUser.load results:")
        for (i in rules.indices)
            log.i(rules[i].str)
    }

}
