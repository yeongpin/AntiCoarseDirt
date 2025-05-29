package com.pin.anticoarsedirt.client;

import com.pin.anticoarsedirt.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "anticoarsedirt", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AntiCoarseDirtClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register config screen using modern Forge API
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(
                (mc, screen) -> createConfigScreen(screen)
            )
        );
    }

    private static Screen createConfigScreen(Screen parent) {
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
}