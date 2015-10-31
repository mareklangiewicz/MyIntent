package pl.mareklangiewicz.myintent;

import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.MyLogger;

import pl.mareklangiewicz.myloggers.MyLogAdapter;

/**
 * Created by Marek Langiewicz on 23.10.15.
 * This class use material-dialogs library to present details of any log message when clicked.
 */
public class MyMDLogAdapter extends MyLogAdapter {
    @Override public void onClick(View v) {
        Object tag = v.getTag(LOG_ITEM_VIEW_TAG_HOLDER);
        if(tag == null)
            return;
        int pos = ((ViewHolder)tag).getAdapterPosition();
        if(history == null)
            return;
        long nr = history.getFilteredId(pos) + 1;
        long time = history.getFilteredTime(pos);
        String logger = history.getFilteredLogger(pos);
        String message = history.getFilteredMessage(pos);
        Logger.Level level = history.getFilteredLevel(pos);
        MaterialDialog dialog = new MaterialDialog.Builder(v.getContext())
                .title(logger + " message " + nr)
                .customView(R.layout.log_details, true)
                .iconRes(R.mipmap.ic_launcher) //TODO SOMEDAY: change icon depending on level
//                .iconRes(R.drawable.ic_report_black_24dp)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .build();

        //noinspection ConstantConditions
        ((TextView) dialog.getCustomView().findViewById(R.id.log_level)).setText(level.toString());
        ((TextView) dialog.getCustomView().findViewById(R.id.log_level)).setTextColor(MyLogger.getLevelColor(level));
        ((TextView) dialog.getCustomView().findViewById(R.id.log_time)).setText(String.format("%tT", time));
        ((TextView) dialog.getCustomView().findViewById(R.id.log_message)).setText(message);

        dialog.show();
    }
}
