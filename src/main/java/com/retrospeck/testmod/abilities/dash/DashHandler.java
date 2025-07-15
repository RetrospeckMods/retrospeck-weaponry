package com.retrospeck.testmod.abilities.dash;

import com.retrospeck.testmod.Config;
import com.retrospeck.testmod.TestMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DashHandler {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    private static int cooldownTicks;
    private static int dashDuration;
    private static int dashDistance;
    private static double dashSpeed;
    private static double dashSlowdown;

    private static final HashMap<UUID, DashData> activeDashes = new HashMap<>();
    private static final HashMap<UUID, Integer> cooldowns = new HashMap<>();

    public static void initialize() {
        loadValues();

        PayloadTypeRegistry.playC2S().register(DashPayload.ID, DashPayload.CODEC); // client-to-server
        PayloadTypeRegistry.playS2C().register(DashPayload.ID, DashPayload.CODEC); // server-to-client
        ServerPlayNetworking.registerGlobalReceiver(
                DashPayload.ID,
                (payload, context) -> {
                    context.server().execute(() -> {
                        ServerPlayerEntity player = context.player();
                        DashHandler.tryDash(player);
                    });
                }
        );
        ServerTickEvents.END_SERVER_TICK.register(DashHandler::serverTick);
    }

    public static boolean loadValues() {
        boolean success = true;
        try {
            cooldownTicks = (int)(Config.getLong("abilities.dash.cooldown"));
            if (cooldownTicks < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            cooldownTicks = 0;
            TestMod.LOGGER.error("Configuration value cooldown in [abilities.dash] is invalid! Expected a positive integer.");
            success = false;
        }
        try {
            dashDuration = (int)(Config.getLong("abilities.dash.duration"));
            if (dashDuration < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            dashDuration = 0;
            TestMod.LOGGER.error("Configuration value duration in [abilities.dash] is invalid! Expected a positive integer.");
            success = false;
        }
        try {
            dashDistance = (int)(Config.getLong("abilities.dash.distance"));
            if (dashDistance < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            dashDistance = 0;
            TestMod.LOGGER.error("Configuration value distance in [abilities.dash] is invalid! Expected a positive integer.");
            success = false;
        }
        if (dashDuration > 1) {
            double denominator = dashDuration-0.5;
            dashSpeed = dashDistance / denominator;
            dashSlowdown = dashDistance / (denominator * 2);
        }
        else if (dashDuration > 0) {
            dashSpeed = (double)dashDistance / dashDuration;
            dashSlowdown = dashSpeed;
        }
        else {
            dashSpeed = 0;
            success = false;
        }
        cooldowns.clear();

        return success;
    }

    private static void tryDash(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (activeDashes.containsKey(uuid)) return;
        int cooldownRemaining = cooldowns.getOrDefault(uuid, 0);
        if (cooldownRemaining > 0) {
            player.sendMessage(Text.literal("You are on cooldown! " + DECIMAL_FORMAT.format(cooldownRemaining/20.0) + " seconds left.").formatted(Formatting.RED));
            return;
        }

        Vec3d baseVelo = player.getMovement();
        Vec3d lookDir = player.getRotationVector().normalize();
        Vec3d dashVelo = baseVelo.add(lookDir.multiply(dashSpeed));
        Vec3d slowdownVelo = baseVelo.add(lookDir.multiply(dashSlowdown));

        activeDashes.put(uuid, new DashData(baseVelo, dashVelo, slowdownVelo, dashDuration));
    }

    private static void serverTick(MinecraftServer server) {
        if (!activeDashes.isEmpty()) {
            activeDashes.entrySet().removeIf(entry -> {
                return !applyDash(entry, server);
            });
        }

        if (cooldowns.isEmpty()) return;
        cooldowns.replaceAll((id, ticks) -> ticks - 1);
        cooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    private static boolean applyDash(Map.Entry<UUID, DashData> entry, MinecraftServer server) {
        UUID uuid = entry.getKey();
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
        if (player == null) return false;
        DashData data = entry.getValue();

        if (data.ticksRemaining > 1) {
            player.setVelocity(data.dashVelo);
        }
        else if (data.ticksRemaining == 1) {
            player.setVelocity(data.slowdownVelo);
            cooldowns.put(uuid, cooldownTicks);
        }
        else {
            player.setVelocity(data.baseVelo);
            return false;
        }
        player.velocityModified = true;

        data = new DashData(data.baseVelo, data.dashVelo, data.slowdownVelo, data.ticksRemaining-1);
        entry.setValue(data);

        return true;
    }

    private record DashData(Vec3d baseVelo, Vec3d dashVelo, Vec3d slowdownVelo, int ticksRemaining) {}
}
