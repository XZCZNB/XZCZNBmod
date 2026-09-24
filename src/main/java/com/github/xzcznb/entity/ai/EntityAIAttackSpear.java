package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.EntityLoyalZombie;
import com.github.xzcznb.item.ItemSpear;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import static com.github.xzcznb.util.CombatHelper.healByMissingHealth;
import static com.github.xzcznb.util.CombatHelper.strafeLook;

public class EntityAIAttackSpear extends EntityAIBase {
    private final EntityCreature attacker;
    private EntityLivingBase target;
    private int attackTick = 0;
    private static final float ATTACK_RANGE = 4.5f;
    private static final int ATTACK_INTERVAL = 10;

    public EntityAIAttackSpear(EntityCreature attacker) {
        this.attacker = attacker;
        this.setMutexBits(2);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (this.attacker instanceof EntityLoyalZombie) {
            ((EntityLoyalZombie) this.attacker).setUsingSpear(true);
        }
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        ItemStack held = this.attacker.getHeldItemMainhand();
        return !held.isEmpty() && held.getItem() instanceof ItemSpear;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemSpear)) return;
        ItemSpear spear = (ItemSpear) held.getItem();
        float distance = this.attacker.getDistance(this.target);
        float width = Math.min(this.target.width, 1.0f);
        float attackRange = ATTACK_RANGE + width;
        if (this.attacker instanceof EntityTameable) attackRange += Math.min(2.0f * width, 1.0f);
        this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.2);
        if (distance <= attackRange) {
            double dx = this.target.posX - this.attacker.posX;
            double dz = this.target.posZ - this.attacker.posZ;
            double eyeY = this.attacker.posY + this.attacker.getEyeHeight();
            double targetY = this.target.posY + this.target.height * 0.4;
            double dy = targetY - eyeY;
            float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
            strafeLook(this.attacker, dx, dz);
            this.attacker.rotationPitch = targetPitch;
            if (this.attacker instanceof EntityLoyalZombie) {
                ((EntityLoyalZombie) this.attacker).setSpearAimPitch(targetPitch);
            }
            float chance = distance > (attackRange / 2.0f) ? 0.2f : 0.5f;
            if (++this.attackTick >= ATTACK_INTERVAL) {
                float damage = 1.0f - this.attacker.getHealth() / this.attacker.getMaxHealth();
                if (this.attacker.getRNG().nextFloat() < chance) {
                    this.attacker.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 1));
                    healByMissingHealth(this.attacker);
                    spear.doChargeMove(this.attacker, held);
                }
                spear.doChargeAttack(this.attacker, held, 4.0f * damage + 1.0f);
                this.attackTick = 0;
            }
        }
        else {
            if (this.attacker.getRNG().nextFloat() < 0.2f) {
                this.attacker.addPotionEffect(new PotionEffect(MobEffects.SPEED, 10, 1));
                this.attacker.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 4));
                spear.doChargeMove(this.attacker, held);
            }
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        if (this.attacker instanceof EntityLoyalZombie) {
            ((EntityLoyalZombie) this.attacker).setUsingSpear(false);
        }
        this.attacker.getNavigator().clearPath();
        this.attackTick = 0;
    }
}