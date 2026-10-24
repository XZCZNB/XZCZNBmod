package com.github.xzcznb.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAIAttackNearTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

    public EntityAIAttackNearTarget(EntityCreature attacker, Class<T> targetClass, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(attacker, targetClass, chance, checkSight, onlyNearby, targetSelector);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.taskOwner.getAttackTarget();
        if (target != null) {
            if (!target.isEntityAlive()) {
                this.taskOwner.setAttackTarget(null);
                return super.shouldExecute();
            }
            return false;
        }
        return super.shouldExecute();
    }
}