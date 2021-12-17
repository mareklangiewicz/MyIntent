package pl.mareklangiewicz.myintent

import android.view.View
import android.view.View.inflate
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.mi_log_details.view.*
import pl.mareklangiewicz.myloggers.LOG_ITEM_VIEW_TAG_HOLDER
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter
import pl.mareklangiewicz.myutils.MyLogEntry
import pl.mareklangiewicz.upue.*
import java.util.*

/**
 * Created by Marek Langiewicz on 23.10.15.
 * This class use material-dialogs library to present details of any log message when clicked.
 */
class MyMDAndroLogAdapter(arr: IArr<MyLogEntry>?) : MyAndroLogAdapter(arr) {

    override fun onClick(v: View) {
        val tag = v.getTag(LOG_ITEM_VIEW_TAG_HOLDER) ?: return
        val pos = (tag as ViewHolder).adapterPosition

        arr?.run {
            val entry = get(pos)
            MaterialDialog(v.context)
                .title(text = entry.tag + " message " + entry.id)
                .icon(R.mipmap .mi_ic_launcher) //TODO SOMEDAY: change icon depending on level
                .customView(R.layout.mi_log_details, scrollable = true)
                .show {
                    getCustomView().apply {
                        log_level.text = entry.level.toString()
                        log_level.setTextColor(entry.level.color)
                        log_time.text = String.format(Locale.US, "%tT", entry.time)
                        log_message.text = entry.message
                    }
                }
        }
    }
}
