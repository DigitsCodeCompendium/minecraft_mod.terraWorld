package com.terraskills.toroidalcompat.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ToroidalCompatClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue SHOW_HUD;
    public static final ModConfigSpec.DoubleValue HUD_SCALE;
    public static final ModConfigSpec.IntValue LEFT_OFFSET;
    public static final ModConfigSpec.IntValue BOTTOM_OFFSET;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("toroidalHud");
        SHOW_HUD = builder.comment("Show latitude and longitude in the bottom-left HUD.")
                .define("enabled", true);
        HUD_SCALE = builder.comment("HUD scale. 1 is normal size.")
                .defineInRange("scale", 1.0, 0.5, 4.0);
        LEFT_OFFSET = builder.comment("Distance from the left edge in pixels.")
                .defineInRange("leftOffset", 6, 0, 1000);
        BOTTOM_OFFSET = builder.comment("Distance from the bottom edge in pixels.")
                .defineInRange("bottomOffset", 6, 0, 1000);
        builder.pop();
        SPEC = builder.build();
    }

    private ToroidalCompatClientConfig() {}
}
