package com.testmod.mana;

public interface ManaComponent {
    // player consumes mana when they use an ability, returns true if successful
    boolean consume(int cost);

    // regenerate player's mana by set amount (stored in variable), return false once full
    boolean regenerate();

    // setter methods for player, either return true if successful or return nothing
    boolean addMana(int amount); // will add by set amount, could also be used for potions
    void resetPlayerMax(); // resets to default value
    void resetPlayerRegen(); // resets to default value
    void setMana(int newAmount); // allows for overflow
    boolean setPlayerMax(int newMax);
    boolean setPlayerRegen (int newRate);

    // for potion effects
    void scalePlayerMax(double scalar);
    void scalePlayerRegen(double scalar);

    // getter methods
    int getPlayerMana();
    int getPlayerMax();
    int getPlayerRegen();
}