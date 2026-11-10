package com.github.xzcznb.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackChase extends EntityAIBase {
    private final EntityCreature attacker;
    private EntityLivingBase target;
    private static final double MIN_CHASE_SQ = 1024.0;
    private static final double MAX_CHASE_SQ = 4096.0;

    public EntityAIAttackChase(EntityCreature attacker) {
        this.attacker = attacker;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        double distanceSq = this.attacker.getDistanceSq(this.target);
        return distanceSq >= MIN_CHASE_SQ && distanceSq < MAX_CHASE_SQ;
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) return;
        this.attacker.getNavigator().tryMoveToXYZ(
                this.attacker.posX + dx / len * 9.0,
                this.attacker.posY,
                this.attacker.posZ + dz / len * 9.0,
                1.8
        );
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.attacker.getNavigator().clearPath();
    }
}