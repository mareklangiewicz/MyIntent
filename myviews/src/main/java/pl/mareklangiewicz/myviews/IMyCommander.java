package pl.mareklangiewicz.myviews;

/**
 * Created by marek on 03.09.15.
 */
public interface IMyCommander {
    IMyNavigation getGlobalNavigation();
    IMyNavigation getLocalNavigation();
}
