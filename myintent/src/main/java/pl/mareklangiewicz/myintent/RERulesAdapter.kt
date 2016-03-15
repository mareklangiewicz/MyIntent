package pl.mareklangiewicz.myintent

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.mi_re_rule_layout.view.*
import pl.mareklangiewicz.myloggers.MY_DEFAULT_ANDRO_LOGGER
import pl.mareklangiewicz.myutils.MyCommands
import pl.mareklangiewicz.myutils.str

/**
 * Created by Marek Langiewicz on 15.10.15.
 */
class RERulesAdapter() : RecyclerView.Adapter<RERulesAdapter.ViewHolder>(), View.OnClickListener {

    val RE_RULE_VIEW_TAG_HOLDER = R.id.mi_re_rule_view_tag_holder

    protected val log = MY_DEFAULT_ANDRO_LOGGER

    private var explained: MyCommands.RERule? = null
    // if some rule can not be removed or moved it displays snackbar only once in a row.
    // we remember this rule here so we do not display an error for it more than once in a row.

    init {
        setHasStableIds(false)
    }

    constructor(arules: MutableList<MyCommands.RERule>) : this() {
        rules = arules
    }

    var rules: MutableList<MyCommands.RERule>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.mi_re_rule_layout, parent, false)
        v.setOnClickListener(this)
        val holder = ViewHolder(v)
        v.setTag(RE_RULE_VIEW_TAG_HOLDER, holder)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (rules === null) {
            log.e("Rules not set.")
        }
        else {
            val rule = rules!![position]
            holder.itemView.rule_name_view.text = Html.fromHtml("<b>rule:</b> ${rule.name}")
            holder.itemView.rule_content_view.text = "match: \"${rule.match}\"\nreplace: \"${rule.replace}\""
        }
    }

    override fun getItemCount(): Int = rules?.size ?: 0

    fun move(pos1: Int, pos2: Int): Boolean {
        val rs = rules
        if (rs === null) {
            log.e("Rules not set.")
            return false
        }
        try {
            val rule = rs.removeAt(pos1)
            rs.add(pos2, rule)
            notifyItemMoved(pos1, pos2)
        } catch (e: UnsupportedOperationException) {
            if (rs[pos1] != explained && rs[pos2] != explained)
                log.i("[SNACK]This group is not editable.")
            explained = rs[pos1]
            return false
        }
        return true
    }

    fun remove(pos: Int) {
        val rs = rules
        if (rs == null) {
            log.e("Rules not set.")
            return
        }
        try {
            rs.removeAt(pos)
            notifyItemRemoved(pos)
        } catch (e: UnsupportedOperationException) {
            if (rs[pos] != explained)
                log.i("[SNACK]This group is not editable.")
            explained = rs[pos]
            notifyDataSetChanged() // so it redraws swiped rule at original position
        }

    }

    override fun onClick(view: View) {

        val tag = view.getTag(RE_RULE_VIEW_TAG_HOLDER) ?: return

        val rs = rules ?: return

        val pos = (tag as ViewHolder).adapterPosition

        val rule = rs[pos]


        val onApply = MaterialDialog.SingleButtonCallback { dialog, dialogAction ->
            val cv = dialog.customView!!
            rule.name = (cv.findViewById(R.id.re_rule_name) as TextView).text.toString()
            rule.description = (cv.findViewById(R.id.re_rule_description) as TextView).text.toString()
            rule.match = (cv.findViewById(R.id.re_rule_match) as TextView).text.toString()
            rule.replace = (cv.findViewById(R.id.re_rule_replace) as TextView).text.toString()
            notifyItemChanged(pos)
        }

        val dialog = if (rule.editable)
            MaterialDialog.Builder(view.context)
                    .title("RE Rule")
                    .customView(R.layout.mi_re_rule_details, true)
                    .positiveText("Apply")
                    .negativeText("Cancel")
                    .iconRes(R.mipmap.mi_ic_launcher)
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .onPositive(onApply)
                    .build()
        else
            MaterialDialog.Builder(view.context)
                    .title("RE Rule " + (pos + 1).str)
                    .customView(R.layout.mi_re_rule_ro_details, true)
                    .iconRes(R.mipmap.mi_ic_launcher)
                    .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                    .build()

        val cv = dialog.customView!!
        (cv.findViewById(R.id.re_rule_name) as TextView).text = rule.name
        (cv.findViewById(R.id.re_rule_description) as TextView).text = rule.description
        (cv.findViewById(R.id.re_rule_match) as TextView).text = rule.match
        (cv.findViewById(R.id.re_rule_replace) as TextView).text = rule.replace

        dialog.show()
    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) { }

}
