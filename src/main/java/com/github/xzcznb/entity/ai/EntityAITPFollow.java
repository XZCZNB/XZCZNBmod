package com.github.xzcznb.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAITPFollow extends EntityAIBase {

    private final EntityTameable tameable;
    private EntityLivingBase owner;
    private final double distance;

    public EntityAITPFollow(EntityTameable tameable, double distance) {
        this.tameable = tameable;
        this.distance = distance;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase owner = this.tameable.getOwner();
        if (owner == null || !owner.isEntityAlive()) return false;
        this.owner = owner;
        return this.tameable.getDistance(this.owner) > this.distance;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void startExecuting() {
        if (owner == null || !owner.isEntityAlive()) return;
        this.tameable.setLocationAndAngles(
                this.owner.posX,
                this.owner.posY,
                this.owner.posZ,
                this.tameable.rotationYaw,
                this.tameable.rotationPitch
        );
        this.tameable.getNavigator().clearPath();
    }
}