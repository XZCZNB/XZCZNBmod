package com.github.xzcznb.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class EntityAIAttackByTarget extends EntityAIHurtByTarget {

    private int notifyCooldown = 0;

    public EntityAIAttackByTarget(EntityCreature creature, boolean entityCallsForHelp) {
        super(creature, entityCallsForHelp);
        this.setMutexBits(0);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.notifyCooldown = 0;
        this.notifyAllies();
    }

    @Override
    public void updateTask() {
        super.updateTask();
        if (--this.notifyCooldown > 0) return;
        this.notifyCooldown = 40;
        this.notifyAllies();
    }

    private void notifyAllies() {
        if (!(this.taskOwner instanceof EntityTameable)) return;
        EntityTameable self = (EntityTameable) this.taskOwner;
        java.util.UUID selfOwnerId = self.getOwnerId();
        EntityLivingBase attacker = self.getAttackTarget();
        if (selfOwnerId == null || attacker == null) return;
        AxisAlignedBB box = self.getEntityBoundingBox().grow(36.0, 18.0, 36.0);
        List<EntityTameable> allies = self.world.getEntitiesWithinAABB(
                EntityTameable.class, box,
                input -> input != self
                        && input.isEntityAlive()
                        && selfOwnerId.equals(input.getOwnerId())
        );
        for (EntityTameable ally : allies) {
            if (ally.getAttackTarget() == null) {
                ally.setAttackTarget(attacker);
            }
        }
    }
}