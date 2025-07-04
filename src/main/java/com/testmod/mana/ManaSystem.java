package com.testmod.mana;

import com.testmod.Config;
import com.testmod.TestMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.HashMap;
import java.util.UUID;

public class ManaSystem {
    private static int defaultMax = 0;
    private static int defaultRegen = 0; // how much mana is regenerated per cycle
    private static HashMap<UUID, ManaPlayer> players;

    public static void initialize() {
        players = new HashMap<>();
        resetDefaultMax();
        resetDefaultRegen();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // give player a mana value when they join
            ManaSystem.register(handler.getPlayer().getUuid());
        });
    }

    public static boolean reload() {
        return resetDefaultMax() && resetDefaultRegen();
    }

    public static void register(UUID player) {
        players.put(player, new ManaPlayer());
    }

    public static boolean resetDefaultMax() {
        try {
            defaultMax = (int)(Config.getLong("mana.defaultManaCap"));
            if (defaultMax < 0) {
                defaultMax = 0;
                throw new ArithmeticException("Invalid config values");
            }
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value defaultManaCap in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        refreshAllMaxes();
        return true;
    }

    public static boolean resetDefaultRegen() {
        try {
            defaultRegen = (int)(Config.getLong("mana.defaultRegenRate"));
            if (defaultRegen < 0) {
                defaultRegen = 0;
                throw new ArithmeticException("Invalid config values");
            }
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value defaultRegenRate in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        refreshAllRegen();
        return true;
    }

    public static boolean setDefaultMax(int newDefaultMax) {
        if (newDefaultMax < 0)
            return false;
        defaultMax = newDefaultMax;
        refreshAllMaxes();
        return true;
    }

    public static boolean setDefaultRegen(int newDefaultRegen) {
        if (newDefaultRegen < 0)
            return false;
        defaultRegen = newDefaultRegen;
        refreshAllRegen();
        return true;
    }

    public static void refreshAllMaxes() {
        for (ManaPlayer player: players.values()) {
            player.refreshPlayerMax();
        }
    }

    public static void refreshAllRegen() {
        for (ManaPlayer player : players.values()) {
            player.refreshPlayerRegen();
        }
    }

    public static ManaPlayer getManaInstance(UUID playerUuid) { return players.get(playerUuid); }
    public static int getDefaultMax() { return defaultMax; }
    public static int getDefaultRegen() { return defaultRegen; }
}
