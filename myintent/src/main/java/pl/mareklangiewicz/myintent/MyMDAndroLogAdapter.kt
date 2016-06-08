package pl.mareklangiewicz.myintent

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.mi_log_details.view.*
import pl.mareklangiewicz.myloggers.LOG_ITEM_VIEW_TAG_HOLDER
import pl.mareklangiewicz.myloggers.MyAndroLogAdapter
import pl.mareklangiewicz.myutils.IArr
import pl.mareklangiewicz.myutils.MyLogEntry
import java.util.*

/**
 * Created by Marek Langiewicz on 23.10.15.
 * This class use material-dialogs library to present details of any log message when clicked.
 */
class MyMDAndroLogAdapter(arr: IArr<MyLogEntry>?) : MyAndroLogAdapter(arr) {

    override fun onClick(v: View) {
        val tag = v.getTag(LOG_ITEM_VIEW_TAG_HOLDER) ?: return
        val pos = (tag as MyAndroLogAdapter.ViewHolder).adapterPosition

        arr?.run {
            val entry = get(pos)
            val dialog = MaterialDialog.Builder(v.context)
                    .title(entry.tag + " message " + entry.id)
                    .customView(R.layout.mi_log_details, true)
                    .iconRes(R.mipmap .mi_ic_launcher) //TODO SOMEDAY: change icon depending on level
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .build()

            dialog.customView?.run {
                log_level.text = entry.level.toString()
                log_level.setTextColor(entry.level.color)
                log_time.text = String.format(Locale.US, "%tT", entry.time)
                log_message.text = entry.message
            }

            dialog.show()
        }
    }
}
