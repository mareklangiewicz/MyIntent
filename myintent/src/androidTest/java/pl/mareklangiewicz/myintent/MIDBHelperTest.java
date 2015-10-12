package pl.mareklangiewicz.myintent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.noveogroup.android.log.MyLogger;

/**
 * Created by Marek Langiewicz on 09.10.15.
 */
public class MIDBHelperTest extends AndroidTestCase {

    private MyLogger log = new MyLogger("MITest");

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

    public void logCursor(Cursor c) {
        String[] columns = c.getColumnNames();
        log.i("column names:");
        for(int i = 0; i < columns.length; ++i) {
            log.i("col %d: %s", i, columns[i]);
        }
        int r = 0;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            log.i("row %d:", r);
            for(int i = 0; i < c.getColumnCount(); ++i) {
                log.i("val %s: %s", columns[i], c.getString(i));
            }
            r++;
        }
    }

    public void logTable(SQLiteDatabase db, String table) {
        log.i("Table: %s", table);
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

    public void testLogTables() throws Exception {
        SQLiteDatabase db = mMIDBHelper.getReadableDatabase();
        assertNotNull(db);
        logTable(db, "sqlite_master");
        logTable(db, "CmdRecent");
        logTable(db, "CmdExample");
        logTable(db, "CmdSuggest");
        logTable(db, "android_metadata");
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

    public void testOnCreate() throws Exception {

    }

    public void testOnUpgrade() throws Exception {

    }

    public void testCreateTable() throws Exception {

    }

    public void testDropTable() throws Exception {

    }
}