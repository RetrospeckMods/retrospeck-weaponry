package com.testmod.item.custom;

import com.testmod.Config;
import com.testmod.TestMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ModDaggerItem extends SwordItem {
    private final float attackDamage;
    private static float extraDamageMulti;
    private static double backstabArea;

    public ModDaggerItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed,Settings settings){
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.attackDamage = attackDamage;
    }

    public static boolean initialize() {
        try {
            extraDamageMulti = (float)(Config.getDouble("abilities.backstab.damageMultiplier"));
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value damageMultiplier in [abilities.backstab] is invalid! Expected a float (floating point value).");
            return false;
        }
        try {
            backstabArea = (float)(Config.getDouble("abilities.backstab.backstabArea"));
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value backstabArea in [abilities.backstab] is invalid! Expected a double (floating point value).");
            return false;
        }
        return true;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && isBackStab(attacker, target)) {
            if (!attacker.getWorld().isClient && attacker.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                float extraDamage = attackDamage*extraDamageMulti;
                target.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), extraDamage);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    public boolean isBackStab(LivingEntity attacker, LivingEntity target){
        Vec3d targetLook = target.getRotationVec(0.0f);
        Vec3d flatTargetLook = new Vec3d(targetLook.x, 0, targetLook.z);

        Vec3d delta = attacker.getPos().subtract(target.getPos());
        Vec3d flatToAttacker = new Vec3d(delta.x, 0, delta.z).normalize();

        double dot = flatTargetLook.dotProduct(flatToAttacker);
        return dot < backstabArea;
    }
}
