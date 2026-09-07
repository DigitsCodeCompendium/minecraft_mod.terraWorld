package com.terraskills.toroidalcompat.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ToroidalCompatConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ENABLE_DEBUG_LOGGING = builder
                .comment("Enable verbose toroidal seam diagnostics, crossing logs, and debug overlay commands.")
                .define("enableDebugLogging", false);
        SPEC = builder.build();
    }

    public static boolean debugLoggingEnabled() {
        return ENABLE_DEBUG_LOGGING.get();
    }

    private ToroidalCompatConfig() {}
}
