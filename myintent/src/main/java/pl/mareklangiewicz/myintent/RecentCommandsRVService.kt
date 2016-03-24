package pl.mareklangiewicz.myintent

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import pl.mareklangiewicz.myutils.EX_COMMAND
import java.util.*

/**
 * Created by Marek Langiewicz on 24.11.15.
 */
class RecentCommandsRVService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory = RVFactory(this.applicationContext)

    internal inner class RVFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

        private val MAX = 64
        private val commands = ArrayList<String>(MAX)


        private fun reload() {
            commands.clear()
            MIContract.CmdRecent.load(context, commands, MAX)
        }

        override fun onCreate() = reload()
        override fun onDataSetChanged() = reload()
        override fun onDestroy() { }
        override fun getCount(): Int = commands.size
        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount(): Int = 1
        override fun getItemId(position: Int): Long = position.toLong()
        override fun hasStableIds(): Boolean = true

        override fun getViewAt(position: Int): RemoteViews {
            val rv = RemoteViews(context.packageName, R.layout.mi_recent_commands_appwidget_item)
            rv.setTextViewText(R.id.cmd_recent_app_widget_item, commands[position])

            val fillInIntent = Intent()
            fillInIntent.putExtra(EX_COMMAND, commands[position])
            rv.setOnClickFillInIntent(R.id.cmd_recent_app_widget_item, fillInIntent)

            return rv
        }

    }
}
