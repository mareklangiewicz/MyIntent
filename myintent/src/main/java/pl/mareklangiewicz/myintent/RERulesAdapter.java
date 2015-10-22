package pl.mareklangiewicz.myintent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.mareklangiewicz.myutils.MyCommands;

/**
 * Created by Marek Langiewicz on 15.10.15.
 */
public class RERulesAdapter extends RecyclerView.Adapter<RERulesAdapter.ViewHolder> {

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
                .inflate(R.layout.re_rule_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mRules == null)
            throw new IllegalStateException();
        MyCommands.RERule rule = mRules.get(position);
        holder.mRuleNameView.setText(Html.fromHtml("<b>rule:</b> " + rule.getName()));
        holder.mRuleContentView.setText("match: \"" + rule.getMatch() + "\"\nreplace: \"" + rule.getReplace() + "\"");
    }

    @Override
    public int getItemCount() {
        return mRules == null ? 0 : mRules.size();
    }

    public void move(int pos1, int pos2) {
        if(mRules == null)
            throw new IllegalStateException("Rules not set");
        MyCommands.RERule rule = mRules.remove(pos1);
        mRules.add(pos2, rule);
        notifyItemMoved(pos1, pos2);
    }

    public void remove(int pos) {
        if(mRules == null)
            throw new IllegalStateException("Rules not set");
        mRules.remove(pos);
        notifyItemRemoved(pos);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public @NonNull TextView mRuleNameView;
        public @NonNull TextView mRuleContentView;

        public ViewHolder(View v) {
            super(v);
            mRuleNameView = (TextView) v.findViewById(R.id.rule_name_view);
            mRuleContentView = (TextView) v.findViewById(R.id.rule_content_view);
        }

    }

}
