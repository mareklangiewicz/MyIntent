package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mi_rules_fragment.*
import pl.mareklangiewicz.myfragments.MyFragment
import pl.mareklangiewicz.myutils.RERule
import pl.mareklangiewicz.myutils.RE_RULES
import pl.mareklangiewicz.myutils.RE_USER_GROUP
import pl.mareklangiewicz.myviews.IMyUINavigation

/**
 * Created by Marek Langiewicz on 14.10.15.
 */
class MIRulesFragment : MyFragment() {

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
        manager?.name = getString(R.string.mi_rules)
        mi_rules_recycler_view.setHasFixedSize(true)
        mi_rules_recycler_view.adapter = adapter
    }

    override fun onDestroyView() {
        manager?.lnav?.menuId = -1
        manager?.lnav?.headerId = -1
        super.onDestroyView()
    }

    override fun onItemSelected(nav: IMyUINavigation, item: MenuItem): Boolean = when (item.itemId) {
        R.id.new_user_rule -> {
            RE_USER_GROUP.rules.add(RERule("", "", "", "", true))
            adapter.notifyItemChanged(1)
            true
        }
        R.id.clear_user_rules -> {
            RE_USER_GROUP.rules.clear()
            adapter.notifyItemChanged(1)
            true
        }
        else -> false
    }

}
