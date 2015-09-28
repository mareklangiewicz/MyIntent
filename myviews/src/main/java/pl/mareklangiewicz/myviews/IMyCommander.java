package pl.mareklangiewicz.myviews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

/**
 * Created by Marek Langiewicz on 03.09.15.
 * An object that manages ui navigation etc.. usually it's an activity
 */
public interface IMyCommander {
    @NonNull CharSequence getTitle();

    void setTitle(@NonNull CharSequence title);

    @Nullable FloatingActionButton getFAB();

    @Nullable IMyNavigation getGlobalNavigation();

    @Nullable IMyNavigation getLocalNavigation();
}
