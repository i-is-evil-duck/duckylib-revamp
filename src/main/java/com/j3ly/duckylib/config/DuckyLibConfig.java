package com.j3ly.duckylib.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class DuckyLibConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_THEME;

    static {
        BUILDER.push("DuckyLib");

        DEFAULT_THEME = BUILDER
            .comment("Default theme to load on startup")
            .define("default_theme", "default");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
