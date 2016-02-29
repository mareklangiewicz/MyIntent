package pl.mareklangiewicz.myintent;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noveogroup.android.log.MyAndroidLogger;

import java.util.Collections;
import java.util.List;

import pl.mareklangiewicz.myutils.IMyLogger;

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
public class CmdAdapter extends RecyclerView.Adapter<CmdAdapter.ViewHolder> implements View.OnClickListener{

    protected @NonNull final IMyLogger log = MyAndroidLogger.UIL;

    protected @DrawableRes int mImageRes = R.drawable.mi_ic_recent_command_black_24dp;

    static public final int RE_COMMAND_VIEW_TAG_HOLDER = R.id.mi_re_command_view_tag_holder;

    @Nullable List<String> mCommands;

    public CmdAdapter() {
        setHasStableIds(false);
    }

    public CmdAdapter(List<String> commands) {
        this();
        setCommands(commands);
    }

    public @Nullable List<String> getCommands() { return mCommands == null ? null : Collections.unmodifiableList(mCommands); }

    public void setCommands(@Nullable List<String> commands) {
        mCommands = commands;
        notifyDataSetChanged();
    }

    public int getImageRes() { return mImageRes; }

    public void setImageRes(@DrawableRes int res) { mImageRes = res; }

    @Override public CmdAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mi_command, parent, false);
        v.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(RE_COMMAND_VIEW_TAG_HOLDER, holder);
        return holder;
    }
    @Override public void onBindViewHolder(CmdAdapter.ViewHolder holder, int position) {
        if(mCommands == null) {
            log.e("Commands not set.", null);
            return;
        }
        String command = mCommands.get(position);
        holder.mTextView.setText(command);
        holder.mImageView.setImageResource(mImageRes);
    }
    @Override public int getItemCount() {
        return mCommands == null ? 0 : mCommands.size();
    }

    @Override public void onClick(View v) {
        Object tag = v.getTag(RE_COMMAND_VIEW_TAG_HOLDER);

        if(tag == null)
            return;

        if(mCommands == null)
            return;

        final int pos = ((ViewHolder) tag).getAdapterPosition();
        final String command = mCommands.get(pos);
        Context context = v.getContext();
        if(context instanceof MIActivity) {
            ((MIActivity)context).play(command);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public @NonNull final ImageView mImageView;
        public @NonNull final TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.cmd_image_view);
            mTextView = (TextView) v.findViewById(R.id.cmd_text_view);
        }
    }
}
