package pl.mareklangiewicz.myfragments

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mf_maf_detail.view.*
import kotlinx.android.synthetic.main.mf_my_about_fragment.*
import pl.mareklangiewicz.myutils.inflate
import pl.mareklangiewicz.myutils.str

/**
 * Created by Marek Langiewicz on 06.04.16.
 */
@Suppress("unused")
open class MyAboutFragment : MyFragment() {

    var title: String = ""
        set(value) {
            field = value
            if(isViewAvailable)
                mf_maf_tv_title.text = value
        }

    var description: String = ""
        set(value) {
            field = value
            if(isViewAvailable)
                mf_maf_tv_description.text = value
        }

    var details: List<Pair<String, String>> = emptyList()
        set(value) {
            field = value
            adapter.details = value
            adapter.notifyDataSetChanged()
        }

    val adapter: DetailsAdapter = DetailsAdapter(details)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.mf_my_about_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val det = arrayListOf<Pair<String, String>>()
        arguments?.let {
            for (key in it.keySet()) {
                when(key) {
                    "title" -> title = it[key].str
                    "description" -> description = it[key].str
                    else -> det.add(key to it[key].str)
                }
            }
        }
        details = det
        title = title
        description = description
        mf_maf_rv_details.adapter = adapter
    }

    override fun onDestroyView() {
        mf_maf_rv_details.adapter = null
        super.onDestroyView()
    }

    class DetailViewHolder(v: View) : RecyclerView.ViewHolder(v) { }

    class DetailsAdapter(var details: List<Pair<String, String>>) : RecyclerView.Adapter<DetailViewHolder>() {

        override fun getItemCount() = details.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailViewHolder(parent.inflate<View>(R.layout.mf_maf_detail)!!)

        override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
            val (label, value) = details[position]
            holder.itemView.mf_maf_tv_detail_label.text = label
            holder.itemView.mf_maf_tv_detail_value.text = value
        }
    }
}

