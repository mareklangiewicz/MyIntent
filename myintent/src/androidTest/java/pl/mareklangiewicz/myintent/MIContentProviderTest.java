package pl.mareklangiewicz.myintent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.noveogroup.android.log.MyOldAndroidLogger;

import java.util.ArrayList;
import java.util.List;

import pl.mareklangiewicz.myutils.IMyLogger;

import static android.net.Uri.parse;
import static java.lang.System.currentTimeMillis;
import static pl.mareklangiewicz.myintent.MIContract.CmdExample;
import static pl.mareklangiewicz.myintent.MIContract.CmdRecent.COL_COMMAND;
import static pl.mareklangiewicz.myintent.MIContract.CmdRecent.COL_TIME;
import static pl.mareklangiewicz.myintent.MIContract.CmdRecent.clear;
import static pl.mareklangiewicz.myintent.MIContract.CmdRecent.insert;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.COL_DESCRIPTION;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.COL_EDITABLE;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.COL_MATCH;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.COL_NAME;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.COL_POSITION;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.COL_REPLACE;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.load;
import static pl.mareklangiewicz.myintent.MIContract.RuleUser.save;
import static pl.mareklangiewicz.myutils.MyCommands.RERule;
import static pl.mareklangiewicz.myutils.MyCommands.RE_USER_GROUP;
import static pl.mareklangiewicz.myutils.MyTextUtilsKt.str;

/**
 * Created by Marek Langiewicz on 10.10.15.
 * These tests are not automated unit/instrumentation tests.
 * They are just for me to run manually one by one and observe
 * what they log in logcat (android monitor).
 *
 * TODO LATER: use new testing support library
 */
public class MIContentProviderTest extends AndroidTestCase {

    private IMyLogger log = new MyOldAndroidLogger();

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testBuildUriMatcher() throws Exception {
        UriMatcher matcher = MIContentProvider.sUriMatcher;
        assertEquals(
                UriMatcher.NO_MATCH,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_RECENT_DIR,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_RECENT_DIR,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent?limit=33"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_RECENT_ITEM,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent/22"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_EXAMPLE_DIR,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_EXAMPLE_ITEM,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example/33"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_SUGGEST_DIR_ALL,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_SUGGEST_DIR_ALL,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query?blabla=ble"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_SUGGEST_DIR_LIKE,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/blabla"))
        );
        assertEquals(
                MIContentProvider.MATCH_CMD_SUGGEST_DIR_LIKE,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/blabla?blu=bli&x=y"))
        );
        assertEquals(
                MIContentProvider.MATCH_RULE_USER_DIR,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/rule/user?limit=33"))
        );
        assertEquals(
                MIContentProvider.MATCH_RULE_USER_ITEM,
                matcher.match(Uri.parse("content://pl.mareklangiewicz.myintent.provider/rule/user/22"))
        );

    }

    @SuppressWarnings("ConstantConditions")
    public void testGetType() throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        log.i(cr.getType(Uri.parse("content://blabla")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent/123")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example/666")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/example/blabla")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/121")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/blabla")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/rule/user")));
        log.i(cr.getType(Uri.parse("content://pl.mareklangiewicz.myintent.provider/rule/user/123")));

    }

    public void testQueryUri(Uri uri) throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        log.w(String.format("testQueryUri: %s", uri.toString()));
        Cursor c = cr.query(uri, null, null, null, null);
        assertNotNull(c);
        logCursor(c);
        c.close();

    }

    public void testQueryUri(String uri) throws Exception {
        testQueryUri(Uri.parse(uri));
    }

    public void testQueryAllCmdRecent() throws Exception {
        testQueryUri(MIContract.CmdRecent.URI);
    }

    public void testQueryAllCmdExample() throws Exception {
        testQueryUri(MIContract.CmdExample.URI);
    }

    public void testQueryAllCmdExampleLimit5() throws Exception {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/example?fjdskljfal&limit=5");
    }

    public void testQueryAllCmdSuggest() throws Exception {
        testQueryUri(MIContract.CmdSuggest.URI);
    }

    public void testQueryAllCmdSuggestLike30() throws Exception {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/30");
    }

    public void testQueryAllCmdSuggestLike8_30() throws Exception {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/8%2030");
    }

    public void testQueryAllCmdSuggestLimit3() throws Exception {
        testQueryUri("content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query?limit=3");
    }

    public void testQueryAllRuleUser() throws Exception {
        testQueryUri(MIContract.RuleUser.URI);
    }


    public void testQuery() throws Exception {

        String uris[] = new String[] {
                "content://pl.mareklangiewicz.myintent.provider/cmd/recent",
                "content://pl.mareklangiewicz.myintent.provider/cmd/example",
                "content://pl.mareklangiewicz.myintent.provider/cmd/example/3",
                "content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query",
                "content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/set",
                "content://pl.mareklangiewicz.myintent.provider/cmd/search_suggest_query/alarm",
                "content://pl.mareklangiewicz.myintent.provider/rule/user",
                "content://pl.mareklangiewicz.myintent.provider/rule/user/0",
                "content://pl.mareklangiewicz.myintent.provider/rule/user/868",
        };

        for(String uri : uris)
            testQueryUri(uri);
    }

    private void logCursor(Cursor c) {
        assertNotNull(c);
        log.i("Cursor:");
        String[] columns = c.getColumnNames();
        for(int i = 0; i < columns.length; ++i)
            log.i(String.format("col %d: %s", i, columns[i]));
        int count = c.getCount();
        if(count == 0) {
            log.i("Cursor is empty (0 rows).");
            return;
        }

        boolean ok = c.moveToFirst();
        assertTrue(ok);
        do {
            log.i(String.format("Row %d:", c.getPosition()));
            for(int i = 0; i < c.getColumnCount(); ++i)
                log.i(String.format("%s: %s", c.getColumnName(i), c.getString(i)));
        }
        while(c.moveToNext());
    }


    public void insertRowTo(ContentValues values, String uri) {

        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        log.i(String.format("insert row to: %s", uri));
        Uri result = cr.insert(Uri.parse(uri), values);
        assertNotNull(result);
        log.i(String.format("uri of inserted row: %s", result.toString()));

    }

    public void testInsert6RowsToCmdRecent() throws Exception {

        ContentValues values = new ContentValues();
        String uri = "content://pl.mareklangiewicz.myintent.provider/cmd/recent";

        values.put(COL_COMMAND, "moja komenda..");
        values.put(COL_TIME, currentTimeMillis());
        insertRowTo(values, uri);

        values.put(COL_COMMAND, "blaaaaaa");
        values.put(COL_TIME, currentTimeMillis());
        insertRowTo(values, uri);

        values.put(COL_COMMAND, "blaa");
        values.put(COL_TIME, currentTimeMillis());
        insertRowTo(values, uri);

        values.put(COL_COMMAND, "cudza komenda..");
        values.put(COL_TIME, currentTimeMillis());
        insertRowTo(values, uri);

        String cmd = "piata komenda";
        log.i(String.format("CmdRecent.insert: %s", cmd));
        insert(getContext(), cmd);
        log.i("done.");
        cmd = "szosta komenda";
        log.i(String.format("CmdRecent.insert: %s", cmd));
        insert(getContext(), cmd);
        log.i("done.");

    }

    public void testInsert6RowsToRuleUser() throws Exception {

        ContentValues values = new ContentValues();
        String uri = "content://pl.mareklangiewicz.myintent.provider/rule/user";

        values.put(COL_POSITION, 666);
        values.put(COL_EDITABLE, true);
        values.put(COL_NAME, "bla");
        values.put(COL_DESCRIPTION, "desc bla");
        values.put(COL_MATCH, "Maaatch %@$#");
        values.put(COL_REPLACE, "Replace $1 $2 $667");
        insertRowTo(values, uri);

        values.put(COL_POSITION, 667);
        values.put(COL_EDITABLE, true);
        values.put(COL_NAME, "ble");
        values.put(COL_DESCRIPTION, "desc ble");
        values.put(COL_MATCH, "Maaatch %ble@$#");
        values.put(COL_REPLACE, "fjdskalfjeplace $1 $2");
        insertRowTo(values, uri);

        values.put(COL_POSITION, 668);
        values.put(COL_EDITABLE, false);
        values.put(COL_NAME, "blaaaa xxxxx");
        values.put(COL_DESCRIPTION, "desc xxxxxx");
        values.put(COL_MATCH, "xxxxxmatchxxxx");
        values.put(COL_REPLACE, "xxxxx replace xxxxx");
        insertRowTo(values, uri);

        values.put(COL_POSITION, 13);
        values.put(COL_EDITABLE, false);
        values.put(COL_NAME, "y");
        values.put(COL_DESCRIPTION, "desc y");
        values.put(COL_MATCH, "Maaatch y");
        values.put(COL_REPLACE, "Replace y");
        insertRowTo(values, uri);

        RERule rule = RE_USER_GROUP.getRules().get(1);
        log.i(String.format("RuleUser.insert: %s", str(rule)));
        RuleUser.insert(getContext(), 77, rule);
        log.i("done.");

        rule = RE_USER_GROUP.getRules().get(2);
        log.i(String.format("RuleUser.insert: %s", str(rule)));
        RuleUser.insert(getContext(), 76, rule);
        log.i("done.");

    }

    public void testUpdateCmdRecentLikeLa() throws Exception { //it will "merge" all updated commands into one (UNIQUE ON CONFLICT REPLACE)
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        ContentValues values = new ContentValues();
        values.put(COL_COMMAND, "UPDATED blaaaaaa!!!!");
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent");
        log.i("update rows from cmd/recent where: Command LIKE '%%la%%'");
        int updated = cr.update(uri, values, " Command LIKE '%la%' ", null);
        log.i(String.format("%s rows updated.", updated));
    }

    public void testUpdateCmdRecentTime() throws Exception { //it change all recent commands time column to current.
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        ContentValues values = new ContentValues();
        values.put(COL_TIME, currentTimeMillis());
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent");
        log.i("update time of all rows from cmd/recent:");
        int updated = cr.update(uri, values, null, null);
        log.i(String.format("%s rows updated.", updated));
    }


    public void testUpdateRuleUserLikeLa() throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        ContentValues values = new ContentValues();
        values.put(COL_NAME, "UPDATED BLEEEEE!!!!");
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/rule/user");
        log.i("update rows from rule/user where: Name LIKE '%%la%%'");
        int updated = cr.update(uri, values, " Name LIKE '%la%' ", null);
        log.i(String.format("%s rows updated.", updated));
    }

    public void testDeleteRecentLikeLa() throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent");
        log.i("delete rows from cmd/recent where: Command LIKE '%%la%%'");
        int deleted = cr.delete(uri, " Command LIKE '%la%' ", null);
        log.i(String.format("%s rows deleted.", deleted));
    }

    public void testDeleteRuleUserLikeLa() throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/rule/user");
        log.i("delete rows from rule/user where: Name LIKE '%%la%%'");
        int deleted = cr.delete(uri, " Name LIKE '%la%' ", null);
        log.i(String.format("%s rows deleted.", deleted));
    }


    public void testDeleteAllRecentManually() throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/cmd/recent");
        log.i("delete all rows from cmd/recent:");
        int deleted = cr.delete(uri, null, null);
        log.i(String.format("%s rows deleted.", deleted));
    }

    public void testDeleteAllRuleUserManually() throws Exception {
        ContentResolver cr = getContext().getContentResolver();
        assertNotNull(cr);
        Uri uri = parse("content://pl.mareklangiewicz.myintent.provider/rule/user");
        log.i("delete all rows from rule/user:");
        int deleted = cr.delete(uri, null, null);
        log.i(String.format("%s rows deleted.", deleted));
    }

    public void testClearRecent() throws Exception {
        log.i("CmdRecent.clear:");
        clear(getContext());
        log.i("done.");
    }

    public void testClearExample() throws Exception {
        log.i("CmdExample.clear:");
        CmdExample.clear(getContext());
        log.i("done.");
    }

    public void testClearRuleUser() throws Exception {
        log.i("RuleUser.clear:");
        RuleUser.clear(getContext());
        log.i("done.");
    }

    public void testSaveRuleUserAllUserRules3Times() throws Exception {
        List<RERule> rules = RE_USER_GROUP.getRules();
        log.i("RuleUser.save 1:");
        save(getContext(), rules);
        log.i("RuleUser.save 2:");
        save(getContext(), rules);
        log.i("RuleUser.save 3:");
        save(getContext(), rules);
        log.i("done.");
    }

    public void testLoadRuleUser() throws Exception {
        log.i("RuleUser.load");
        List<RERule> rules = new ArrayList<>();
        boolean ok = load(getContext(), rules);
        assertTrue(ok);
        log.i("RuleUser.load results:");
        for(int i = 0; i < rules.size(); ++i)
            log.i(str(rules.get(i)));
    }

}
