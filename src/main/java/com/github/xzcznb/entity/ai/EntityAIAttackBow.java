package com.github.xzcznb.entity.ai;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class EntityAIAttackBow extends EntityAIBase {
    private final EntityCreature attacker;
    private final IRangedAttackMob rangedAttacker;
    private EntityLivingBase target;
    private int attackTime = 0;
    private final int maxAttackTime;
    private final int minAttackTime;
    private final float maxAttackDistance;
    private final float minAttackDistance;
    private final int attackCooldown;

    public EntityAIAttackBow(EntityCreature attacker, int minAttackTime, int maxAttackTime, float maxAttackDistance) {
        this(attacker, minAttackTime, maxAttackTime, maxAttackDistance, 6.0f);
    }

    public EntityAIAttackBow(EntityCreature attacker, int minAttackTime, int maxAttackTime, float maxAttackDistance, float minAttackDistance) {
        this.attacker = attacker;
        this.rangedAttacker = attacker instanceof IRangedAttackMob ? (IRangedAttackMob) attacker : null;
        this.minAttackTime = minAttackTime;
        this.maxAttackTime = maxAttackTime;
        this.attackCooldown = Math.min((maxAttackTime - minAttackTime), minAttackTime) / 2 + 1;
        this.maxAttackDistance = maxAttackDistance;
        this.minAttackDistance = minAttackDistance;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.rangedAttacker == null) return false;
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            this.attacker.setAttackTarget(null);
            return false;
        }
        return ItemHelper.holding(this.attacker.getHeldItemMainhand(), ItemBow.class);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemBow.class)) return;
        boolean isSuper = held.getMaxDamage() > 2048;
        float distance = this.attacker.getDistance(this.target);
        PathNavigate navigator = this.attacker.getNavigator();
        EntityLivingBase beneficiary = SummonHelper.getBeneficiary(this.attacker);
        this.attacker.getLookHelper().setLookPositionWithEntity(this.target, 30.0f, 30.0f);
        if (distance > this.maxAttackDistance) {
            navigator.tryMoveToEntityLiving(this.target, 1.5);
        } else {
            Random rand = this.attacker.getRNG();
            if (distance > minAttackDistance) {
                boolean canSee = this.attacker.getEntitySenses().canSee(this.target);
                if (canSee && distance < this.maxAttackDistance * 0.5f) {
                    navigator.tryMoveToEntityLiving(this.target, 0.4);
                } else navigator.tryMoveToEntityLiving(this.target, 1.2);
                this.rangedAttacker.setSwingingArms(true);
                float factor = 0.25f + rand.nextFloat() * (distance / this.maxAttackDistance);
                float distanceFactor = isSuper ? 2.0f * factor : factor;
                if (--this.attackTime <= 0) {
                    if (!canSee) return;
                    this.rangedAttacker.attackEntityWithRangedAttack(this.target, distanceFactor);
                    this.rangedAttacker.setSwingingArms(false);
                    this.attackTime = MathHelper.floor(factor * (this.maxAttackTime - this.minAttackTime) + this.minAttackTime);
                    if (isSuper && rand.nextFloat() < 0.5f) this.attackTime /= 4;
                    else if (rand.nextFloat() < 0.5f) this.attackTime /= 2;
                }
            } else {
                double dx = target.posX - this.attacker.posX;
                double dz = target.posZ - this.attacker.posZ;
                if (--this.attackTime <= 0) {
                    CombatHelper.healByMissingHealth(beneficiary);
                    this.attacker.attackEntityAsMob(this.target);
                    if (isSuper && rand.nextFloat() < 0.1f) {
                        this.attackTime /= 2;
                        int duration = 16 + 8 * rand.nextInt(8);
                        int amplifier = rand.nextInt(4);
                        beneficiary.addPotionEffect(new PotionEffect(PotionLoader.purification, duration * 4, 0));
                        this.target.addPotionEffect(new PotionEffect(PotionLoader.corruption, duration, amplifier));
                    }
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
    }

    @Override
    public void resetTask() {
        this.target = null;
        rangedAttacker.setSwingingArms(false);
        this.attacker.getNavigator().clearPath();
        this.attackTime = 0;
    }
}