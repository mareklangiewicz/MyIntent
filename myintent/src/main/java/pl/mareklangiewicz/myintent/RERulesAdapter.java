package pl.mareklangiewicz.myintent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.noveogroup.android.log.MyLogger;

import java.util.List;
import java.util.Locale;

import pl.mareklangiewicz.myutils.MyCommands;

import static pl.mareklangiewicz.myutils.MyTextUtilsKt.str;

/**
 * Created by Marek Langiewicz on 15.10.15.
 */
public class RERulesAdapter extends RecyclerView.Adapter<RERulesAdapter.ViewHolder> implements View.OnClickListener {

    protected @NonNull final MyLogger log = MyLogger.UIL;

    private @Nullable MyCommands.RERule explained; // if some rule can not be removed or moved it displays snackbar only once in a row.
        // we remember this rule here so we do not display an error for it more than once in a row.

    static public final int RE_RULE_VIEW_TAG_HOLDER = R.id.mi_re_rule_view_tag_holder;

    @Nullable List<MyCommands.RERule> mRules;

    public RERulesAdapter() {
        setHasStableIds(false);
    }

    public RERulesAdapter(List<MyCommands.RERule> rules) {
        this();
        setRules(rules);
    }

    public @Nullable List<MyCommands.RERule> getRules() { return mRules; }

    public void setRules(@Nullable List<MyCommands.RERule> rules) {
        mRules = rules;
        notifyDataSetChanged();
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mi_re_rule_layout, parent, false);
        v.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(RE_RULE_VIEW_TAG_HOLDER, holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mRules == null) {
            log.e("Rules not set.");
            return;
        }
        MyCommands.RERule rule = mRules.get(position);
        holder.mRuleNameView.setText(Html.fromHtml("<b>rule:</b> " + rule.getName()));
        holder.mRuleContentView.setText(String.format(Locale.US, "match: \"%s\"\nreplace: \"%s\"", rule.getMatch(), rule.getReplace()));
    }

    @Override
    public int getItemCount() {
        return mRules == null ? 0 : mRules.size();
    }

    public boolean move(int pos1, int pos2) {
        if(mRules == null) {
            log.e("Rules not set.");
            return false;
        }
        try {
            MyCommands.RERule rule = mRules.remove(pos1);
            mRules.add(pos2, rule);
            notifyItemMoved(pos1, pos2);
        }
        catch(UnsupportedOperationException e) {
            if(mRules.get(pos1) != explained && mRules.get(pos1) != explained)
                log.i("[SNACK]This group is not editable.");
            explained = mRules.get(pos1);
            return false;
        }
        return true;
    }

    public void remove(int pos) {
        if(mRules == null) {
            log.e("Rules not set.");
            return;
        }
        try {
            mRules.remove(pos);
            notifyItemRemoved(pos);
        }
        catch(UnsupportedOperationException e) {
            if(mRules.get(pos) != explained)
                log.i("[SNACK]This group is not editable.");
            explained = mRules.get(pos);
            notifyDataSetChanged(); // so it redraws swiped rule at original position
        }

    }

    @Override public void onClick(View v) {

        Object tag = v.getTag(RE_RULE_VIEW_TAG_HOLDER);

        if(tag == null)
            return;

        if(mRules == null)
            return;

        final int pos = ((ViewHolder) tag).getAdapterPosition();
        final MyCommands.RERule rule = mRules.get(pos);

        MaterialDialog.SingleButtonCallback onApply = new MaterialDialog.SingleButtonCallback() {
            @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction dialogAction) {
                //noinspection ConstantConditions
                rule.setName(((TextView) dialog.getCustomView().findViewById(R.id.re_rule_name)).getText().toString());
                rule.setDescription(((TextView) dialog.getCustomView().findViewById(R.id.re_rule_description)).getText().toString());
                rule.setMatch(((TextView) dialog.getCustomView().findViewById(R.id.re_rule_match)).getText().toString());
                rule.setReplace(((TextView) dialog.getCustomView().findViewById(R.id.re_rule_replace)).getText().toString());
                notifyItemChanged(pos);
            }
        };

        MaterialDialog dialog = rule.getEditable()
                ?
                new MaterialDialog.Builder(v.getContext())
                        .title("RE Rule")
                        .customView(R.layout.mi_re_rule_details, true)
                        .positiveText("Apply")
                        .negativeText("Cancel")
                        .iconRes(R.mipmap.mi_ic_launcher)
                        .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                        .onPositive(onApply)
                        .build()
                :
                new MaterialDialog.Builder(v.getContext())
                        .title("RE Rule " + str(pos + 1))
                        .customView(R.layout.mi_re_rule_ro_details, true)
                        .iconRes(R.mipmap.mi_ic_launcher)
                        .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                        .build();

        //noinspection ConstantConditions
        ((TextView) dialog.getCustomView().findViewById(R.id.re_rule_name)).setText(rule.getName());
        ((TextView) dialog.getCustomView().findViewById(R.id.re_rule_description)).setText(rule.getDescription());
        ((TextView) dialog.getCustomView().findViewById(R.id.re_rule_match)).setText(rule.getMatch());
        ((TextView) dialog.getCustomView().findViewById(R.id.re_rule_replace)).setText(rule.getReplace());

        dialog.show();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public @NonNull final TextView mRuleNameView;
        public @NonNull final TextView mRuleContentView;

        public ViewHolder(View v) {
            super(v);
            mRuleNameView = (TextView) v.findViewById(R.id.rule_name_view);
            mRuleContentView = (TextView) v.findViewById(R.id.rule_content_view);
        }

    }

}
