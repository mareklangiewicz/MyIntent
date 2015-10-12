package pl.mareklangiewicz.myintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import pl.mareklangiewicz.myutils.MyCommands;

/**
 * Created by Marek Langiewicz on 07.10.15.
 */
class MIDBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "MyIntentData.db";
    private static final int DATABASE_VERSION = 2;
    private static final String RES_ICON_RECENT = "'android.resource://pl.mareklangiewicz.myintent/" + R.drawable.ic_youtube_searched_for_black_24dp + "'";
    private static final String RES_ICON_EXAMPLE = "'android.resource://pl.mareklangiewicz.myintent/" + R.drawable.ic_find_replace_black_24dp + "'";
    //FIXME SOMEDAY: is 24dp version correct size? looks ok on nexus4...

    public MIDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static void createTable(SQLiteDatabase db, String name, String... columns) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(name);
        sql.append(" ( ");
        for(int i = 0; i < columns.length; ++i) {
            sql.append(columns[i]);
            if(i < columns.length - 1)
                sql.append(" , ");
        }
        sql.append(" ) ");
        db.execSQL(sql.toString());
    }

    static void dropTable(SQLiteDatabase db, String name) {
        db.execSQL("DROP TABLE IF EXISTS " + name);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        createTable(db, MIContract.CmdRecent.TABLE_NAME,
                MIContract.CmdRecent._ID + " INTEGER PRIMARY KEY",
                MIContract.CmdRecent.COL_COMMAND + " TEXT UNIQUE ON CONFLICT REPLACE",
                MIContract.CmdRecent.COL_TIME + " LONG"
        );
        createTable(db, MIContract.CmdExample.TABLE_NAME,
                MIContract.CmdExample._ID + " INTEGER PRIMARY KEY",
                MIContract.CmdExample.COL_COMMAND + " TEXT UNIQUE ON CONFLICT REPLACE",
                MIContract.CmdExample.COL_PRIORITY + " LONG"
        );
        List<String> commands = MyCommands.DEFAULT_EXAMPLE_COMMANDS;
        for(int i = 0; i < commands.size(); ++i) {
            ContentValues values = new ContentValues();
            values.put(MIContract.CmdExample.COL_COMMAND, commands.get(i));
            values.put(MIContract.CmdExample.COL_PRIORITY, commands.size() - i);
            db.insert(MIContract.CmdExample.TABLE_NAME, null, values);
        }
        String sql = "CREATE VIEW " + MIContract.CmdSuggest.TABLE_NAME + " AS " +
                " SELECT " +
                MIContract.CmdRecent._ID + " AS " + MIContract.CmdSuggest._ID + " , " +
                MIContract.CmdRecent.COL_COMMAND + " AS " + MIContract.CmdSuggest.COL_QUERY + " , " +
                MIContract.CmdRecent.COL_COMMAND + " AS " + MIContract.CmdSuggest.COL_TEXT + " , " +
                RES_ICON_RECENT + " AS " + MIContract.CmdSuggest.COL_ICON + " , " +
                " 1000000 AS " + MIContract.CmdSuggest.COL_PRIORITY + " , " +
                MIContract.CmdRecent.COL_TIME + " AS " + MIContract.CmdSuggest.COL_TIME +
                " FROM " + MIContract.CmdRecent.TABLE_NAME +
                " UNION " +
                " SELECT " +
                MIContract.CmdExample._ID + " + 1000000 AS " + MIContract.CmdSuggest._ID + " , " +
                MIContract.CmdExample.COL_COMMAND + " AS " + MIContract.CmdSuggest.COL_QUERY + " , " +
                MIContract.CmdExample.COL_COMMAND + " AS " + MIContract.CmdSuggest.COL_TEXT + " , " +
                RES_ICON_EXAMPLE + " AS " + MIContract.CmdSuggest.COL_ICON + " , " +
                MIContract.CmdExample.COL_PRIORITY + " AS " + MIContract.CmdSuggest.COL_PRIORITY + " , " +
                " 666 AS " + MIContract.CmdSuggest.COL_TIME + // the old times...
                " FROM " + MIContract.CmdExample.TABLE_NAME;

        db.execSQL(sql);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP VIEW " + MIContract.CmdSuggest.TABLE_NAME);
        dropTable(db, MIContract.CmdExample.TABLE_NAME);
        dropTable(db, MIContract.CmdRecent.TABLE_NAME);
        onCreate(db);
    }
}
