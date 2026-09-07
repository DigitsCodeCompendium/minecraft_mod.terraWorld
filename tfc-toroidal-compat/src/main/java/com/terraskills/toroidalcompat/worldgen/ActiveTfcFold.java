package com.terraskills.toroidalcompat.worldgen;

import com.toroidalworld.core.WorldFold;

/** Makes the immutable overworld fold visible to TFC's background workers. */
public final class ActiveTfcFold {
    private static volatile WorldFold fold;

    public static WorldFold get() {
        return fold;
    }

    public static void set(WorldFold activeFold) {
        fold = activeFold;
    }

    public static void clear() {
        fold = null;
    }

    private ActiveTfcFold() { }
}
