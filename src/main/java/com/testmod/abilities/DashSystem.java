package com.testmod.abilities;

public class DashSystem {
    private static int DefaultTime;
    private static int DefaultInterval;
    private static double DefaultPower;
    private static int DefaultCooldown;

    public static void initialize() {
        DefaultInterval = 3;
        DefaultTime = 9;
        DefaultPower = 0.5;
    }


    public static boolean setDefaultInterval(int defaultInterval) {
        if (defaultInterval <= 0){
            return false;
        }
        DefaultInterval = defaultInterval;
        return true;
    }

    public static boolean setDefaultCooldown(int defaultCooldown) {
        if (defaultCooldown <= 0){
            return false;
        }
        DefaultCooldown = defaultCooldown;
        return true;
    }

    public static boolean setDefaultPower(double defaultPower) {
        if (defaultPower <= 0){
            return false;
        }
        DefaultPower = defaultPower;
        return true;
    }

    public static boolean setDefaultTime(int defaultTime) {
        if (defaultTime <=0){
            return false;
        }
        DefaultTime = defaultTime;
        return true;
    }

    public static double getDefaultPower(){
        return DefaultPower;
    }

    public static int getDefaultTime(){
        return DefaultTime;
    }

    public static int getDefaultInterval(){
        return DefaultInterval;
    }

    public static int getDefaultCooldown() {
        return DefaultCooldown;
    }
}
