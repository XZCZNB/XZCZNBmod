package com.github.xzcznb.entity.ai;

import com.github.xzcznb.item.ItemMace;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

import static com.github.xzcznb.util.CombatHelper.strafeLook;
import static com.github.xzcznb.util.CombatHelper.strafeTowards;

public class EntityAIAttackMace extends EntityAIBase {

    private final EntityCreature attacker;
    private EntityLivingBase target;
    private int attackTick = 0;
    private static final int ATTACK_INTERVAL = 10;
    private static final float ATTACK_RANGE = 4.5f;

    public EntityAIAttackMace(EntityCreature attacker) {
        this.attacker = attacker;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        ItemStack held = this.attacker.getHeldItemMainhand();
        return !held.isEmpty() && held.getItem() instanceof ItemMace;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemMace)) return;
        ItemMace mace = (ItemMace) held.getItem();
        float distance = this.attacker.getDistance(this.target);
        float width = Math.min(this.target.width, 1.0f);
        float attackRange = ATTACK_RANGE + width * 2;
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        strafeLook(this.attacker, dx, dz);
        if (distance <= attackRange) {
            strafeTowards(this.attacker, dx, dz, 1.2, 0.6, 40.0, 20.0);
            if (++this.attackTick >= ATTACK_INTERVAL) {
                if (this.attacker.fallDistance > 6.0f) {
                    float damage = mace.onLeftClickAttack(held, this.attacker, this.target, 1.0f);
                    this.attacker.heal(damage);
                }
                else {
                    if (Math.abs(this.attacker.posY - this.target.posY) < 0.5f && this.attacker.getRNG().nextFloat() < 0.5f) {
                        mace.doSweepingEdge(held, this.attacker, 4.0f);
                    }
                    else {
                        this.attacker.attackEntityAsMob(this.target);
                        this.attacker.fallDistance += 4.0f;
                    }
                }
                this.attackTick = 0;
            }
        } else {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.2);
            this.attackTick = 0;
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.attacker.getNavigator().clearPath();
        this.attackTick = 0;
    }
}