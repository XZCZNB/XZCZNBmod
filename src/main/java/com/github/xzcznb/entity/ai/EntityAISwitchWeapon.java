package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.EntityLoyalZombie;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISwitchWeapon extends EntityAIBase {
    private final EntityLoyalZombie zombie;
    private final int size;
    private int weaponCount = 0;
    private int cooldown = 0;
    private final int[] slotBuffer;
    private static final int[] SLOT_COOLDOWN = {300, 275, 250, 225, 200, 50};

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
        this.weaponCount = 0;
        for (int i = 0; i < this.size; i++) {
            if (this.zombie.hasWeapon(i)) {
                this.slotBuffer[this.weaponCount++] = i;
            }
        }
        return this.weaponCount > 0;
    }

    @Override
    public void startExecuting() {
        if (this.weaponCount == 0) return;
        int slot = this.slotBuffer[this.zombie.getRNG().nextInt(this.weaponCount)];
        this.zombie.switchWeapon(slot);
        this.cooldown = SLOT_COOLDOWN[slot];
        EntityLivingBase target = this.zombie.getAttackTarget();
        if (target != null && target.isEntityAlive()) this.cooldown /= 2;
    }

    @Override
    public void resetTask() {
        this.weaponCount = 0;
    }
}