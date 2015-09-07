package pl.mareklangiewicz.myutils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by marek on 07.09.15.
 */
public final class MyText {

    static final boolean VERBOSE = true;
    //TODO later: implement it as a build time switch for user
    static final boolean VERY_VERBOSE = false;
    //TODO later: implement it as a build time switch for user

    static public String toStr(int x) {
        throw new UnsupportedOperationException(); //TODO
    }
    //TODO: toStr for other primitive types..

    static public String toStr(@Nullable Object x) {

        if(x == null)
            return "null";

        if(!VERY_VERBOSE) { //Shorten some very long android object descriptions:
            if(x instanceof Bundle)
                return String.format("%s{size:%d}", x.getClass().getSimpleName(), ((Bundle) x).size());
            if(x instanceof View)
                return String.format("%s{hash:%x}", x.getClass().getSimpleName(), x.hashCode());
        }

        //TODO: pretty print our android special cases.


        return x.toString();
    }
}
