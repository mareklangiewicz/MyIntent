package pl.mareklangiewicz.myintent;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

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

}
