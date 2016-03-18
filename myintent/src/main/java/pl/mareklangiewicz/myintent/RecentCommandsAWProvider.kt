package pl.mareklangiewicz.myintent

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews

import pl.mareklangiewicz.myutils.MyCommands

class RecentCommandsAWProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, ids: IntArray) {
        for (id in ids) update(context, appWidgetManager, id)
    }

    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle) {
        update(context, appWidgetManager, appWidgetId)
    }

    private fun update(context: Context, manager: AppWidgetManager, id: Int) {
        manager.updateAppWidget(id, generateRemoteViews(context, id))
    }

    private fun generateRemoteViews(context: Context, id: Int): RemoteViews {

        val views = RemoteViews(context.packageName, R.layout.mi_recent_commands_appwidget)

        val startIntent = Intent(context, MIActivity::class.java)
        startIntent.data = Uri.parse(startIntent.toUri(Intent.URI_INTENT_SCHEME))
        val startPendingIntent = PendingIntent.getActivity(context, 0, startIntent, 0)
        views.setOnClickPendingIntent(R.id.widget_start, startPendingIntent)

        val listenIntent = Intent(context, MIActivity::class.java)
        listenIntent.putExtra(MyCommands.EX_COMMAND, "start custom action listen")
        listenIntent.data = Uri.parse(listenIntent.toUri(Intent.URI_INTENT_SCHEME))
        val listenPendingIntent = PendingIntent.getActivity(context, 0, listenIntent, 0)
        views.setOnClickPendingIntent(R.id.widget_listen, listenPendingIntent)

        val serviceIntent = Intent(context, RecentCommandsRVService::class.java)
        serviceIntent.data = Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        views.setRemoteAdapter(R.id.recent_commands_listview, serviceIntent)

        val itemIntent = Intent(context, MIActivity::class.java)
        itemIntent.data = Uri.parse(itemIntent.toUri(Intent.URI_INTENT_SCHEME))
        val itemPendingIntent = PendingIntent.getActivity(context, 0, itemIntent, 0)
        views.setPendingIntentTemplate(R.id.recent_commands_listview, itemPendingIntent)

        return views

    }
}
