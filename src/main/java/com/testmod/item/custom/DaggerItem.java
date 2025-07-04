package com.testmod.item.custom;

import com.testmod.Config;
import com.testmod.TestMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.SwordItem;

public class DaggerItem extends SwordItem implements ReachModifier {
    private final float attackDamage;
    private static float extraDamageMulti;
    private static double areaCosAngle;

    public DaggerItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
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
            areaCosAngle = Config.getDouble("abilities.backstab.backstabArea");
        }
        catch (ArithmeticException e) {
            TestMod.LOGGER.error("Configuration value areaCosAngle in [abilities.backstab] is invalid! Expected a double (floating point value).");
            return false;
        }
        return true;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && isBackstab(attacker, target)) {
            if (!attacker.getWorld().isClient && attacker.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                float extraDamage = attackDamage*extraDamageMulti;
                target.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), extraDamage);
            }
        }
        return super.postHit(stack, target, attacker);
    }

    public boolean isBackstab(LivingEntity player, LivingEntity target) {
        Vec3d targetDir = target.getRotationVec(0.0f);
        Vec3d flatTargetLooking = new Vec3d(targetDir.x, 0, targetDir.z);

        Vec3d delta = player.getPos().subtract(target.getPos());
        Vec3d flatToAttacker = new Vec3d(delta.x, 0, delta.z).normalize();

        double dot = flatTargetLooking.dotProduct(flatToAttacker);
        return dot < areaCosAngle;

        /*double fullConeAngleDegrees = 60;
        Vec3d playerFacingVec = player.getRotationVec(1.0f).normalize();
        Vec3d dirToTarget = target.getPos().subtract(player.getPos()).normalize();

        double dot = playerFacingVec.dotProduct(dirToTarget);
        double angleCos = Math.cos(Math.toRadians(fullConeAngleDegrees/2.0));
        //for 60 degrees, this is -0.866: concave up between both points (150 and 210 aka +-30 of 180)

        return dot < angleCos;*/
    }

    @Override
    public float getReach() {
        return 1f;
    }
}
