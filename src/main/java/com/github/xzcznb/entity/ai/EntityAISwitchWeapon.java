package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.EntityLoyalZombie;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Arrays;

public class EntityAISwitchWeapon extends EntityAIBase {
    private final EntityLoyalZombie zombie;
    private final int size;
    private int availableCount = 0;
    private int cooldown = 0;
    private static final int COOLDOWN_TICKS = 200;
    private final int[] slotBuffer;

    public EntityAISwitchWeapon(EntityLoyalZombie zombie) {
        this.zombie = zombie;
        this.size = zombie.getWeaponStorageSize();
        this.slotBuffer = new int[this.size];
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        this.availableCount = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.zombie.hasWeapon(i)) {
                this.slotBuffer[this.availableCount++] = i;
            }
        }
        return this.availableCount > 0;
    }

    @Override
    public void startExecuting() {
        this.cooldown = COOLDOWN_TICKS;
        EntityLivingBase target = this.zombie.getAttackTarget();
        if (target != null && target.isEntityAlive()) this.cooldown /= 2;
        if (this.availableCount == 0) return;
        int slot = this.slotBuffer[this.zombie.getRNG().nextInt(this.availableCount)];
        this.zombie.switchWeapon(slot);
    }

    @Override
    public void resetTask() {
        this.availableCount = 0;
    }
}