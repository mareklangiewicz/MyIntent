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
import com.noveogroup.android.log.MyLogger;

/**
 * Created by Marek Langiewicz on 25.06.15.
 */
public final class MyLogAdapter extends RecyclerView.Adapter<MyLogAdapter.ViewHolder> {

    private @Nullable MyLogger log;
    private @Nullable LogHistory history;

    public MyLogAdapter() {
        setLog(null);
        setHasStableIds(true);
    }

    /**
     * WARNING: remember to set it back to null if the adapter is not used anymore - to avoid memory leaks
     * WARNING: use MyLogger object from UI thread only - if you want to use this adapter with it.
     */
    public void setLog(@Nullable MyLogger log) {
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
                .inflate(R.layout.log_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        long nr = 0;
        long time = 0;
        //String logger = "";
        Logger.Level level = Logger.Level.VERBOSE;
        String message = "";
        float elevation = 2;
        if(history != null) {
            nr = history.getFilteredId(position) + 1;
            time = history.getFilteredTime(position);
            //logger = history.getFilteredLogger(position);
            message = history.getFilteredMessage(position);
            level = history.getFilteredLevel(position);
        }
        int color = MyLogger.getLevelColor(level);

/*
        if(Logger.Level.WARN.includes(level)) {
            elevation = 4;
        }
*/

//        message = String.format("%tT: %s", time, message);
        message = String.format("%s", message);

        holder.mCardView.setCardElevation(elevation);
        holder.mHeadView.setText(String.format("%02d%c", nr, MyLogger.getLevelChar(level)));

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
        return super.getItemId(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public @NonNull CardView mCardView;
        public @NonNull TextView mHeadView;
        public @NonNull TextView mMessageView;

        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.li_card_view);
            mHeadView = (TextView) v.findViewById(R.id.li_head);
            mMessageView = (TextView) v.findViewById(R.id.li_message);
        }
    }
}
