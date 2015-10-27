package pl.mareklangiewicz.myintent;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import pl.mareklangiewicz.myutils.MyCommands;

import static pl.mareklangiewicz.myutils.MyTextUtils.str;

/**
 * Created by Marek Langiewicz on 14.10.15.
 */
public class REGroupsAdapter extends RecyclerView.Adapter<REGroupsAdapter.ViewHolder> implements View.OnClickListener {

    static public final int RE_GROUP_VIEW_TAG_HOLDER = R.id.re_group_view_tag_holder;

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
        v.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(RE_GROUP_VIEW_TAG_HOLDER, holder);
        return holder;
    }

    @Override public void onViewRecycled(ViewHolder holder) {
        holder.resetRulesRecyclerView();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mGroups == null)
            throw new IllegalStateException();

        MyCommands.REGroup group = mGroups.get(position);

        holder.mGroupHeaderView.setText(Html.fromHtml("<b>group</b> " + group.getName() + ":<br/>\"" + group.getMatch() + "\""));

        holder.resetRulesRecyclerView();

        holder.setupRulesRecyclerView(group.getRules());

    }

    @Override
    public int getItemCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override public void onClick(View v) {

        Object tag = v.getTag(RE_GROUP_VIEW_TAG_HOLDER);

        if(tag == null)
            return;

        if(mGroups == null)
            return;

        final int pos = ((ViewHolder) tag).getAdapterPosition();
        final MyCommands.REGroup group = mGroups.get(pos);

        MaterialDialog dialog = new MaterialDialog.Builder(v.getContext())
                .title("RE Group " + str(pos + 1))
                .customView(R.layout.re_group_details, true)
                .iconRes(R.mipmap.ic_launcher)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .build();

        //noinspection ConstantConditions
        ((TextView) dialog.getCustomView().findViewById(R.id.re_group_name)).setText(group.getName());
        ((TextView) dialog.getCustomView().findViewById(R.id.re_group_description)).setText(group.getDescription());
        ((TextView) dialog.getCustomView().findViewById(R.id.re_group_match)).setText(group.getMatch());

        dialog.show();

    }


    static class ViewHolder extends RecyclerView.ViewHolder {


        public @NonNull CardView mGroupCardView;
        public @NonNull TextView mGroupHeaderView;
        public @NonNull RecyclerView mRulesRecyclerView;

        @Nullable ItemTouchHelper mItemTouchHelper;

        public ViewHolder(View v) {
            super(v);
            mGroupCardView = (CardView) v.findViewById(R.id.group_card_view);
            mGroupHeaderView = (TextView) v.findViewById(R.id.group_header_view);
            mRulesRecyclerView = (RecyclerView) v.findViewById(R.id.group_rules_view);
        }


        void resetRulesRecyclerView() {
            if(mItemTouchHelper != null) {
                mItemTouchHelper.attachToRecyclerView(null);
                mItemTouchHelper = null;
            }
            mRulesRecyclerView.setAdapter(null);
            mRulesRecyclerView.setLayoutManager(null);
        }


        void setupRulesRecyclerView(List<MyCommands.RERule> rules) {

            resetRulesRecyclerView();

//            LinearLayoutManager manager = new LinearLayoutManager(mRulesRecyclerView.getContext());
            LinearLayoutManager manager = new WCLinearLayoutManager(mRulesRecyclerView.getContext());
            mRulesRecyclerView.setLayoutManager(manager);

            mRulesRecyclerView.setItemAnimator(null); // FIXME SOMEDAY: remove this line when we get rid of WCLinearLayoutManager..

            final RERulesAdapter adapter = new RERulesAdapter();
            adapter.setRules(rules);

            mRulesRecyclerView.setAdapter(adapter);

            mItemTouchHelper = new ItemTouchHelper(new RERulesTouchHelperCallback(adapter));
            mItemTouchHelper.attachToRecyclerView(mRulesRecyclerView);
        }

    }

}
