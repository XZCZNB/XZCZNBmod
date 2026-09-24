package com.github.xzcznb.entity.ai;

import com.github.xzcznb.item.ItemMace;
import com.github.xzcznb.item.ItemSpear;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

import static com.github.xzcznb.util.CombatHelper.strafeLook;
import static com.github.xzcznb.util.CombatHelper.strafeTowards;

public class EntityAIAttackOnItem extends EntityAIBase {
    protected EntityCreature attacker;
    private EntityLivingBase target;
    private int attackTick = 0;
    private static final int ATTACK_INTERVAL = 10;
    private static final float ATTACK_RANGE = 4.5f;

    public EntityAIAttackOnItem(EntityCreature attacker) {
        this.attacker = attacker;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (!held.isEmpty()) {
            Item item = held.getItem();
            return !(item instanceof ItemBow) && !(item instanceof ItemSpear) && !(item instanceof ItemMace);
        }
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        float distance = this.attacker.getDistance(this.target);
        float width = Math.min(this.target.width, 1.0f);
        float attackRange = ATTACK_RANGE + width * 2.0f;
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        strafeLook(this.attacker, dx, dz);
        if (distance > attackRange * 2.0f) {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.2);
        } else if (distance > attackRange) {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.0);
        } else {
            if (distance > attackRange * 0.7f) this.attacker.getNavigator().tryMoveToEntityLiving(target, 0.1);
            else strafeTowards(this.attacker, dx, dz, 1.6);
            if (++this.attackTick >= ATTACK_INTERVAL) {
                this.attacker.attackEntityAsMob(this.target);
                this.attackTick = 0;
            }
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.attacker.getNavigator().clearPath();
        this.attackTick = 0;
    }
}