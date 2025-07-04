package com.testmod.abilities.dash;


import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.testmod.TestMod;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DashCommand {


    public static void register(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("dash")
                    .requires(src -> Permissions.check(src, TestMod.MOD_ID + ".commands.dash", 2))
                    .then(CommandManager.literal("defaults")
                            .requires(src -> Permissions.check(src, TestMod.MOD_ID + ".commands.dash.defaults", 3))
                            // set defaults
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.literal("dashTime")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int value = IntegerArgumentType.getInteger(context, "value");
                                                        if (!DashSystem.setDefaultTime(value)) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive integer greater than 0.").formatted(Formatting.RED), false);
                                                            return -1;
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set default dash time to").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(" " + value).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("dashInterval")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int value = IntegerArgumentType.getInteger(context, "value");
                                                        if (!DashSystem.setDefaultInterval(value)) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive integer greater than 0.").formatted(Formatting.RED), false);
                                                            return -1;
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set default dash interval to").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(" " + value).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("dashPower")
                                            .then(CommandManager.argument("value", DoubleArgumentType.doubleArg())
                                                    .executes(context -> {
                                                        double value = DoubleArgumentType.getDouble(context, "value");
                                                        if (!DashSystem.setDefaultPower(value)) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive double greater than 0.").formatted(Formatting.RED), false);
                                                            return -1;
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set default dash power to").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(" " + value).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            // get defaults
                            .then(CommandManager.literal("get")
                                    .then(CommandManager.literal("dashTime")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(() -> Text.literal("The current default dash time is").formatted(Formatting.YELLOW)
                                                        .append(Text.literal(" " + DashSystem.getDefaultTime()).formatted(Formatting.AQUA)), false);
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("dashInterval")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(() -> Text.literal("The current default dash interval is").formatted(Formatting.YELLOW)
                                                        .append(Text.literal(" " + DashSystem.getDefaultInterval()).formatted(Formatting.AQUA)), false);
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("dashPower")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(() -> Text.literal("The current default dash power is").formatted(Formatting.YELLOW)
                                                        .append(Text.literal(" " + DashSystem.getDefaultPower()).formatted(Formatting.AQUA)), false);
                                                return 1;
                                            })
                                    )
                            )
                    )
            );

        });
    }

}
