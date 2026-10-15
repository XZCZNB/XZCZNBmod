package com.github.xzcznb.entity.ai;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.entity.EntityShell;
import com.github.xzcznb.item.ItemCannon;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackCannon extends EntityAIBase {
    private final EntityCreature attacker;
    private final IRangedAttackMob rangedAttacker;
    private EntityLivingBase target;
    private int attackTime = 0;
    private final int maxAttackTime;
    private final int minAttackTime;
    private final float maxAttackDistance;
    private final float minAttackDistance;
    private final int attackCooldown;

    public EntityAIAttackCannon(IRangedAttackMob attacker, int minAttackTime, int maxAttackTime, float maxAttackDistance) {
        this(attacker, minAttackTime, maxAttackTime, maxAttackDistance, 6.0f);
    }

    public EntityAIAttackCannon(IRangedAttackMob attacker, int minAttackTime, int maxAttackTime, float maxAttackDistance, float minAttackDistance) {
        if (!(attacker instanceof EntityCreature)) {
            throw new IllegalArgumentException("EntityAIAttackCannon requires Mob implements RangedAttackMob");
        }
        this.rangedAttacker = attacker;
        this.attacker = (EntityCreature) attacker;
        this.minAttackTime = minAttackTime;
        this.maxAttackTime = maxAttackTime;
        this.attackCooldown = Math.min((maxAttackTime - minAttackTime), minAttackTime) / 2 + 1;
        this.maxAttackDistance = maxAttackDistance;
        this.minAttackDistance = minAttackDistance;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        ItemStack held = this.attacker.getHeldItemMainhand();
        return ItemHelper.holding(held, ItemCannon.class);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void resetTask() {
        this.target = null;
        rangedAttacker.setSwingingArms(false);
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
        if (!ItemHelper.holding(held, ItemCannon.class)) return;
        float distance = this.attacker.getDistance(this.target);
        PathNavigate navigator = this.attacker.getNavigator();
        this.attacker.getLookHelper().setLookPositionWithEntity(this.target, 30.0f, 30.0f);
        if (distance > this.maxAttackDistance) {
            navigator.tryMoveToEntityLiving(this.target, 1.2);
        } else if (distance > minAttackDistance) {
            boolean canSee = this.attacker.getEntitySenses().canSee(this.target);
            if (canSee && distance < this.maxAttackDistance * 0.5f) {
                navigator.tryMoveToEntityLiving(this.target, 0.4);
            } else {
                navigator.tryMoveToEntityLiving(this.target, 1.2);
            }
            this.rangedAttacker.setSwingingArms(true);
            float factor = 0.25f + this.attacker.getRNG().nextFloat() * (distance / this.maxAttackDistance);
            if (--this.attackTime <= 0) {
                if (!canSee) return;
                this.rangedAttacker.attackEntityWithRangedAttack(this.target, factor);
                this.rangedAttacker.setSwingingArms(false);
                this.attackTime = MathHelper.floor(factor * (this.maxAttackTime - this.minAttackTime) + this.minAttackTime);
                if (this.attacker.getRNG().nextFloat() < 0.5f) this.attackTime /= 2;
            }
        } else {
            double dx = target.posX - this.attacker.posX;
            double dz = target.posZ - this.attacker.posZ;
            if (--this.attackTime <= 0) {
                CombatHelper.healByMissingHealth(this.attacker);
                this.attacker.attackEntityAsMob(this.target);
                this.attackTime = attackCooldown;
                float pitch = attacker.isChild() ? 1.5f : 1.0f;
                this.attacker.playSound(SoundLoader.ROYAL_GUARD_RUN, 1.0f, pitch);
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

    public static void fireCannon(EntityLivingBase shooter, EntityLivingBase target) {
        if (shooter == null || target == null || shooter.world.isRemote) return;
        ItemStack held = shooter.getHeldItemMainhand();
        int bound = 4 + ItemHelper.getTotalEnchantLevel(held);
        int count = 1 + shooter.getRNG().nextInt(bound);
        double spread = 4.5;
        for (int i = 0; i < count; i++) {
            EntityShell shell = new EntityShell(shooter.world, shooter);
            double aimX = target.posX;
            double aimY = target.posY + target.height * 0.5;
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