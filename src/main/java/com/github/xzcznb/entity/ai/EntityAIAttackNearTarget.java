package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.ITargetQueue;
import com.github.xzcznb.util.TargetQueueHelper;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAIAttackNearTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
    private final Predicate<? super T> targetSelector;

    public EntityAIAttackNearTarget(EntityCreature attacker, Class<T> targetClass, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(attacker, targetClass, chance, checkSight, onlyNearby, targetSelector);
        this.targetSelector = targetSelector;
    }

    @Override
    public boolean shouldExecute() {
        if (this.taskOwner.getAttackTarget() != null) return false;
        double range = this.getTargetDistance();
        double rangeSq = range * range;
        int added = 0;
        if (this.taskOwner instanceof ITargetQueue) {
            ITargetQueue attacker = (ITargetQueue) this.taskOwner;
            for (Entity entity : this.taskOwner.world.loadedEntityList) {
                if (added >= 4) break;
                if (!(entity instanceof EntityLivingBase)) continue;
                EntityLivingBase living = (EntityLivingBase) entity;
                if (this.taskOwner.getDistanceSq(living) > rangeSq) continue;
                if (!TargetQueueHelper.shouldAddTarget(this.taskOwner, living)) continue;
                if (!this.taskOwner.canEntityBeSeen(living)) continue;
                T target = this.targetClass.cast(living);
                if (!this.targetSelector.apply(target)) continue;
                if (added == 0) {
                    TargetQueueHelper.addTargetFront(attacker, target);
                } else {
                    TargetQueueHelper.addTargetBack(attacker, target);
                }
                added++;
            }
        } else {
            for (Entity entity : this.taskOwner.world.loadedEntityList) {
                if (!(entity instanceof EntityLivingBase)) continue;
                EntityLivingBase living = (EntityLivingBase) entity;
                if (this.taskOwner.getDistanceSq(living) > rangeSq) continue;
                if (!TargetQueueHelper.shouldAddTarget(this.taskOwner, living)) continue;
                if (!this.taskOwner.canEntityBeSeen(living)) continue;
                T target = this.targetClass.cast(living);
                if (!this.targetSelector.apply(target)) continue;
                this.taskOwner.setAttackTarget(target);
                return false;
            }
        }
        return false;
    }
}