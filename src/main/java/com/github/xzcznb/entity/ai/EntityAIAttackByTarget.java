package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.ITargetQueue;
import com.github.xzcznb.util.TargetQueueHelper;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class EntityAIAttackByTarget extends EntityAIBase {
    private final EntityCreature attacker;
    private int notifyCooldown = 0;

    public EntityAIAttackByTarget(EntityCreature attacker) {
        this.attacker = attacker;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        return this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
    }

    @Override
    public void updateTask() {
        if (--this.notifyCooldown > 0) return;
        this.notifyCooldown = 40;
        this.notifyAllies();
    }

    private void notifyAllies() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (!TargetQueueHelper.shouldAddTarget(this.attacker, target)) return;
        AxisAlignedBB box = this.attacker.getEntityBoundingBox().grow(36.0, 18.0, 36.0);
        List<EntityCreature> allies = this.attacker.world.getEntitiesWithinAABB(
                EntityCreature.class, box,
                input -> input != this.attacker
                        && input.isEntityAlive()
                        && TeamHelper.isAlly(this.attacker, input)
        );
        for (EntityCreature ally : allies) {
            if (ally instanceof ITargetQueue) {
                TargetQueueHelper.addTargetBack((ITargetQueue) ally, target);
            } else if (ally.getAttackTarget() == null) {
                ally.setAttackTarget(target);
            }
        }
    }
}