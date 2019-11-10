package pl.mareklangiewicz.myintent



import android.annotation.SuppressLint
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.mi_re_group_details.view.*
import kotlinx.android.synthetic.main.mi_re_group_layout.view.*
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.REGroup
import pl.mareklangiewicz.myutils.RERule
import pl.mareklangiewicz.myutils.inflate
import pl.mareklangiewicz.myutils.str
import java.util.*

/**
 * Created by Marek Langiewicz on 14.10.15.
 */
class REGroupsAdapter() : RecyclerView.Adapter<REGroupsAdapter.ViewHolder>(), View.OnClickListener {

    val RE_GROUP_VIEW_TAG_HOLDER = R.id.mi_re_group_view_tag_holder

    val log = MY_DEFAULT_ANDRO_LOGGER

    var groups: List<REGroup>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(false)
    }

    constructor(groups: List<REGroup>) : this() {
        this.groups = groups
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = parent.inflate<View>(R.layout.mi_re_group_layout)!!
        v.setOnClickListener(this)
        val holder = ViewHolder(v)
        v.setTag(RE_GROUP_VIEW_TAG_HOLDER, holder)
        return holder
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.resetRulesRecyclerView()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val group = groups?.get(position) ?: throw IllegalStateException()

        holder.itemView.group_header_view.text = "group ${group.name}:\n\"${group.match}\""

        holder.resetRulesRecyclerView()

        val rules = if(group.editable) group.rules else Collections.unmodifiableList(group.rules)

        holder.setupRulesRecyclerView(rules)

    }

    override fun getItemCount(): Int = groups?.size ?: 0

    override fun onClick(v: View) {

        val tag = v.getTag(RE_GROUP_VIEW_TAG_HOLDER) ?: return

        val pos = (tag as ViewHolder).adapterPosition

        val group = groups?.get(pos) ?: return

        MaterialDialog(v.context)
            .title(text = "RE Group " + (pos + 1).str)
            .customView(R.layout.mi_re_group_details, scrollable = true)
            .icon(R.mipmap.mi_ic_launcher)
            .show {
                getCustomView().apply {
                    re_group_name.text = group.name
                    re_group_description.text = group.description
                    re_group_match.text = group.match
                }
            }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var ithelper: ItemTouchHelper? = null

        fun resetRulesRecyclerView() {
            if (ithelper != null) {
                ithelper!!.attachToRecyclerView(null)
                ithelper = null
            }
            itemView.group_rules_view.adapter = null
        }

        fun setupRulesRecyclerView(rules: MutableList<RERule>) {

            resetRulesRecyclerView()

            val adapter = RERulesAdapter()
            adapter.rules = rules

            itemView.group_rules_view.adapter = adapter

            // TODO SOMEDAY: Maybe I should disable item animations on tablets (when linearlayout is used instead of drawer)
            // because it has some layout issues, but.. maybe google will fix it soon...
            // itemView.group_rules_view.itemAnimator = null

            ithelper = ItemTouchHelper(RERulesTouchHelperCallback(adapter)).apply { attachToRecyclerView(itemView.group_rules_view) }
        }
    }
}
