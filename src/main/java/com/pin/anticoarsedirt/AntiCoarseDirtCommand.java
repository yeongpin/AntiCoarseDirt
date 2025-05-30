package com.pin.anticoarsedirt;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class AntiCoarseDirtCommand {
    
    // Auto-completion for blocks - only solid blocks suitable for replacement
    private static final SuggestionProvider<CommandSourceStack> BLOCK_SUGGESTIONS = (context, builder) -> {
        return SharedSuggestionProvider.suggest(
            BuiltInRegistries.BLOCK.keySet().stream()
                .map(ResourceLocation::toString)
                .filter(s -> !s.contains("button") && !s.contains("pressure_plate") 
                          && !s.contains("door") && !s.contains("gate")
                          && !s.contains("slab") && !s.contains("stairs")
                          && !s.contains("torch") && !s.contains("lever")
                          && !s.contains("redstone") && !s.contains("comparator")
                          && !s.contains("repeater") && !s.contains("tripwire")
                          && !s.contains("observer") && !s.contains("piston")
                          && !s.contains("dispenser") && !s.contains("dropper"))
                .sorted(), // Sort alphabetically for better UX
            builder
        );
    };
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("anticoarsedirt")
            .requires(source -> source.hasPermission(2)) // Require OP level 2
            
            // Help command
            .then(Commands.literal("help")
                .executes(context -> showHelp(context)))
            
            // Toggle commands
            .then(Commands.literal("on")
                .executes(context -> setEnabled(context, true)))
            .then(Commands.literal("off")
                .executes(context -> setEnabled(context, false)))
            
            // Block setting command with suggestions
            .then(Commands.literal("setblock")
                .then(Commands.argument("blockId", ResourceLocationArgument.id())
                    .suggests(BLOCK_SUGGESTIONS)
                    .executes(context -> setTargetBlock(context))))
            
            // Range setting command
            .then(Commands.literal("setrange")
                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 16))
                    .executes(context -> setRadius(context))))
            
            // Status command
            .then(Commands.literal("status")
                .executes(context -> showStatus(context)))
            
            // Boolean toggle (true/false)
            .then(Commands.argument("enabled", BoolArgumentType.bool())
                .executes(context -> toggleEnabled(context)))
            
            // Default to status
            .executes(context -> showStatus(context))
        );
    }
    
    private static int showHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> 
            LocalizationHelper.createHelpMessage(context.getSource()), false);
        return 1;
    }
    
    private static int toggleEnabled(CommandContext<CommandSourceStack> context) {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        return setEnabled(context, enabled);
    }
    
    private static int setEnabled(CommandContext<CommandSourceStack> context, boolean enabled) {
        Config.enabled.set(enabled);
        Config.SPEC.save();
        
        String messageKey = enabled ? "anticoarsedirt.command.enabled" : "anticoarsedirt.command.disabled";
        context.getSource().sendSuccess(() -> 
            LocalizationHelper.translate(context.getSource(), messageKey), true);
        
        return 1;
    }
    
    private static int setTargetBlock(CommandContext<CommandSourceStack> context) {
        // Get ResourceLocation directly from argument
        ResourceLocation resourceLocation = ResourceLocationArgument.getId(context, "blockId");
        
        Block block = BuiltInRegistries.BLOCK.get(resourceLocation);
        
        if (block == null || block == Blocks.AIR) {
            context.getSource().sendFailure(
                LocalizationHelper.formatMessage(context.getSource(), 
                    "anticoarsedirt.command.block.not_found", resourceLocation));
            return 0;
        }
        
        // Check if block is suitable for replacement
        if (!isSuitableBlock(block, resourceLocation)) {
            context.getSource().sendFailure(
                LocalizationHelper.formatMessage(context.getSource(),
                    "anticoarsedirt.command.block.unsupported", resourceLocation));
            return 0;
        }
        
        String finalBlockId = resourceLocation.toString();
        Config.targetBlock.set(finalBlockId);
        Config.SPEC.save();
        
        context.getSource().sendSuccess(() -> 
            LocalizationHelper.formatMessage(context.getSource(),
                "anticoarsedirt.command.block.set", finalBlockId), true);
        
        return 1;
    }
    
    /**
     * Check if the block is suitable for replacement
     */
    private static boolean isSuitableBlock(Block block, ResourceLocation location) {
        String path = location.getPath();
        
        // Exclude problematic block types
        if (path.contains("button") || path.contains("pressure_plate") ||
            path.contains("door") || path.contains("gate") ||
            path.contains("torch") || path.contains("lever") ||
            path.contains("redstone") || path.contains("comparator") ||
            path.contains("repeater") || path.contains("tripwire") ||
            path.contains("observer") || path.contains("piston") ||
            path.contains("dispenser") || path.contains("dropper")) {
            return false;
        }
        
        // Exclude air blocks
        if (block == Blocks.AIR || block == Blocks.VOID_AIR || block == Blocks.CAVE_AIR) {
            return false;
        }
        
        return true;
    }
    
    private static int setRadius(CommandContext<CommandSourceStack> context) {
        int radius = IntegerArgumentType.getInteger(context, "radius");
        
        Config.radius.set(radius);
        Config.SPEC.save();
        
        context.getSource().sendSuccess(() -> 
            LocalizationHelper.formatMessage(context.getSource(),
                "anticoarsedirt.command.radius.set", radius), true);
        
        return 1;
    }
    
    private static int showStatus(CommandContext<CommandSourceStack> context) {
        boolean enabled = Config.enabled.get();
        int radius = Config.radius.get();
        String targetBlock = Config.targetBlock.get();
        
        context.getSource().sendSuccess(() -> 
            LocalizationHelper.createStatusMessage(context.getSource(), enabled, radius, targetBlock), false);
        
        return 1;
    }
} 