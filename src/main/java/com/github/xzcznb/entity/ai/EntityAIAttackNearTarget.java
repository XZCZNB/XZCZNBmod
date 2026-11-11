package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.ITargetQueue;
import com.github.xzcznb.util.TargetQueueHelper;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackNearTarget<T extends EntityLivingBase> extends EntityAIBase {
    private final EntityCreature attacker;
    private final Class<T> targetClass;
    private final Predicate<? super T> targetSelector;

    public EntityAIAttackNearTarget(EntityCreature attacker, Class<T> targetClass, Predicate<? super T> targetSelector) {
        this.attacker = attacker;
        this.targetClass = targetClass;
        this.targetSelector = targetSelector;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.attacker.getAttackTarget() != null) return false;
        double range = this.attacker.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
        double rangeSq = range * range;
        int added = 0;
        if (this.attacker instanceof ITargetQueue) {
            ITargetQueue attacker = (ITargetQueue) this.attacker;
            for (Entity entity : this.attacker.world.loadedEntityList) {
                if (added >= 4) break;
                if (!(entity instanceof EntityLivingBase)) continue;
                EntityLivingBase living = (EntityLivingBase) entity;
                if (this.attacker.getDistanceSq(living) > rangeSq) continue;
                if (!TargetQueueHelper.shouldAddTarget(this.attacker, living)) continue;
                if (!this.attacker.canEntityBeSeen(living)) continue;
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
            for (Entity entity : this.attacker.world.loadedEntityList) {
                if (!(entity instanceof EntityLivingBase)) continue;
                EntityLivingBase living = (EntityLivingBase) entity;
                if (this.attacker.getDistanceSq(living) > rangeSq) continue;
                if (!TargetQueueHelper.shouldAddTarget(this.attacker, living)) continue;
                if (!this.attacker.canEntityBeSeen(living)) continue;
                T target = this.targetClass.cast(living);
                if (!this.targetSelector.apply(target)) continue;
                this.attacker.setAttackTarget(target);
                return false;
            }
        }
        return false;
    }
}