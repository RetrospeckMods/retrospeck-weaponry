package com.testmod.mana;

import com.testmod.Config;
import com.testmod.TestMod;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

public class ManaSystem {
    private static int defaultMax = 0;
    private static int defaultRegen = 0; // how much mana is regenerated per cycle
    private static HashMap<PlayerEntity, ManaPlayer> players;

    public static boolean initialize() {
        players = new HashMap<>();
        return resetDefaultMax() && resetDefaultRate();
    }

    public static void register(PlayerEntity player) {
        players.put(player, new ManaPlayer());
    }

    public static boolean resetDefaultMax() {
        try {
            defaultMax = (int)(Config.getLong("mana.defaultManaCap"));
            if (defaultMax < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value defaultManaCap in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        return true;
    }

    public static boolean resetDefaultRate() {
        try {
            defaultRegen = (int)(Config.getLong("mana.defaultRegenRate"));
            if (defaultRegen < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value defaultRegenRate in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        return true;
    }

    public static boolean setDefaultMax(int newDefaultMax) {
        if (newDefaultMax < 0)
            return false;
        defaultMax = newDefaultMax;
        return true;
    }

    public static boolean setDefaultRegen(int newDefaultRegen) {
        if (newDefaultRegen < 0)
            return false;
        defaultRegen = newDefaultRegen;
        return true;
    }

    public static ManaPlayer getManaInstance(PlayerEntity playerKey) { return players.get(playerKey); }
    public static int getDefaultMax() { return defaultMax; }
    public static int getDefaultRegen() { return defaultRegen; }
}
