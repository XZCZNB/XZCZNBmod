package com.github.xzcznb.entity.ai;

import com.github.xzcznb.item.ItemMace;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.ItemHelper;
import com.github.xzcznb.util.SummonHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class EntityAIAttackMace extends EntityAIBase {
    private final EntityCreature attacker;
    private final float attackDamage;
    private EntityLivingBase target;
    private int attackTime = 0;
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
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            this.attacker.setAttackTarget(null);
            return false;
        }
        return ItemHelper.holding(this.attacker.getHeldItemMainhand(), ItemMace.class );
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
        if (distance > attackRange) {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.2);
        } else {
            CombatHelper.strafeTowards(this.attacker, dx, dz, 1.2, 0.6, 40.0, 20.0);
            EntityLivingBase beneficiary = SummonHelper.getBeneficiary(this.attacker);
            if (--this.attackTime <= 0) {
                float bonus = 1.0f - 0.75f * this.attacker.getHealth() / this.attacker.getMaxHealth();
                if (this.attacker.fallDistance > 6.0f) {
                    float damage = mace.onLeftClickAttack(held, this.attacker, this.target, this.attackDamage * bonus);
                    beneficiary.heal(damage);
                }
                else {
                    Random rand = this.attacker.getRNG();
                    if (Math.abs(this.attacker.posY - this.target.posY) < 1.0 && rand.nextFloat() < 0.5f) {
                        mace.doSweepingEdge(held, this.attacker, this.attackDamage * bonus);
                    }
                    else {
                        if (rand.nextFloat() < 0.5f) mace.onLeftClickAttack(held, this.attacker, this.target, this.attackDamage);
                        else this.attacker.attackEntityAsMob(this.target);
                        this.attacker.fallDistance += 4.0f;
                    }
                }
                this.attackTime = ATTACK_INTERVAL;
            }
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.attacker.getNavigator().clearPath();
        this.attackTime = 0;
    }
}