package pl.mareklangiewicz.myloggers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noveogroup.android.log.LogHistory;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.MyAndroidLogger;

import java.util.Locale;

/**
 * Created by Marek Langiewicz on 25.06.15.
 */
public class MyLogAdapter extends RecyclerView.Adapter<MyLogAdapter.ViewHolder> implements View.OnClickListener {

    static public final int LOG_ITEM_VIEW_TAG_HOLDER = R.id.ml_log_item_view_tag_holder;

    protected  @Nullable MyAndroidLogger log;
    protected  @Nullable LogHistory history;

    public MyLogAdapter() {
        setLog(null);
        setHasStableIds(true);
    }

    /**
     * WARNING: remember to set it back to null if the adapter is not used anymore - to avoid memory leaks
     * WARNING: use MyAndroidLogger object from UI thread only - if you want to use this adapter with it.
     */
    public void setLog(@Nullable MyAndroidLogger log) {
        if(this.log != null)
            this.log.setAdapter(null);
        this.log = log;
        this.history = log == null ? null : log.getLogHistory();
        if(this.log != null)
            this.log.setAdapter(this);
        notifyDataSetChanged();
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ml_log_item, parent, false);
        v.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(v);
        v.setTag(LOG_ITEM_VIEW_TAG_HOLDER, holder);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        long nr = 0;
//        long time = 0;
        //String logger = "";
        Logger.Level level = Logger.Level.VERBOSE;
        String message = "";
        float elevation = 2;
        if(history != null) {
            nr = history.getFilteredId(position) + 1;
//            time = history.getFilteredTime(position);
//            logger = history.getFilteredLogger(position);
            message = history.getFilteredMessage(position);
            level = history.getFilteredLevel(position);
        }
        int color = MyAndroidLogger.getLevelColor(level);

/*
        if(Logger.Level.WARN.includes(level)) {
            elevation = 4;
        }
*/

//        message = String.format(Locale.US, "%tT: %s", time, message);
        message = String.format(Locale.US, "%s", message);

        holder.mCardView.setCardElevation(elevation);
        holder.mHeadView.setText(String.format(Locale.US, "%03d%c", nr, MyAndroidLogger.getLevelChar(level)));

        holder.mMessageView.setTextColor(color);
        holder.mMessageView.setText(message);

    }

    @Override
    public int getItemCount() {
        return history == null ? 0 : history.getFilteredSize();
    }

    @Override
    public long getItemId(int position) {
        if(history != null) {
            return history.getFilteredId(position);
        }
        return RecyclerView.NO_ID;
    }

    @Override public void onClick(View v) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public @NonNull final CardView mCardView;
        public @NonNull final TextView mHeadView;
        public @NonNull final TextView mMessageView;

        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.li_card_view);
            mHeadView = (TextView) v.findViewById(R.id.li_head);
            mMessageView = (TextView) v.findViewById(R.id.li_message);
        }
    }
}
