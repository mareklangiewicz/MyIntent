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
    private static final String RES_ICON_RECENT  = "'android.resource://pl.mareklangiewicz.myintent/drawable/mi_ic_recent_command_black_24dp'";
    private static final String RES_ICON_EXAMPLE = "'android.resource://pl.mareklangiewicz.myintent/drawable/mi_ic_example_command_black_24dp'";
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
        createTable(db, MIContract.CmdRecent.INSTANCE.getTABLE_NAME(),
                MIContract.CmdRecent._ID + " INTEGER PRIMARY KEY",
                MIContract.CmdRecent.INSTANCE.getCOL_COMMAND() + " TEXT UNIQUE ON CONFLICT REPLACE",
                MIContract.CmdRecent.INSTANCE.getCOL_TIME() + " LONG"
        );
        createTable(db, MIContract.CmdExample.INSTANCE.getTABLE_NAME(),
                MIContract.CmdExample._ID + " INTEGER PRIMARY KEY",
                MIContract.CmdExample.INSTANCE.getCOL_COMMAND() + " TEXT UNIQUE ON CONFLICT REPLACE",
                MIContract.CmdExample.INSTANCE.getCOL_PRIORITY() + " LONG"
        );
        List<String> commands = MIExamples.EXAMPLE_COMMANDS;
        for(int i = 0; i < commands.size(); ++i) {
            ContentValues values = new ContentValues();
            values.put(MIContract.CmdExample.INSTANCE.getCOL_COMMAND(), commands.get(i));
            values.put(MIContract.CmdExample.INSTANCE.getCOL_PRIORITY(), commands.size() - i);
            db.insert(MIContract.CmdExample.INSTANCE.getTABLE_NAME(), null, values);
        }
        String sql = "CREATE VIEW " + MIContract.CmdSuggest.INSTANCE.getTABLE_NAME() + " AS " +
                " SELECT " +
                MIContract.CmdRecent._ID + " AS " + MIContract.CmdSuggest._ID + " , " +
                MIContract.CmdRecent.INSTANCE.getCOL_COMMAND() + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_QUERY() + " , " +
                MIContract.CmdRecent.INSTANCE.getCOL_COMMAND() + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_TEXT() + " , " +
                RES_ICON_RECENT + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_ICON() + " , " +
                " 1000000 AS " + MIContract.CmdSuggest.INSTANCE.getCOL_PRIORITY() + " , " +
                MIContract.CmdRecent.INSTANCE.getCOL_TIME() + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_TIME() +
                " FROM " + MIContract.CmdRecent.INSTANCE.getTABLE_NAME() +
                " UNION " +
                " SELECT " +
                MIContract.CmdExample._ID + " + 1000000 AS " + MIContract.CmdSuggest._ID + " , " +
                MIContract.CmdExample.INSTANCE.getCOL_COMMAND() + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_QUERY() + " , " +
                MIContract.CmdExample.INSTANCE.getCOL_COMMAND() + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_TEXT() + " , " +
                RES_ICON_EXAMPLE + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_ICON() + " , " +
                MIContract.CmdExample.INSTANCE.getCOL_PRIORITY() + " AS " + MIContract.CmdSuggest.INSTANCE.getCOL_PRIORITY() + " , " +
                " 666 AS " + MIContract.CmdSuggest.INSTANCE.getCOL_TIME() + // the old times...
                " FROM " + MIContract.CmdExample.INSTANCE.getTABLE_NAME();

        db.execSQL(sql);

        createTable(db, MIContract.RuleUser.INSTANCE.getTABLE_NAME(),
                MIContract.RuleUser._ID + " INTEGER PRIMARY KEY",
                MIContract.RuleUser.INSTANCE.getCOL_POSITION() + " INTEGER",
                MIContract.RuleUser.INSTANCE.getCOL_EDITABLE() + " INTEGER",
                MIContract.RuleUser.INSTANCE.getCOL_NAME() + " TEXT",
                MIContract.RuleUser.INSTANCE.getCOL_DESCRIPTION() + " TEXT",
                MIContract.RuleUser.INSTANCE.getCOL_MATCH() + " TEXT",
                MIContract.RuleUser.INSTANCE.getCOL_REPLACE() + " TEXT"
        );
        List<MyCommands.RERule> rules = MIExamples.EXAMPLE_RULES;
        for(int i = 0; i < rules.size(); ++i) {
            MyCommands.RERule rule = rules.get(i);
            ContentValues values = new ContentValues();
            values.put(MIContract.RuleUser.INSTANCE.getCOL_POSITION(), i);
            values.put(MIContract.RuleUser.INSTANCE.getCOL_EDITABLE(), rule.getEditable());
            values.put(MIContract.RuleUser.INSTANCE.getCOL_NAME(), rule.getName());
            values.put(MIContract.RuleUser.INSTANCE.getCOL_DESCRIPTION(), rule.getDescription());
            values.put(MIContract.RuleUser.INSTANCE.getCOL_MATCH(), rule.getMatch());
            values.put(MIContract.RuleUser.INSTANCE.getCOL_REPLACE(), rule.getReplace());
            db.insert(MIContract.RuleUser.INSTANCE.getTABLE_NAME(), null, values);
        }
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP VIEW " + MIContract.CmdSuggest.INSTANCE.getTABLE_NAME());
        dropTable(db, MIContract.CmdExample.INSTANCE.getTABLE_NAME());
        dropTable(db, MIContract.CmdRecent.INSTANCE.getTABLE_NAME());
        dropTable(db, MIContract.RuleUser.INSTANCE.getTABLE_NAME());
        onCreate(db);
    }
}
