package pl.mareklangiewicz.myintent;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import pl.mareklangiewicz.myutils.MyCommands;

public class RecentCommandsAppWidgetProvider extends AppWidgetProvider {

    @Override public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] ids) {

        for (int i = 0; i < ids.length; i++) {
            update(context, appWidgetManager, ids[i]);
        }

    }

    @Override public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        update(context, appWidgetManager, appWidgetId);
    }

    private void update(Context context, AppWidgetManager manager, int id) {
        manager.updateAppWidget(id, generateRemoteViews(context, id));
    }

    private RemoteViews generateRemoteViews(Context context, int id) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recent_commands_appwidget);

        Intent startIntent = new Intent(context, MIActivity.class);
        startIntent.setData(Uri.parse(startIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent startPendingIntent = PendingIntent.getActivity(context, 0, startIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_start, startPendingIntent);

        Intent listenIntent = new Intent(context, MIActivity.class);
        listenIntent.putExtra(MyCommands.EX_COMMAND, "start custom action listen");
        listenIntent.setData(Uri.parse(listenIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent listenPendingIntent = PendingIntent.getActivity(context, 0, listenIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_listen, listenPendingIntent);

        Intent serviceIntent = new Intent(context, RecentCommandsRemoteViewsService.class);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        views.setRemoteAdapter(R.id.recent_commands_listview, serviceIntent);

        Intent itemIntent = new Intent(context, MIActivity.class);
        itemIntent.setData(Uri.parse(itemIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent itemPendingIntent = PendingIntent.getActivity(context, 0, itemIntent, 0);
        views.setPendingIntentTemplate(R.id.recent_commands_listview, itemPendingIntent);

        return views;

    }
}
