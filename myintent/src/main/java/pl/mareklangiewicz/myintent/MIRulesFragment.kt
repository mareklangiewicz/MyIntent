package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mi_rules_fragment.*
import pl.mareklangiewicz.myfragments.MyFragment
import pl.mareklangiewicz.myutils.*
import pl.mareklangiewicz.upue.Cancel
import pl.mareklangiewicz.upue.Pushee

/**
 * Created by Marek Langiewicz on 14.10.15.
 */
class MIRulesFragment : MyFragment() {

    private val tocancel = Lst<Pushee<Cancel>>()

    private val adapter = REGroupsAdapter(RE_RULES)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState) //just for logging

        manager?.lnav?.headerId = R.layout.mi_rules_header
        manager?.lnav?.menuId = R.menu.mi_rules_local

        if (RE_USER_GROUP.rules.size > 20)
            log.w("You have more than 20 user rules!")
        // I had some strange native exceptions when displaying a lot of rules in user group.
        // I guess the card view becomes too tall in that case... TODO SOMEDAY: google this issue

        return inflater.inflate(R.layout.mi_rules_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager?.name = BuildConfig.NAME_PREFIX + getString(R.string.mi_rules)
        mi_rules_recycler_view.setHasFixedSize(true)
        mi_rules_recycler_view.adapter = adapter

        val ctl = manager!!.lnav!!.items {
            when(it) {
                R.id.new_user_rule -> RE_USER_GROUP.rules.add(RERule("", "", "", "", true))
                R.id.clear_user_rules -> RE_USER_GROUP.rules.clear()
            }
            adapter.notifyItemChanged(1)
        }
        tocancel.add(ctl)
    }

    override fun onDestroyView() {
        tocancel.forEach { it(Cancel) }
        tocancel.clr()
        mi_rules_recycler_view.adapter = null
        manager?.lnav?.menuId = -1
        manager?.lnav?.headerId = -1
        super.onDestroyView()
    }

}
