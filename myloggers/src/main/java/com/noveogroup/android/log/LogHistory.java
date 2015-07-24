package com.noveogroup.android.log;

/**
 * Created by marek on 24.06.15.
 *
 * This class is not thread-safe
 * We don't allocate any objects after constructor,
 * so it should be fast and it shouldn't launch garbage collection.
 */
public class LogHistory {

    private final int capacity;

    private int size = 0;
    private int cursor = 0;

    private long id = 0;

    private final long arrid[];
    private final long arrtime[];
    private final String arrlogger[];
    private final Logger.Level arrlevel[];
    private final String arrmessage[];

    private Logger.Level filter_level;

    private int filter_size = 0;
    private int filter_cursor = 0;
    private final int filter_arrindexes[]; // indexes of elements that pass the filter

    LogHistory(int acapacity) {
        capacity = acapacity;
        arrid = new long[capacity];
        arrtime = new long[capacity];
        arrlogger = new String[capacity];
        arrlevel = new Logger.Level[capacity];
        arrmessage = new String[capacity];

        filter_arrindexes = new int[capacity];
        setFilterLevel(Logger.Level.INFO);

    }

    void setFilterLevel(Logger.Level level) {
        filter_level = level;
        filter_refresh();
    }

    public Logger.Level getFilterLevel() { return filter_level; }

    private void filter_refresh() {
        filter_cursor = 0;
        filter_size = 0;
        if(size == 0)
            return;
        int pos = cursor - size;
        if(pos < 0)
            pos += capacity;
        for(int i = 0; i < size; ++i) {
            if(filter_matches(arrlevel[pos]))
                filter_add(pos);
            pos ++;
            pos %= capacity;
        }
    }

    private void filter_add(int pos) {
        filter_arrindexes[filter_cursor] = pos;
        filter_cursor ++;
        filter_cursor %= capacity;
        if(filter_size < capacity)
            filter_size ++;
    }

    private boolean filter_matches(Logger.Level level) {
        return filter_level.includes(level);
    }

    void add(String logger, Logger.Level level, String message) {
        if(size == capacity && filter_size > 0) {
            // we will forget the oldest element, so maybe we have to remove it from filter array too..
            int fidx = filter_cursor - filter_size;
            if(fidx < 0)
                fidx += capacity; // fidx points to the oldest element in filter array.
            if(arrid[filter_arrindexes[fidx]] == arrid[cursor])
                filter_size --; // here we remove the oldest element from filter.
        }
        arrid[cursor] = id ++;
        arrtime[cursor] = System.currentTimeMillis();
        arrlogger[cursor] = logger;
        arrlevel[cursor] = level;
        arrmessage[cursor] = message;
        if(filter_matches(level))
            filter_add(cursor);
        cursor ++;
        cursor %= capacity;
        if(size < capacity) {
            size ++;
        }
    }

    private int getRealIdx(int idx) {
        if(idx < 0 || idx >= size)
            throw new IndexOutOfBoundsException();
        idx = cursor - 1 - idx;
        if(idx < 0)
            idx += capacity;
        return idx;
    }

    public int getCapacity() { return capacity; }
    public int getSize() { return size; }

    public long getId(int idx) { return arrid[getRealIdx(idx)]; }
    public long getTime(int idx) { return arrtime[getRealIdx(idx)]; }
    public String getLogger(int idx) { return arrlogger[getRealIdx(idx)]; }
    public Logger.Level getLevel(int idx) { return arrlevel[getRealIdx(idx)]; }
    public String getMessage(int idx) { return arrmessage[getRealIdx(idx)]; }

    private int getFilteredIdx(int idx) {
        if(idx < 0 || idx >= filter_size)
            throw new IndexOutOfBoundsException();
        idx = filter_cursor - 1 - idx;
        if(idx < 0)
            idx += capacity;

        return filter_arrindexes[idx];
    }

    public int getFilteredSize() { return filter_size; }

    public long getFilteredId(int idx) { return arrid[getFilteredIdx(idx)]; }
    public long getFilteredTime(int idx) { return arrtime[getFilteredIdx(idx)]; }
    public String getFilteredLogger(int idx) { return arrlogger[getFilteredIdx(idx)]; }
    public Logger.Level getFilteredLevel(int idx) { return arrlevel[getFilteredIdx(idx)]; }
    public String getFilteredMessage(int idx) { return arrmessage[getFilteredIdx(idx)]; }

}
