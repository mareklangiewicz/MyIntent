package pl.mareklangiewicz.myloggers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ml_log_item.view.*
import pl.mareklangiewicz.myutils.IMyArray
import pl.mareklangiewicz.myutils.MyLogEntry
import pl.mareklangiewicz.myutils.MyLogLevel
import java.util.*

/**
 * Created by Marek Langiewicz on 25.06.15.
 */

val LOG_ITEM_VIEW_TAG_HOLDER = R.id.ml_log_item_view_tag_holder

open class MyAndroLogAdapter(array: IMyArray<MyLogEntry>? = null) : RecyclerView.Adapter<MyAndroLogAdapter.ViewHolder>(), View.OnClickListener {

    init {
        setHasStableIds(true)
    }

    var array: IMyArray<MyLogEntry>? = array
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.ml_log_item, parent, false)
        v.setOnClickListener(this)
        val holder = ViewHolder(v)
        v.setTag(LOG_ITEM_VIEW_TAG_HOLDER, holder)
        return holder

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var nr = 0L
//        var time = 0L
//        var tag = ""
        var level = MyLogLevel.VERBOSE
        var message = ""
        var elevation = 2f

        array?.run {
            val e = get(position)
            nr = e.id
//            time = e.time
//            tag = e.tag
            message = e.message
            level = e.level
        }

//        if(level >= MyLogLevel.WARN)
//            elevation = 4f

//        message = "%tT: %s".format(Locale.US, time, message);

        holder.itemView.li_card_view.cardElevation = elevation
        holder.itemView.li_head.text = "%03d%c".format(Locale.US, nr, level.symbol)

        holder.itemView.li_message.setTextColor(level.color)
        holder.itemView.li_message.text = message

    }

    override fun getItemCount(): Int = array?.size ?: 0

    override fun getItemId(position: Int): Long = array?.get(position)?.id ?: RecyclerView.NO_ID

    override fun onClick(v: View) { }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) { }
}
