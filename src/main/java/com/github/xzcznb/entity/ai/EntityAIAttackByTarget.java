package com.github.xzcznb.entity.ai;

import com.github.xzcznb.util.TeamHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class EntityAIAttackByTarget extends EntityAIHurtByTarget {

    private final EntityCreature attacker;
    private EntityLivingBase target;
    private int notifyCooldown = 0;

    public EntityAIAttackByTarget(EntityCreature attacker, boolean entityCallsForHelp) {
        super(attacker, entityCallsForHelp);
        this.attacker = attacker;
        this.setMutexBits(0);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.target = this.attacker.getAttackTarget();
        this.notifyCooldown = 0;
        this.notifyAllies();
    }

    @Override
    public void updateTask() {
        super.updateTask();
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            this.attacker.setAttackTarget(null);
            return;
        }
        if (--this.notifyCooldown > 0) return;
        this.notifyCooldown = 40;
        this.notifyAllies();
    }

    private void notifyAllies() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        AxisAlignedBB box = this.attacker.getEntityBoundingBox().grow(36.0, 18.0, 36.0);
        List<EntityCreature> allies = this.attacker.world.getEntitiesWithinAABB(
                EntityCreature.class, box,
                input -> input != this.attacker
                        && input.isEntityAlive()
                        && TeamHelper.isAlly(this.attacker, input)
        );
        for (EntityCreature ally : allies) {
            if (ally.getAttackTarget() == null) {
                ally.setAttackTarget(this.target);
            }
        }
    }
}