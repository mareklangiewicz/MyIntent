package pl.mareklangiewicz.myintent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.mareklangiewicz.myutils.MyCommands;

/**
 * Created by Marek Langiewicz on 14.10.15.
 */
public class REGroupsAdapter extends RecyclerView.Adapter<REGroupsAdapter.ViewHolder> {

    @Nullable List<MyCommands.REGroup> mGroups;

    public REGroupsAdapter() {
        setHasStableIds(false);
    }

    public REGroupsAdapter(List<MyCommands.REGroup> groups) {
        this();
        setGroups(groups);
    }

    public @Nullable List<MyCommands.REGroup> getGroups() { return mGroups; }

    public void setGroups(@Nullable List<MyCommands.REGroup> groups) {
        mGroups = groups;
        notifyDataSetChanged();
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.re_group_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mGroups == null)
            throw new IllegalStateException();
        MyCommands.REGroup group = mGroups.get(position);
        String name = group.getName();
//        holder.mGroupCardView.setCardElevation(name.equals("user") ? 4 : 2); //TODO: test and change or delete this line
        holder.mGroupHeaderView.setText(group.toString());
        ((RERulesAdapter)holder.mRulesRecyclerView.getAdapter()).setRules(group.getRules());
    }

    @Override
    public int getItemCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public @NonNull CardView mGroupCardView;
        public @NonNull TextView mGroupHeaderView;
        public @NonNull RecyclerView mRulesRecyclerView;

        public ViewHolder(View v) {
            super(v);
            mGroupCardView = (CardView) v.findViewById(R.id.group_card_view);
            mGroupHeaderView = (TextView) v.findViewById(R.id.group_header_view);
            mRulesRecyclerView = (RecyclerView) v.findViewById(R.id.group_rules_view);
            LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
            mRulesRecyclerView.setLayoutManager(manager);
            RERulesAdapter adapter = new RERulesAdapter();
            mRulesRecyclerView.setAdapter(adapter);
        }

    }

}
