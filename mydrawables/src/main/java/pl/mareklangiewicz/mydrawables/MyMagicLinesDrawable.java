package pl.mareklangiewicz.mydrawables;

import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.Random;


/**
 * Created by Marek Langiewicz on 16.09.15.
 * TODO LATER: description
 */
public class MyMagicLinesDrawable extends MyLivingDrawable {

    private final Random RANDOM = new Random();
    private int[] mLines;

    public MyMagicLinesDrawable setLines(int... lines) {
        mLines = lines;
        return this;
    }

    public MyMagicLinesDrawable setRandomLines(int count, long seed) {
        RANDOM.setSeed(seed);
        setRandomLines(count);
        return this;
    }

    public MyMagicLinesDrawable setRandomLines(int count) {
        mLines = new int[count * 2];
        for(int i = 0; i < count; ++i) {
            mLines[i * 2] =
                    RANDOM.nextInt(
                            1 + RANDOM.nextInt(10000)
                    );
            mLines[i * 2 + 1] =
                    10000 - RANDOM.nextInt(
                            1 + RANDOM.nextInt(
                                    10000 - mLines[i * 2]
                            )
                    );
        }
        return this;
    }

    public MyMagicLinesDrawable setRandomLines() {
        setRandomLines(3);
        return this;
    }


    @Override
    public void drawLivingPath(@NonNull Path path, @IntRange(from = 0, to = 10000) int level, Rect bounds, int cx, int cy) {

        if(mLines == null)
            setRandomLines();

        int count = mLines.length / 2;
        for(int i = 0; i < count; ++i) {
            int from = mLines[i * 2];
            int to = mLines[i * 2 + 1];
            int height = (i + 1) * bounds.height() / (count + 1);
            ln(bounds.left, height, lvl(from, to, bounds.left, bounds.right), height);
        }
    }
}
