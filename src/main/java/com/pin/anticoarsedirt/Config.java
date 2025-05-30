package com.pin.anticoarsedirt;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue enabled;
    public static final ForgeConfigSpec.IntValue radius;
    public static final ForgeConfigSpec.ConfigValue<String> targetBlock;

    static {
        BUILDER.push("general");

        enabled = BUILDER
            .comment("Enable or disable the feature")
            .define("enabled", true);

        radius = BUILDER
            .comment("Radius around the player to convert coarse dirt")
            .defineInRange("radius", 1, 1, 16);

        targetBlock = BUILDER
            .comment("The block to replace coarse dirt with (use minecraft:block_name format)",
                     "Examples: minecraft:dirt, minecraft:grass_block, minecraft:stone")
            .define("targetBlock", "minecraft:dirt");

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
