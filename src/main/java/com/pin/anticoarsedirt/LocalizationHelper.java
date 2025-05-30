package com.pin.anticoarsedirt;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class LocalizationHelper {
    
    /**
     * Get localized component for command source
     */
    public static Component translate(CommandSourceStack source, String key, Object... args) {
        return Component.translatable(key, args);
    }
    
    /**
     * Get localized string for formatting
     */
    public static String getLocalizedString(CommandSourceStack source, String key) {
        if (source.getEntity() instanceof ServerPlayer player) {
            // For server-side, we'll use the key as fallback
            // In a real implementation, you might want to cache player languages
            return key;
        }
        return key;
    }
    
    /**
     * Format message with parameters
     */
    public static Component formatMessage(CommandSourceStack source, String key, Object... args) {
        return Component.translatable(key, args);
    }
    
    /**
     * Create help message components
     */
    public static Component createHelpMessage(CommandSourceStack source) {
        return Component.translatable("anticoarsedirt.command.help.title")
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.status"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.help"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.on"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.off"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.toggle"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.setblock"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.examples"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.setrange"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.help.status_cmd"));
    }
    
    /**
     * Create status message
     */
    public static Component createStatusMessage(CommandSourceStack source, boolean enabled, int radius, String targetBlock) {
        Component enabledText = enabled ? 
            Component.translatable("anticoarsedirt.command.status.enabled_yes") :
            Component.translatable("anticoarsedirt.command.status.enabled_no");
            
        return Component.translatable("anticoarsedirt.command.status.title")
            .append("\n").append(Component.translatable("anticoarsedirt.command.status.author"))
            .append("\n").append(Component.translatable("anticoarsedirt.command.status.enabled", enabledText))
            .append("\n").append(Component.translatable("anticoarsedirt.command.status.radius", radius))
            .append("\n").append(Component.translatable("anticoarsedirt.command.status.target_block", targetBlock))
            .append("\n").append(Component.translatable("anticoarsedirt.command.status.help_hint"));
    }
} 