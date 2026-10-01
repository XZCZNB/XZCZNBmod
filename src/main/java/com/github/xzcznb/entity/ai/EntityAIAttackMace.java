package com.github.xzcznb.entity.ai;

import com.github.xzcznb.item.ItemMace;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

public class EntityAIAttackMace extends EntityAIBase {

    private final EntityCreature attacker;
    private final float attackDamage;
    private EntityLivingBase target;
    private int attackTick = 0;
    private static final int ATTACK_INTERVAL = 10;
    private static final float ATTACK_RANGE = 4.5f;

    public EntityAIAttackMace(EntityCreature attacker) {
        this.attacker = attacker;
        this.attackDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
        ItemStack held = this.attacker.getHeldItemMainhand();
        return ItemHelper.holding(held, ItemMace.class );
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemMace.class)) return;
        ItemMace mace = (ItemMace) held.getItem();
        float distance = this.attacker.getDistance(this.target);
        float width = Math.min(this.target.width, 1.0f);
        float attackRange = ATTACK_RANGE + width * 2;
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        CombatHelper.strafeLook(this.attacker, dx, dz);
        if (distance <= attackRange) {
            CombatHelper.strafeTowards(this.attacker, dx, dz, 1.2, 0.6, 40.0, 20.0);
            if (++this.attackTick >= ATTACK_INTERVAL) {
                float bonus = 1.0f - 0.75f * this.attacker.getHealth() / this.attacker.getMaxHealth();
                if (this.attacker.fallDistance > 3.0f) {
                    float damage = mace.onLeftClickAttack(held, this.attacker, this.target, this.attackDamage * bonus);
                    this.attacker.heal(damage);
                }
                else {
                    if (Math.abs(this.attacker.posY - this.target.posY) < 0.5 && this.attacker.getRNG().nextFloat() < 0.5f) {
                        mace.doSweepingEdge(held, this.attacker, this.attackDamage * bonus);
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