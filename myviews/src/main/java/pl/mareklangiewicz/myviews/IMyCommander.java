package pl.mareklangiewicz.myviews;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

/**
 * Created by marek on 03.09.15.
 */
public interface IMyCommander {
    @NonNull CharSequence getTitle();
    void setTitle(@NonNull CharSequence title);
    @Nullable FloatingActionButton getFAB();
    @Nullable IMyNavigation getGlobalNavigation();
    @Nullable IMyNavigation getLocalNavigation();
}
