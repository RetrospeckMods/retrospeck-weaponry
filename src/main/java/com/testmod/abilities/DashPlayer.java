package com.testmod.abilities;

import net.minecraft.entity.player.PlayerEntity;

public class DashPlayer {
    private int time;
    private final PlayerEntity player;
    private final int interval;

    public DashPlayer(PlayerEntity player){
        this.time = DashSystem.getDefaultTime();
        this.player = player;
        this.interval = DashSystem.getDefaultInterval();
    }

    public boolean decrementTime(){
        time--;
        return time <= 0;
    }

    public int getTime(){
        return time;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public int getInterval(){
        return interval;
    }
}
