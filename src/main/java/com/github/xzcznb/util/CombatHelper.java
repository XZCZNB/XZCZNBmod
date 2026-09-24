package com.github.xzcznb.util;

import com.github.xzcznb.SoundLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CombatHelper {

    public static double getRelativeSpeed(EntityLivingBase attacker, EntityLivingBase target, double weightX, double weightY, double weightZ) {
        double dx = (attacker.motionX - target.motionX) * weightX;
        double dy = (attacker.motionY - target.motionY) * weightY;
        double dz = (attacker.motionZ - target.motionZ) * weightZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
    }

    public static List<EntityLivingBase> rayTraceEntities(EntityLivingBase attacker, double attackRange) {
        World world = attacker.world;
        List<EntityLivingBase> result = new ArrayList<>();
        Vec3d start = new Vec3d(attacker.posX, attacker.posY + attacker.getEyeHeight(), attacker.posZ);
        Vec3d look = attacker.getLookVec();
        Vec3d end = start.add(look.x * attackRange, look.y * attackRange, look.z * attackRange);
        AxisAlignedBB searchBox = attacker.getEntityBoundingBox().grow(attackRange + 2.0, attackRange + 2.0, attackRange + 2.0);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                searchBox,
                input -> input != attacker && input.isEntityAlive() && input.canBeCollidedWith()
        );
        for (EntityLivingBase entity : entities) {
            AxisAlignedBB box = entity.getEntityBoundingBox();
            RayTraceResult hit = box.calculateIntercept(start, end);
            if (hit != null) {
                result.add(entity);
            }
        }
        return result;
    }

    public static boolean tryExecute(EntityLivingBase target, EntityLivingBase attacker) {
        return tryExecute(target, attacker, 0.05f);
    }

    public static boolean tryExecute(EntityLivingBase target, EntityLivingBase attacker, float probability) {
        if (target == null || attacker == null || target.world.isRemote) return false;
        if (TeamHelper.isAlly(attacker, target)) return false;
        if (attacker.getRNG().nextFloat() >= probability || target.getHealth() <= 0) return false;
        float factor = 4.0f - 3.0f * attacker.getHealth() / attacker.getMaxHealth();
        float damage = target.getMaxHealth() * factor + 1.0f;
        DamageSource source = DamageSource.causeMobDamage(attacker);
        if (attacker instanceof EntityPlayer) source = DamageSource.causePlayerDamage((EntityPlayer) attacker);
        source.setDamageBypassesArmor();
        source.setMagicDamage();
        target.attackEntityFrom(source, damage);
        float pitch = attacker.isChild() ? 1.5f : 1.0f;
        target.world.playSound(null, target.posX, target.posY, target.posZ, SoundLoader.SWORD_KILL, SoundCategory.HOSTILE, 1.0f, pitch);
        return true;
    }

    public static void strafeTowards(EntityLivingBase attacker, double dx, double dz, double speed, double inertia, double angle, double range) {
        double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
        if (horizDist < 0.0001) horizDist = 0.0001;
        double dirX = dx / horizDist;
        double dirZ = dz / horizDist;
        double angleOffset = Math.toRadians(angle + attacker.getRNG().nextDouble() * range);
        double cos = Math.cos(angleOffset);
        double sin = Math.sin(angleOffset);
        double newDirX = dirX * cos - dirZ * sin;
        double newDirZ = dirX * sin + dirZ * cos;
        attacker.motionX = attacker.motionX * inertia + newDirX * speed * (1.0 - inertia);
        attacker.motionZ = attacker.motionZ * inertia + newDirZ * speed * (1.0 - inertia);
    }

    public static void strafeTowards(EntityLivingBase attacker, double dx, double dz, double speed, double inertia) {
        strafeTowards(attacker, dx, dz, speed, inertia, 45.0, 30.0);
    }

    public static void strafeTowards(EntityLivingBase attacker, double dx, double dz, double speed) {
        strafeTowards(attacker, dx, dz, speed, 0.4, 45.0, 30.0);
    }

    public static void strafeLook(EntityLivingBase attacker, double dx, double dz) {
        float targetYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        attacker.rotationYaw = targetYaw;
        attacker.rotationYawHead = targetYaw;
    }

    public static void healByMissingHealth(EntityLivingBase attacker, float percent) {
        float amount = (attacker.getMaxHealth() - attacker.getHealth()) * percent;
        attacker.heal(amount);
    }

    public static void healByMissingHealth(EntityLivingBase attacker) {
        healByMissingHealth(attacker, 0.05f);
    }
}