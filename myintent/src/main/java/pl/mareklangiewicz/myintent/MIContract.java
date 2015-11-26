package pl.mareklangiewicz.myintent;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.List;

import pl.mareklangiewicz.myutils.MyCommands;

import static pl.mareklangiewicz.myutils.MyTextUtils.str;

/**
 * Created by Marek Langiewicz on 07.10.15.
 */
public final class MIContract {

    public static final String AUTH = "pl.mareklangiewicz.myintent.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTH);

    private MIContract() {
        throw new AssertionError("MIContract class is noninstantiable.");
    }

    public static final class CmdRecent implements BaseColumns {

        public static final String PATH = "cmd/recent";

        public static final Uri URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build();

        public static final String TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH;
        public static final String TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTH + "/" + PATH;

        public static final String TABLE_NAME = CmdRecent.class.getSimpleName();

        public static final String COL_COMMAND = "Command";
        public static final String COL_TIME = "Time";

        public static int clear(Context context) {
            ContentResolver cr = context.getContentResolver();
            return cr.delete(URI, null, null);
        }

        public static Uri insert(Context context, String command) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(COL_COMMAND, command);
            values.put(COL_TIME, System.currentTimeMillis());
            return cr.insert(URI, values);
        }

        /**
         * limit <= 0 means no limit
         * returns true if success
         */
        public static boolean load(Context context, List<String> commands, int limit) {

            String slimit = limit <= 0 ? "" : " LIMIT " + str(limit) + " ";
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(URI, null, null, null, " " + COL_TIME + " DESC " + slimit);
            if(c == null)
                return false;
            boolean empty = !c.moveToFirst();
            if(empty) {
                c.close();
                return true;
            }
            do commands.add(c.getString(c.getColumnIndex(COL_COMMAND)));
            while(c.moveToNext());
            c.close();
            return true;
        }

    }


    public static final class CmdExample implements BaseColumns {

        public static final String PATH = "cmd/example";

        public static final Uri URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build();

        public static final String TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH;
        public static final String TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTH + "/" + PATH;

        public static final String TABLE_NAME = CmdExample.class.getSimpleName();

        public static final String COL_COMMAND = "Command";
        public static final String COL_PRIORITY = "Priority";

        public static int clear(Context context) {
            ContentResolver cr = context.getContentResolver();
            return cr.delete(URI, null, null);
        }

        public static Uri insert(Context context, String command, long priority) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(COL_COMMAND, command);
            values.put(COL_PRIORITY, priority);
            return cr.insert(URI, values);
        }

        public static boolean load(Context context, List<String> commands) {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(URI, null, null, null, " " + COL_PRIORITY + " DESC ");
            if(c == null)
                return false;
            boolean empty = !c.moveToFirst();
            if(empty) {
                c.close();
                return true;
            }
            do commands.add(c.getString(c.getColumnIndex(COL_COMMAND)));
            while(c.moveToNext());
            c.close();
            return true;
        }
    }


    public static final class CmdSuggest implements BaseColumns {

        public static final String PATH = "cmd/" + SearchManager.SUGGEST_URI_PATH_QUERY;

        public static final Uri URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build();

        public static final String TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH;

        public static final String TABLE_NAME = CmdSuggest.class.getSimpleName();

        public static final String COL_QUERY = SearchManager.SUGGEST_COLUMN_QUERY;
        public static final String COL_TEXT = SearchManager.SUGGEST_COLUMN_TEXT_1;
        public static final String COL_ICON = SearchManager.SUGGEST_COLUMN_ICON_1;
        public static final String COL_PRIORITY = "Priority";
        public static final String COL_TIME = "Time";
    }

    public static final class RuleUser implements BaseColumns {

        public static final String PATH = "rule/user";

        public static final Uri URI = BASE_URI.buildUpon().appendEncodedPath(PATH).build();

        public static final String TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTH + "/" + PATH;
        public static final String TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTH + "/" + PATH;

        public static final String TABLE_NAME = RuleUser.class.getSimpleName();

        public static final String COL_POSITION = "Position";
        public static final String COL_EDITABLE = "Editable";
        public static final String COL_NAME = "Name";
        public static final String COL_DESCRIPTION = "Description";
        public static final String COL_MATCH = "Match";
        public static final String COL_REPLACE = "Replace";

        public static int clear(Context context) {
            ContentResolver cr = context.getContentResolver();
            return cr.delete(URI, null, null);
        }

        public static Uri insert(Context context, int position, MyCommands.RERule rule) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(COL_POSITION, position);
            values.put(COL_EDITABLE, rule.getEditable());
            values.put(COL_NAME, rule.getName());
            values.put(COL_DESCRIPTION, rule.getDescription());
            values.put(COL_MATCH, rule.getMatch());
            values.put(COL_REPLACE, rule.getReplace());
            return cr.insert(URI, values);
        }

        public static void save(Context context, List<MyCommands.RERule> rules) {
            for(int i = 0; i < rules.size(); ++i)
                insert(context, i, rules.get(i));
        }

        public static boolean load(Context context, List<MyCommands.RERule> rules) {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(URI, null, null, null, " " + COL_POSITION + " ASC ");
            if(c == null)
                return false;
            boolean empty = !c.moveToFirst();
            if(empty) {
                c.close();
                return true;
            }
            do {
                rules.add(
                        new MyCommands.RERule(
                                0 < c.getInt(c.getColumnIndex(COL_EDITABLE)),
                                c.getString(c.getColumnIndex(COL_NAME)),
                                c.getString(c.getColumnIndex(COL_DESCRIPTION)),
                                c.getString(c.getColumnIndex(COL_MATCH)),
                                c.getString(c.getColumnIndex(COL_REPLACE))
                        )
                );
            }
            while(c.moveToNext());
            c.close();
            return true;
        }


    }

}
