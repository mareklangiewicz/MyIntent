package pl.mareklangiewicz.myutils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;
import java.util.Locale;

/**
 * Created by Marek Langiewicz on 07.09.15.
 * Utilities for text manipulation
 */
public final class MyTextUtils {

    private MyTextUtils() {
        throw new AssertionError("MyTextUtils class is noninstantiable.");
    }

    /*
        private static final boolean V = BuildConfig.VERBOSE;
        private static final boolean VV = BuildConfig.VERY_VERBOSE;

        FIXME SOMEDAY: enable version with BuildConfig when Google fix issue with propagating build types to libraries.
        Now it is always 'release' in libraries.. see:
        https://code.google.com/p/android/issues/detail?id=52962
        http://stackoverflow.com/questions/20176284/buildconfig-debug-always-false-when-building-library-projects-with-gradle
        http://tools.android.com/tech-docs/new-build-system/user-guide#TOC-Library-Publication
    */
    private static final boolean V = true;
    private static final boolean VV = false;


    static public @NonNull String str(int x) {
        if(VV)
            return toVeryLongStr(x);
        if(V)
            return toLongStr(x);
        return toShortStr(x);
    }
    //TODO SOMEDAY: str for other primitive types..

    static public @NonNull <E> String str(@Nullable List<E> x) {
        if(VV)
            return toVeryLongStr(x);
        else if(V)
            return toLongStr(x);
        else
            return toShortStr(x);
    }

    static public @NonNull String str(@Nullable Object x) {
        if(VV)
            return toVeryLongStr(x);
        else if(V)
            return toLongStr(x);
        else
            return toShortStr(x);
    }

    static private @NonNull <E> String toStr(@Nullable List<E> list, int max) {
        if(list == null)
            return "null";
        StringBuilder result = new StringBuilder();
        result.append("[");
        int N = Math.min(list.size(), max);
        for(int i = 0; i < N; ++i) {
            result.append(str(list.get(i)));
            if(i < N - 1)
                result.append((","));
        }
        if(max < list.size())
            result.append(",...");
        result.append("]");
        return result.toString();
    }

    static public @NonNull <E> String toVeryLongStr(@Nullable List<E> list) {
        return toStr(list, 1000);
    }

    static public @NonNull <E> String toLongStr(@Nullable List<E> list) {
        return toStr(list, 100);
    }

    static public @NonNull <E> String toShortStr(@Nullable List<E> list) {
        return toStr(list, 10);
    }

    static public @NonNull String toShortStr(int x) {
        return Integer.toString(x);
    }
    //TODO SOMEDAY: toShortStr for other primitive types..

    static public @NonNull String toShortStr(@Nullable Object x) {
        throw new UnsupportedOperationException(); //TODO SOMEDAY
    }

    static public @NonNull String toLongStr(int x) {
        return Integer.toString(x);
    }
    //TODO SOMEDAY: toLongStr for other primitive types..

    static public @NonNull String toLongStr(@Nullable Object x) {

        if(x == null)
            return "null";

        //Shorten some very long android object descriptions:
        if(x instanceof Bundle)
            return String.format(Locale.US, "%s{size:%d}", x.getClass().getSimpleName(), ((Bundle) x).size());
        if(x instanceof View)
            return String.format(Locale.US, "%s{hash:%x}", x.getClass().getSimpleName(), x.hashCode());

        //TODO SOMEDAY: pretty print our android special cases.

        return x.toString();
    }

    static public @NonNull String toVeryLongStr(int x) {
        return Integer.toString(x);
    }
    //TODO SOMEDAY: toVeryLongStr for other primitive types..

    static public @NonNull String toVeryLongStr(@Nullable Object x) {

        if(x == null)
            return "null";

        //TODO SOMEDAY: very long and pretty print our android special cases.

        return x.toString();
    }

}

