package pl.mareklangiewicz.myutils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by marek on 07.09.15.
 */
public final class MyTextUtils {

    static final boolean VERBOSE = true;
    //TODO LATER: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false;
    //TODO LATER: implement it as a build time switch for user

    static public String toStr(int x) {
        if(VERY_VERBOSE) return toVeryLongStr(x);
        if(VERBOSE) return toLongStr(x);
        return toShortStr(x);
    }
    //TODO: toStr for other primitive types..

    static public String toStr(@Nullable Object x) {
        if(VERY_VERBOSE) return toVeryLongStr(x);
        if(VERBOSE) return toLongStr(x);
        return toShortStr(x);
    }

    static public String toShortStr(int x) {
        throw new UnsupportedOperationException(); //TODO
    }
    //TODO: toShortStr for other primitive types..

    static public String toShortStr(@Nullable Object x) {
        throw new UnsupportedOperationException(); //TODO
    }
    static public String toLongStr(int x) {
        throw new UnsupportedOperationException(); //TODO
    }
    //TODO: toLongStr for other primitive types..

    static public String toLongStr(@Nullable Object x) {

        if(x == null)
            return "null";

        //Shorten some very long android object descriptions:
        if(x instanceof Bundle)
            return String.format("%s{size:%d}", x.getClass().getSimpleName(), ((Bundle) x).size());
        if(x instanceof View)
            return String.format("%s{hash:%x}", x.getClass().getSimpleName(), x.hashCode());

        //TODO: pretty print our android special cases.

        return x.toString();
    }

    static public String toVeryLongStr(int x) {
        throw new UnsupportedOperationException(); //TODO
    }
    //TODO: toVeryLongStr for other primitive types..

    static public String toVeryLongStr(@Nullable Object x) {

        if(x == null)
            return "null";

        //TODO: very long and pretty print our android special cases.

        return x.toString();
    }

}
