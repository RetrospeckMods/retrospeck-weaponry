package com.testmod.mana;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

public class ManaPlayer implements ManaComponent {
    private int playerMana;
    private int playerMax;
    private int playerRegen;
    private DoubleArrayList playerMaxModifiers;
    private DoubleArrayList playerRegenModifiers;

    public ManaPlayer() {
        playerMana = ManaSystem.getDefaultMax(); // mana is full after initialization
        playerMaxModifiers = new DoubleArrayList();
        playerRegenModifiers = new DoubleArrayList();
        refreshPlayerMax();
        refreshPlayerRegen();
    }

    public boolean consume(int cost) {
        int newMana = playerMana - cost;
        if (newMana < 0)
            return false;
        playerMana = newMana;
        return true;
    }

    public boolean regenerate() {
        int newMana = playerMana + playerRegen;
        if (newMana > playerMax)
            return false;
        playerMana = newMana;
        return true;
    }

    public boolean addMana(int amount) {
        if (playerMana >= playerMax && amount > 0)
            return false;
        int newAmount = playerMana + amount;
        if (newAmount > playerMax) {
            playerMana = playerMax; // fills player's mana to full but doesn't go over
            return true;
        }
        playerMana = newAmount;
        return true;
    }

    public void resetPlayerMax() {
        playerMax = ManaSystem.getDefaultMax();
    }

    public void resetPlayerRegen() {
        playerRegen = ManaSystem.getDefaultRegen();
    }

    public boolean setMana(int newAmount) {
        playerMana = newAmount;
        return newAmount >= 0 && newAmount < playerMax;
    }

    // for mod elements that can modify mana cap/regen rate
    public void addMaxModifier(double modifier) {
        playerMaxModifiers.add(modifier);
        refreshPlayerMax();
    }
    public void addRegenModifier(double modifier) {
        playerRegenModifiers.add(modifier);
        refreshPlayerRegen();
    }

    public void removeMaxModifier(double modifier) {
        playerMaxModifiers.rem(modifier);
        refreshPlayerMax();
    }

    public void removeRegenModifier(double modifier) {
        playerRegenModifiers.rem(modifier);
        refreshPlayerRegen();
    }

    public void refreshPlayerMax() {
        double newPlayerMax = ManaSystem.getDefaultMax();
        for (double scalar: playerMaxModifiers) {
            newPlayerMax *= scalar;
        }
        playerMax = (int)newPlayerMax;
    }

    public void refreshPlayerRegen() {
        double newPlayerRegen = ManaSystem.getDefaultRegen();
        for (double scalar: playerRegenModifiers) {
            newPlayerRegen *= scalar;
        }
        playerRegen = (int)newPlayerRegen;
    }

    public boolean setPlayerMax(int newMax) {
        if (newMax < 0)
            return false;
        playerMax = newMax;
        return true;
    }

    public boolean setPlayerRegen (int newRate) {
        if (newRate < 0)
            return false;
        playerRegen = newRate;
        return true;
    }

    public int getPlayerMana() { return playerMana; }
    public int getPlayerMax() { return playerMax; }
    public int getPlayerRegen() { return playerRegen; }
}