package pl.mareklangiewicz.myintent;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import pl.mareklangiewicz.myutils.MyCommands;

/**
 * Created by Marek Langiewicz on 24.11.15.
 */
public class RecentCommandsRemoteViewsService extends RemoteViewsService {
    @Override public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecentCommandsRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class RecentCommandsRemoteViewsFactory implements RemoteViewsFactory {

        private final int MAX = 64;

        private final Context mContext;
        private final int mAppWidgetId;
        private final ArrayList<String> mCommands = new ArrayList<>(MAX);

        public RecentCommandsRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override public void onCreate() { reload(); }

        private void reload() {
            mCommands.clear();
            MIContract.CmdRecent.INSTANCE.load(mContext, mCommands, MAX);
        }

        @Override public void onDataSetChanged() {
            reload();
        }

        @Override public void onDestroy() { }

        @Override public int getCount() { return mCommands.size(); }

        @Override public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.mi_recent_commands_appwidget_item);
            rv.setTextViewText(R.id.cmd_recent_app_widget_item, mCommands.get(position));

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(MyCommands.EX_COMMAND, mCommands.get(position));
            rv.setOnClickFillInIntent(R.id.cmd_recent_app_widget_item, fillInIntent);

            return rv;
        }

        @Override public RemoteViews getLoadingView() { return null; }

        @Override public int getViewTypeCount() { return 1; }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public boolean hasStableIds() {
            return true;
        }
    }
}
