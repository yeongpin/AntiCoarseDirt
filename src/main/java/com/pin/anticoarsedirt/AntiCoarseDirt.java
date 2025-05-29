package com.pin.anticoarsedirt;

import com.mojang.logging.LogUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.client.ConfigScreenHandler;

import org.slf4j.Logger;

@Mod(AntiCoarseDirt.MODID)
public class AntiCoarseDirt {
    public static final String MODID = "anticoarsedirt";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AntiCoarseDirt() {
        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        
        // Register config screen using modern Forge API
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(
                (mc, screen) -> createConfigScreen(screen)
            )
        );

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        AntiCoarseDirtCommand.register(event.getDispatcher());
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("anticoarsedirt.config.title"));
        ConfigCategory general = builder.getOrCreateCategory(
            Component.translatable("anticoarsedirt.config.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(
                Component.translatable("anticoarsedirt.config.enabled"), Config.enabled.get())
            .setDefaultValue(true)
            .setSaveConsumer(Config.enabled::set)
            .build());

        general.addEntry(entryBuilder.startIntSlider(
                Component.translatable("anticoarsedirt.config.radius"), Config.radius.get(), 1, 6)
            .setDefaultValue(1)
            .setSaveConsumer(Config.radius::set)
            .build());

        general.addEntry(entryBuilder.startStrField(
                Component.translatable("anticoarsedirt.config.target_block"), Config.targetBlock.get())
            .setDefaultValue("minecraft:dirt")
            .setTooltip(Component.translatable("anticoarsedirt.config.target_block.tooltip"))
            .setSaveConsumer(Config.targetBlock::set)
            .build());

        builder.setSavingRunnable(() -> Config.SPEC.save());
        return builder.build();
    }

    /**
     * Get the target block from config with fallback to dirt if invalid
     */
    private Block getTargetBlock() {
        try {
            ResourceLocation blockId = new ResourceLocation(Config.targetBlock.get());
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            
            // Check if the block exists and is not air
            if (block != null && block != Blocks.AIR) {
                return block;
            } else {
                LOGGER.warn("Invalid target block: {}. Falling back to dirt.", Config.targetBlock.get());
                return Blocks.DIRT;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to parse target block: {}. Falling back to dirt. Error: {}", 
                       Config.targetBlock.get(), e.getMessage());
            return Blocks.DIRT;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!Config.enabled.get()) return;

        ServerLevel level = player.serverLevel();
        int radius = Config.radius.get();
        BlockPos center = player.blockPosition();
        Block targetBlock = getTargetBlock();

        // Convert coarse dirt to target block within radius
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (level.getBlockState(pos).is(Blocks.COARSE_DIRT)) {
                        level.setBlockAndUpdate(pos, targetBlock.defaultBlockState());
                    }
                }
            }
        }
    }
}
