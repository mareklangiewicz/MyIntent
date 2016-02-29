package pl.mareklangiewicz.myintent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.noveogroup.android.log.MyAndroidLogger;

import pl.mareklangiewicz.myutils.IMyLogger;

/**
 * Created by Marek Langiewicz on 09.10.15.
 *
 * TODO LATER: use new testing support library
 */
public class MIDBHelperTest extends AndroidTestCase {

    private IMyLogger log = new MyAndroidLogger("MITest");

    private MIDBHelper mMIDBHelper;

    public void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase(MIDBHelper.DATABASE_NAME);
        mMIDBHelper = new MIDBHelper(getContext());

    }

    public void tearDown() throws Exception {
        mMIDBHelper.close();
        mMIDBHelper = null;
        getContext().deleteDatabase(MIDBHelper.DATABASE_NAME);
    }

    public void testGetReadableDatabase() throws Exception {
        SQLiteDatabase db = mMIDBHelper.getReadableDatabase();
        assertNotNull(db);
        assertTrue(db.isOpen());
    }

    public void testGetWritableDatabase() throws Exception {
        SQLiteDatabase db = mMIDBHelper.getWritableDatabase();
        assertNotNull(db);
        assertTrue(db.isOpen());
    }


    public void logCursor(Cursor c) {
        String[] columns = c.getColumnNames();
        log.i("column names:", null);
        for(int i = 0; i < columns.length; ++i) {
            log.i(String.format("col %d: %s", i, columns[i]), null);
        }
        int r = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            log.i(String.format("row %d:", r), null);
            for(int i = 0; i < c.getColumnCount(); ++i) {
                log.i(String.format("val %s: %s", columns[i], c.getString(i)), null);
            }
            r++;
        }
    }

    public void logTable(SQLiteDatabase db, String table) {
        log.i(String.format("Table: %s", table), null);
        Cursor c = db.rawQuery("SELECT * FROM " + table, null);
        logCursor(c);
        c.close();
    }

    public void testCmdRecent() throws Exception {
        SQLiteDatabase db = mMIDBHelper.getWritableDatabase();
        assertNotNull(db);
        ContentValues values = new ContentValues();
        values.put(MIContract.CmdRecent.COL_COMMAND, "bla bla commbla");
        values.put(MIContract.CmdRecent.COL_TIME, System.currentTimeMillis());
        long rowid = db.insert(MIContract.CmdRecent.TABLE_NAME, null, values);
        assertTrue(rowid >= 0);
        values.put(MIContract.CmdRecent.COL_COMMAND, "ble ble bleeee");
        values.put(MIContract.CmdRecent.COL_TIME, System.currentTimeMillis());
        rowid = db.insert(MIContract.CmdRecent.TABLE_NAME, null, values);
        assertTrue(rowid >= 0);
        logTable(db, "CmdRecent");
        logTable(db, "CmdSuggest");
    }

    public void testRuleUser() throws Exception {
        SQLiteDatabase db = mMIDBHelper.getWritableDatabase();
        assertNotNull(db);
        ContentValues values = new ContentValues();
        values.put(MIContract.RuleUser.COL_POSITION, 666);
        values.put(MIContract.RuleUser.COL_EDITABLE, true);
        values.put(MIContract.RuleUser.COL_NAME, "Test rule naaaame");
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "Test rule description");
        values.put(MIContract.RuleUser.COL_MATCH, "Matchfdjsklafj");
        values.put(MIContract.RuleUser.COL_REPLACE, "REplace");
        long rowid = db.insert(MIContract.RuleUser.TABLE_NAME, null, values);
        assertTrue(rowid >= 0);
        values.put(MIContract.RuleUser.COL_POSITION, 667);
        values.put(MIContract.RuleUser.COL_EDITABLE, false);
        values.put(MIContract.RuleUser.COL_NAME, "second name");
        values.put(MIContract.RuleUser.COL_DESCRIPTION, "second description");
        values.put(MIContract.RuleUser.COL_MATCH, "Match second rule..");
        values.put(MIContract.RuleUser.COL_REPLACE, "Replace second rule");
        rowid = db.insert(MIContract.RuleUser.TABLE_NAME, null, values);
        assertTrue(rowid >= 0);
        logTable(db, "RuleUser");
    }

    public void testLogTables() throws Exception {
        SQLiteDatabase db = mMIDBHelper.getReadableDatabase();
        assertNotNull(db);
        logTable(db, "sqlite_master");
        logTable(db, "CmdRecent");
        logTable(db, "CmdExample");
        logTable(db, "CmdSuggest");
        logTable(db, "RuleUser");
        logTable(db, "android_metadata");
    }

    public void testOnCreate() throws Exception {

    }

    public void testOnUpgrade() throws Exception {

    }

    public void testCreateTable() throws Exception {

    }

    public void testDropTable() throws Exception {

    }
}