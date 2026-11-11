package com.github.xzcznb.entity.ai;

import com.github.xzcznb.item.ItemScythe;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.ItemHelper;
import com.github.xzcznb.util.TargetQueueHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import java.util.Random;

public class EntityAIAttackScythe extends EntityAIBase {
    private final EntityCreature attacker;
    private final float attackDamage;
    private EntityLivingBase target;
    private int attackTime = 0;
    private static final int ATTACK_INTERVAL = 6;
    private static final float ATTACK_RANGE = 3.0f;

    public EntityAIAttackScythe(EntityCreature attacker) {
        this.attacker = attacker;
        this.attackDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (!ItemHelper.holding(this.attacker.getHeldItemMainhand(), ItemScythe.class)) return false;
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            TargetQueueHelper.clearTarget(this.attacker);
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemScythe.class)) return;
        ItemScythe scythe = (ItemScythe) held.getItem();
        float distance = this.attacker.getDistance(this.target);
        float attackRange = ATTACK_RANGE;
        if (this.attackDamage > 4.0f) {
            float width = Math.min(this.target.width * 2.0f, 1.0f);
            attackRange += width * 3.0f;
        }
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        CombatHelper.strafeLook(this.attacker, dx, dz);
        double rad = Math.toRadians(this.attacker.rotationYaw);
        Random rand = this.attacker.getRNG();
        if (distance > attackRange) {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.25);
            if (rand.nextFloat() < 0.125f) {
                scythe.sweepingMove(held, this.attacker, rad, 1.5);
            }
        } else {
            if (distance > attackRange * 0.5f) this.attacker.getNavigator().tryMoveToEntityLiving(target, 0.1);
            else CombatHelper.strafeTowards(this.attacker, dx, dz, 1.5);
            if (--this.attackTime <= 0) {
                float ratio = 2.0f - CombatHelper.safeRatio(this.attacker);
                if (rand.nextFloat() < 0.2f) {
                    scythe.sweepingMove(held, this.attacker, rad, ratio);
                    scythe.sweepingAttack(held, this.attacker, this.attackDamage * ratio);
                    int duration = 64 + 8 * rand.nextInt(32);
                    int amplifier = rand.nextInt(8);
                    this.target.addPotionEffect(new PotionEffect(PotionLoader.corruption, duration, amplifier));
                } else {
                    this.attacker.attackEntityAsMob(this.target);
                    int duration = 16 + 8 * rand.nextInt(8);
                    int amplifier = rand.nextInt(4);
                    this.target.addPotionEffect(new PotionEffect(PotionLoader.corruption, duration, amplifier));
                }
                this.attackTime = ATTACK_INTERVAL;
            }
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.attacker.getNavigator().clearPath();
        this.attackTime = 0;
    }
}