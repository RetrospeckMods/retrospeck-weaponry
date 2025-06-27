package com.testmod.mana;

import com.testmod.TestMod;

public class ManaPlayer extends ManaSystem implements ManaComponent {
    private int playerMana;
    private int playerMax;
    private int playerRegen;

    public ManaPlayer() {
        playerMana = getDefaultMax(); // mana is full after initialization
        playerMax = getDefaultMax();
        playerRegen = getDefaultRegen();
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
        if (playerMana >= playerMax)
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
        playerMax = getDefaultMax();
    }

    public void resetPlayerRegen() {
        playerRegen = getDefaultRegen();
    }

    public void setMana(int newAmount) {
        if (newAmount > playerMax)
            TestMod.LOGGER.warn("Player mana value entered is higher than player's capacity; beware of overflow mana being given");
        playerMana = newAmount;
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

    // scale methods should only be used by elements of the mod, should not need to deal with user input here
    public void scalePlayerMax(double scalar) { playerMana = (int)(playerMana*scalar); }
    public void scalePlayerRegen(double scalar) { playerMax = (int)(playerMax*scalar); }

    public int getPlayerMana() { return playerMana; }
    public int getPlayerMax() { return playerMax; }
    public int getPlayerRegen() { return playerRegen; }
}