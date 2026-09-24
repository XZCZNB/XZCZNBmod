package com.github.xzcznb.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackLeap extends EntityAIBase {
    private final EntityCreature attacker;
    private EntityLivingBase target;
    private final float leapMotionY;
    private final float leapChance;
    private static final float LEAP_RANGE_MIN = 2.0f;
    private static final float LEAP_RANGE_MAX = 6.0f;

    public EntityAIAttackLeap(EntityCreature leapingEntity, float motionY, float chance) {
        this.attacker = leapingEntity;
        this.leapMotionY = motionY;
        this.leapChance = chance;
        this.setMutexBits(1);
    }

    public EntityAIAttackLeap(EntityCreature leapingEntity, float motionY) {
        this(leapingEntity, motionY, 0.4f);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        double distance = this.attacker.getDistance(this.target);
        return distance >= LEAP_RANGE_MIN && distance <= LEAP_RANGE_MAX
                && this.attacker.onGround
                && this.attacker.getRNG().nextFloat() < this.leapChance;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.attacker.onGround;
    }

    @Override
    public void startExecuting() {
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
        if (horizDist < 0.0001) return;
        this.attacker.motionX += dx / horizDist * 0.4 + this.attacker.motionX * 0.2;
        this.attacker.motionZ += dz / horizDist * 0.4 + this.attacker.motionZ * 0.2;
        this.attacker.motionY = this.leapMotionY;
    }
}