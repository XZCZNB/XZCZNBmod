package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.ITargetQueue;
import com.github.xzcznb.util.TargetQueueHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.passive.EntityTameable;

import java.util.UUID;

public class EntityAIOwnerAttackByTarget extends EntityAITarget {
    private final EntityTameable tameable;
    private EntityLivingBase target;
    private UUID lastPushedId;

    public EntityAIOwnerAttackByTarget(EntityTameable tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.tameable.isTamed()) return false;
        EntityLivingBase owner = this.tameable.getOwner();
        if (owner == null) return false;
        this.target = owner.getRevengeTarget();
        if (this.target != null && this.target.getUniqueID().equals(this.lastPushedId)) return false;
        return this.isSuitableTarget(this.target, false) && this.tameable.shouldAttackEntity(this.target, owner);
    }

    @Override
    public void startExecuting() {
        if (this.target == null) return;
        if (this.tameable instanceof ITargetQueue) {
            TargetQueueHelper.addTargetFront((ITargetQueue) this.tameable, this.target);
        } else this.tameable.setAttackTarget(this.target);
        this.lastPushedId = this.target == null ? null : this.target.getUniqueID();
    }
}