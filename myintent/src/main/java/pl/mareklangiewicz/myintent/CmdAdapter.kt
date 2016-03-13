package pl.mareklangiewicz.myintent

import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.mi_command.view.*
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import java.util.*

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
class CmdAdapter(commands: List<String> = emptyList()) : RecyclerView.Adapter<CmdAdapter.ViewHolder>(), View.OnClickListener {

    private val log = MY_DEFAULT_ANDRO_LOGGER

    @DrawableRes var imageRes = R.drawable.mi_ic_recent_command_black_24dp

    init {
        setHasStableIds(false)
    }

    var commands: List<String> = commands
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CmdAdapter.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.mi_command, parent, false)
        v.setOnClickListener(this)
        val holder = ViewHolder(v)
        v.setTag(RE_COMMAND_VIEW_TAG_HOLDER, holder)
        return holder
    }

    override fun onBindViewHolder(holder: CmdAdapter.ViewHolder, position: Int) {
        holder.itemView.mi_cmd_tv.text = commands[position]
        holder.itemView.mi_cmd_iv.setImageResource(imageRes)
    }

    override fun getItemCount(): Int = commands.size

    override fun onClick(v: View) {
        val tag = v.getTag(RE_COMMAND_VIEW_TAG_HOLDER) ?: return
        val pos = (tag as ViewHolder).adapterPosition
        val context = v.context
        if (context is MIActivity) {
            context.play(commands[pos])
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) { }

    companion object {

        val RE_COMMAND_VIEW_TAG_HOLDER = R.id.mi_re_command_view_tag_holder
    }
}
