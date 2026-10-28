package com.github.xzcznb.util;

import com.github.xzcznb.entity.EntityShell;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class ArrowTrajectoryHelper {

    private static final double MAX_VY = 3.6;
    private static final double MIN_VX = 0.05;
    private static final double HEIGHT_FACTOR = 0.5;
    private static final double ARROW_SPAWN_OFFSET = 0.16;

    public static void shootAtTarget(EntityTippedArrow arrow, EntityLivingBase shooter, EntityLivingBase target, float velocity, float inaccuracy) {
        World world = arrow.world;
        if (world.isRemote) return;
        if (shooter == null || target == null) {
            arrow.setDead();
            return;
        }
        double launchY = shooter.posY + shooter.getEyeHeight() * 0.8 - 0.1;
        double dx = target.posX - shooter.posX;
        double targetY = target.getEntityBoundingBox().minY + target.height * HEIGHT_FACTOR;
        double dy = targetY - launchY;
        double dz = target.posZ - shooter.posZ;
        double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
        if (horizDist < 0.0001) horizDist = 0.0001;
        double deltaX = target.posX - target.lastTickPosX;
        double deltaZ = target.posZ - target.lastTickPosZ;
        double deltaY = target.posY - target.lastTickPosY;
        double speedX = Math.abs(target.motionX) > Math.abs(deltaX) ? target.motionX : deltaX;
        double speedZ = Math.abs(target.motionZ) > Math.abs(deltaZ) ? target.motionZ : deltaZ;
        double speedY = Math.abs(target.motionY) > Math.abs(deltaY) ? target.motionY : deltaY;
        double pitchRad = -MathHelper.atan2(dy, horizDist);
        double vx0 = Math.max(velocity * Math.cos(pitchRad), MIN_VX);
        double targetSpeed = (speedX * dx + speedZ * dz) / horizDist;
        double relSpeed = Math.max(vx0 - targetSpeed, MIN_VX);
        double predictT = Math.min(Math.max(horizDist / relSpeed, 0.1), 200.0);
        double preX = target.posX + speedX * predictT;
        double preZ = target.posZ + speedZ * predictT;
        double preY;
        int yFactor = isFlyingEntity(target);
        if (yFactor > 0) {
            preY = target.posY + speedY * predictT * yFactor / 10.0;
        } else if (!target.onGround) {
            if (speedY > 0.2 && predictT > 5.0) speedY *= 1.0 - Math.min(predictT / 20, 0.5);
            preY = Math.max(target.posY + speedY * predictT - 0.04 * predictT * predictT, target.posY - target.fallDistance);
        } else {
            preY = target.posY;
        }
        double preTargetY = preY + target.height * HEIGHT_FACTOR;
        dx = preX - shooter.posX;
        dy = preTargetY - launchY;
        dz = preZ - shooter.posZ;
        horizDist = MathHelper.sqrt(dx * dx + dz * dz);
        if (horizDist < 0.0001) {
            horizDist = 0.0001;
            if (Math.abs(dx) < 0.0001 && Math.abs(dz) < 0.0001) {
                float yawRad = (float) (shooter.rotationYaw * Math.PI / 180.0);
                dx = -MathHelper.sin(yawRad) * horizDist;
                dz = MathHelper.cos(yawRad) * horizDist;
            } else {
                double currentDist = MathHelper.sqrt(dx * dx + dz * dz);
                double scale = horizDist / currentDist;
                dx *= scale;
                dz *= scale;
            }
            dy = targetY - launchY;
        }
        float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(MathHelper.atan2(dy, horizDist) * 180 / Math.PI));
        arrow.setLocationAndAngles(shooter.posX, launchY, shooter.posZ, yaw, pitch);
        arrow.posX -= MathHelper.cos(yaw / 180.0f * (float) Math.PI) * ARROW_SPAWN_OFFSET;
        arrow.posZ -= MathHelper.sin(yaw / 180.0f * (float) Math.PI) * ARROW_SPAWN_OFFSET;
        arrow.setPosition(arrow.posX, arrow.posY, arrow.posZ);
        double pitchRadFinal = pitch * Math.PI / 180.0;
        vx0 = Math.max(velocity * Math.cos(pitchRadFinal), MIN_VX);
        double expTerm = 1.0 - horizDist / (100.0 * vx0);
        expTerm = MathHelper.clamp(expTerm, 0.0001, 0.9999);
        double T_final = Math.min(Math.max(-100.0 * Math.log(expTerm), 0.1), 300.0);
        double gravityDrop = 0.025 * T_final * T_final;
        double fdyComp = dy + gravityDrop;
        double vy0 = fdyComp / T_final;
        vy0 = MathHelper.clamp(vy0, -MAX_VY, MAX_VY);
        double unitX = dx / horizDist;
        double unitZ = dz / horizDist;
        double dirX = unitX * vx0;
        double dirY = vy0;
        double dirZ = unitZ * vx0;
        double realSpeed = MathHelper.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (realSpeed < MIN_VX) realSpeed = MIN_VX;
        arrow.shoot(dirX / realSpeed, dirY / realSpeed, dirZ / realSpeed, (float) realSpeed, inaccuracy);
    }

    public static void shootScatter(EntityTippedArrow arrow, EntityTippedArrow base, Random rand) {
        World world = arrow.world;
        if (world.isRemote) return;
        double baseLen = MathHelper.sqrt(base.motionX * base.motionX + base.motionY * base.motionY + base.motionZ * base.motionZ);
        if (baseLen < 0.0001) baseLen = 0.0001;
        double fx = base.motionX / baseLen;
        double fy = base.motionY / baseLen;
        double fz = base.motionZ / baseLen;
        double offset = 0.2;
        double spawnX = base.posX + fx * offset;
        double spawnY = base.posY + fy * offset;
        double spawnZ = base.posZ + fz * offset;
        arrow.setLocationAndAngles(spawnX, spawnY, spawnZ, base.rotationYaw, base.rotationPitch);
        double ux = -fz, uy = 0, uz = fx;
        double lenU = MathHelper.sqrt(ux * ux + uy * uy + uz * uz);
        if (lenU < 0.0001) { ux = 1.0; uy = 0; uz = 0; lenU = 1.0; }
        ux /= lenU; uy /= lenU; uz /= lenU;
        double vx = fy * uz - fz * uy;
        double vy = fz * ux - fx * uz;
        double vz = fx * uy - fy * ux;
        double angle = rand.nextDouble() * 2 * Math.PI;
        double perturb = 0.2 + rand.nextDouble() * 0.6;
        double px = fx + (ux * Math.cos(angle) + vx * Math.sin(angle)) * perturb;
        double py = fy + (uy * Math.cos(angle) + vy * Math.sin(angle)) * perturb / 4.0;
        double pz = fz + (uz * Math.cos(angle) + vz * Math.sin(angle)) * perturb;
        double len = MathHelper.sqrt(px * px + py * py + pz * pz);
        if (len < 0.0001) len = 0.0001;
        arrow.shoot(px / len, py / len, pz / len, (float) baseLen, 0);
    }

    private static int isFlyingEntity(EntityLivingBase entity) {
        if (entity instanceof EntityDragon || entity instanceof EntityBlaze || entity instanceof EntityFlying) {
            return 10;
        }
        if (entity.isPotionActive(MobEffects.LEVITATION)) {
            return 10;
        }
        return 0;
    }

    public static void fireCannon(EntityLivingBase shooter, EntityLivingBase target) {
        if (shooter == null || target == null || shooter.world.isRemote) return;
        int bound = 4 + ItemHelper.getTotalEnchantLevel(shooter.getHeldItemMainhand());
        int count = 1 + shooter.getRNG().nextInt(bound);
        double spread = 4.5;
        for (int i = 0; i < count; i++) {
            EntityShell shell = new EntityShell(shooter.world, shooter);
            double aimX = target.posX;
            double aimY = target.posY + target.getEyeHeight();
            double aimZ = target.posZ;
            if (i > 0) {
                aimX += (shooter.getRNG().nextDouble() - 0.5) * spread * 2;
                aimY += (shooter.getRNG().nextDouble() - 0.5) * spread;
                aimZ += (shooter.getRNG().nextDouble() - 0.5) * spread * 2;
            }
            double dx = aimX - shooter.posX;
            double dy = aimY - (shooter.posY + shooter.getEyeHeight());
            double dz = aimZ - shooter.posZ;
            double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
            if (horizDist < 0.0001) horizDist = 0.0001;
            float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
            float pitch = (float) (-(MathHelper.atan2(dy, horizDist) * 180.0 / Math.PI));
            shell.shoot(shooter, pitch, yaw, 0.0f, 1.5f, 1.0f);
            shooter.world.spawnEntity(shell);
        }
    }
}