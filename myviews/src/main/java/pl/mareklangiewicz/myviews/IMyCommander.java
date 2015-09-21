package pl.mareklangiewicz.myviews;

import android.support.annotation.Nullable;

/**
 * Created by marek on 03.09.15.
 */
public interface IMyCommander {
    @Nullable IMyNavigation getGlobalNavigation();
    @Nullable IMyNavigation getLocalNavigation();
}
