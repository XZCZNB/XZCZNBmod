package com.github.xzcznb.entity.ai;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.potion.PotionLoader;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class EntityAIUndeadDomain extends EntityAIBase {
    private final EntityCreature attacker;
    private final DamageSource magicDamage;
    private EntityLivingBase target;
    private int cooldown = 0;
    private static final int COOLDOWN_TICKS = 40;
    private static final float MAX_RANGE = 16.0f;

    public EntityAIUndeadDomain(EntityCreature attacker) {
        this.attacker = attacker;
        magicDamage = DamageSource.causeThornsDamage(attacker);
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive() || !this.target.isEntityUndead()) return false;
        return this.attacker.getDistance(this.target) <= MAX_RANGE;
    }

    @Override
    public void startExecuting() {
        this.cooldown = COOLDOWN_TICKS;
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        float rate = 4.0f - 3.0f * this.attacker.getHealth() / this.attacker.getMaxHealth();
        float missingHealth = this.target.getMaxHealth() - this.target.getHealth();
        float damage = rate * missingHealth * 0.08f;
        int duration = (int) (160 * rate * this.attacker.getRNG().nextFloat());
        this.target.addPotionEffect(new PotionEffect(PotionLoader.corruption, duration, this.attacker.getRNG().nextInt(8)));
        this.target.attackEntityFrom(magicDamage, damage);
        this.target.playSound(SoundLoader.ROYAL_GUARD_FALL_BIG, 1.0f, 1.0f);
        this.attacker.heal(damage);
    }

    @Override
    public void resetTask() {
        this.target = null;
    }
}