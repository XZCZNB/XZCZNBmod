package com.github.xzcznb.entity.ai;

import com.github.xzcznb.SoundLoader;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static com.github.xzcznb.util.CombatHelper.healByMissingHealth;

public class EntityAIAttackBow extends EntityAIBase {
    private final EntityCreature attacker;
    private final IRangedAttackMob rangedAttackEntity;
    private EntityLivingBase target;
    private int attackTime = -1;
    private final int maxAttackTime;
    private final int minAttackTime;
    private final float maxAttackDistance;
    private final float minAttackDistance;
    private final int attackCooldown;

    public EntityAIAttackBow(IRangedAttackMob attacker, int minAttackTime, int maxAttackTime, float maxAttackDistance) {
        this(attacker, minAttackTime, maxAttackTime, maxAttackDistance, 6.0f);
    }

    public EntityAIAttackBow(IRangedAttackMob attacker, int minAttackTime, int maxAttackTime, float maxAttackDistance, float minAttackDistance) {
        if (!(attacker instanceof EntityCreature)) {
            throw new IllegalArgumentException("EntityAIAttackBow requires Mob implements RangedAttackMob");
        }
        this.rangedAttackEntity = attacker;
        this.attacker = (EntityCreature) attacker;
        this.minAttackTime = minAttackTime;
        this.maxAttackTime = maxAttackTime;
        this.attackCooldown = Math.min((maxAttackTime - minAttackTime), minAttackTime) / 2 + 1;
        this.maxAttackDistance = maxAttackDistance;
        this.minAttackDistance = minAttackDistance;
        this.setMutexBits(3);
    }

    public static class EntityCustomArrow extends EntityTippedArrow {

        private static final double MAX_VY = 3.6;
        private static final double MIN_VX = 0.06;
        private static final double HEIGHT_FACTOR = 0.5;
        private static final double ARROW_SPAWN_OFFSET = 0.16;

        public EntityCustomArrow(World worldIn) {
            super(worldIn);
        }

        public EntityCustomArrow(World worldIn, EntityCustomArrow base) {
            super(worldIn);
            if (this.world.isRemote) return;
            this.shootingEntity = base.shootingEntity;
            this.pickupStatus = base.pickupStatus;
            double baseLen = MathHelper.sqrt(base.motionX * base.motionX + base.motionY * base.motionY + base.motionZ * base.motionZ);
            if (baseLen < 0.0001) baseLen = 0.0001;
            double fx = base.motionX / baseLen;
            double fy = base.motionY / baseLen;
            double fz = base.motionZ / baseLen;
            double offset = 0.2;
            double spawnX = base.posX + fx * offset;
            double spawnY = base.posY + fy * offset;
            double spawnZ = base.posZ + fz * offset;
            this.setLocationAndAngles(spawnX, spawnY, spawnZ, base.rotationYaw, base.rotationPitch);
            double ux = -fz, uy = 0, uz = fx;
            double lenU = MathHelper.sqrt(ux * ux + uy * uy + uz * uz);
            if (lenU < 0.0001) { ux = 1.0; uy = 0; uz = 0; lenU = 1.0; }
            ux /= lenU; uy /= lenU; uz /= lenU;
            double vx = fy * uz - fz * uy;
            double vy = fz * ux - fx * uz;
            double vz = fx * uy - fy * ux;
            double angle = this.rand.nextDouble() * 2 * Math.PI;
            double perturb = 0.2 + this.rand.nextDouble() * 0.6;
            double px = fx + (ux * Math.cos(angle) + vx * Math.sin(angle)) * perturb;
            double py = fy + (uy * Math.cos(angle) + vy * Math.sin(angle)) * perturb / 4.0;
            double pz = fz + (uz * Math.cos(angle) + vz * Math.sin(angle)) * perturb;
            double len = MathHelper.sqrt(px * px + py * py + pz * pz);
            if (len < 0.0001) len = 0.0001;
            this.shoot(px / len, py / len, pz / len, (float) baseLen, 0);
            this.setDamage(base.getDamage());
        }

        public EntityCustomArrow(World worldIn, EntityLivingBase shooter, EntityLivingBase target, float velocity, float inaccuracy) {
            super(worldIn);
            if (this.world.isRemote) return;
            this.shootingEntity = shooter;
            if (shooter == null || target == null) {
                this.setDead();
                return;
            }
            if (shooter instanceof EntityTameable) this.pickupStatus = PickupStatus.CREATIVE_ONLY;
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
            this.setLocationAndAngles(shooter.posX, launchY, shooter.posZ, yaw, pitch);
            this.posX -= MathHelper.cos(yaw / 180.0f * (float) Math.PI) * ARROW_SPAWN_OFFSET;
            this.posZ -= MathHelper.sin(yaw / 180.0f * (float) Math.PI) * ARROW_SPAWN_OFFSET;
            this.setPosition(this.posX, this.posY, this.posZ);
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
            this.shoot(dirX / realSpeed, dirY / realSpeed, dirZ / realSpeed, (float) realSpeed, inaccuracy);
        }

        private int isFlyingEntity(EntityLivingBase entity) {
            if(entity instanceof EntityDragon || entity instanceof EntityBlaze || entity instanceof EntityFlying) {
                return 10;
            }
            return 0;
        }
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        ItemStack held = this.attacker.getHeldItemMainhand();
        return !held.isEmpty() && held.getItem() instanceof ItemBow;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void resetTask() {
        this.target = null;
        if (this.attacker instanceof IRangedAttackMob) {
            ((IRangedAttackMob) this.attacker).setSwingingArms(false);
        }
        this.attacker.getNavigator().clearPath();
        this.attackTime = 0;
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            this.attacker.setAttackTarget(null);
            return;
        }
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemBow)) return;
        boolean isIAimingEntity = this.attacker instanceof IRangedAttackMob;
        float distance = this.attacker.getDistance(this.target);
        PathNavigate navigator = this.attacker.getNavigator();
        this.attacker.getLookHelper().setLookPositionWithEntity(this.target, 30.0f, 30.0f);
        if (distance > this.maxAttackDistance) {
            navigator.tryMoveToEntityLiving(this.target, 1.2);
        } else if (distance > minAttackDistance) {
            boolean canSee = this.attacker.getEntitySenses().canSee(this.target);
            if (canSee && distance < this.maxAttackDistance / 2.0f) {
                navigator.tryMoveToEntityLiving(this.target, 0.4);
            }
            else navigator.tryMoveToEntityLiving(this.target, 1.2);
            if (isIAimingEntity) {
                ((IRangedAttackMob) this.attacker).setSwingingArms(true);
            }
            float factor = this.attacker.getRNG().nextFloat() * (distance / this.maxAttackDistance);
            if (--this.attackTime <= 0) {
                if (!canSee) return;
                float distanceFactor = MathHelper.clamp(factor, 0.6f, 1.2f);
                this.rangedAttackEntity.attackEntityWithRangedAttack(this.target, distanceFactor);
                this.attackTime = MathHelper.floor(factor * (this.maxAttackTime - this.minAttackTime) + this.minAttackTime);
                if (isIAimingEntity) {
                    ((IRangedAttackMob) this.attacker).setSwingingArms(false);
                }
                if (this.attacker.getRNG().nextFloat() < 0.5f) this.attackTime /= 2;
            }
        } else {
            double dx = target.posX - this.attacker.posX;
            double dz = target.posZ - this.attacker.posZ;
            if (--this.attackTime <= 0) {
                healByMissingHealth(this.attacker);
                this.attacker.attackEntityAsMob(this.target);
                this.attackTime = attackCooldown;
                float pitch = attacker.isChild() ? 1.5f : 1.0f;
                this.attacker.playSound(SoundLoader.LOYAL_ZOMBIE_RUN, 1.0f, pitch);
            }
            double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
            if (horizDist < 0.0001) horizDist = 0.0001;
            navigator.tryMoveToXYZ(
                    this.attacker.posX - dx / horizDist * minAttackDistance * 1.5,
                    this.attacker.posY,
                    this.attacker.posZ - dz / horizDist * minAttackDistance * 1.5,
                    1.5
            );
        }
    }
}