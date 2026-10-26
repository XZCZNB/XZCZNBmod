package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.IBlockValue;
import com.github.xzcznb.entity.ISpear;
import com.github.xzcznb.item.ItemSpear;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.ItemHelper;
import com.github.xzcznb.util.SummonHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EntityAIAttackSpear extends EntityAIBase {
    private final EntityCreature attacker;
    private final ISpear spearAttacker;
    private final float attackDamage;
    private EntityLivingBase target;
    private int attackTime = 0;
    private static final float ATTACK_RANGE = 4.5f;
    private static final int ATTACK_INTERVAL = 10;

    public EntityAIAttackSpear(EntityCreature attacker) {
        this.attacker = attacker;
        this.spearAttacker = attacker instanceof ISpear ? (ISpear) attacker : null;
        this.attackDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        this.setMutexBits(2);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        if (this.spearAttacker != null) {
            this.spearAttacker.setUsingSpear(true);
        }
    }

    @Override
    public boolean shouldExecute() {
        if (this.spearAttacker == null) return false;
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            this.attacker.setAttackTarget(null);
            return false;
        }
        return ItemHelper.holding(this.attacker.getHeldItemMainhand(), ItemSpear.class);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemSpear.class)) return;
        ItemSpear spear = (ItemSpear) held.getItem();
        float distance = this.attacker.getDistance(this.target);
        float attackRange = ATTACK_RANGE;
        if (this.attackDamage > 4.0f) {
            float width = Math.min(this.target.width * 2.0f, 1.0f);
            attackRange += width * 2.0f;
        }
        this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.2);
        if (distance <= attackRange) {
            double dx = this.target.posX - this.attacker.posX;
            double dz = this.target.posZ - this.attacker.posZ;
            double eyeY = this.attacker.posY + this.attacker.getEyeHeight();
            double targetY = this.target.posY + this.target.height * 0.4;
            double dy = targetY - eyeY;
            float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
            CombatHelper.strafeLook(this.attacker, dx, dz);
            this.attacker.rotationPitch = targetPitch;
            this.spearAttacker.setSpearAimPitch(targetPitch);
            if (--this.attackTime <= 0) {
                float bonus = 1.0f - 0.75f * this.attacker.getHealth() / this.attacker.getMaxHealth();
                float chance = distance > (attackRange * 0.5f) ? 0.2f : 0.5f;
                if (this.attacker.getRNG().nextFloat() < chance) {
                    EntityLivingBase beneficiary = SummonHelper.getBeneficiary(this.attacker);
                    if (beneficiary instanceof IBlockValue) {
                        IBlockValue entity = (IBlockValue) beneficiary;
                        float amount = entity.getBlockValue() + this.attacker.getRNG().nextInt(16);
                        entity.setBlockValue(amount);
                    }
                    beneficiary.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 50, 4));
                    this.attacker.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 2));
                    spear.doChargeMove(this.attacker, held);
                }
                spear.doChargeAttack(this.attacker, held, this.attackDamage * bonus);
                this.attackTime = ATTACK_INTERVAL;
            }
        }
        else {
            if (this.attacker.getRNG().nextFloat() < 0.2f) {
                double dx = this.target.posX - this.attacker.posX;
                double dz = this.target.posZ - this.attacker.posZ;
                CombatHelper.strafeLook(this.attacker, dx, dz);
                spear.doChargeMove(this.attacker, held);
            }
        }
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.spearAttacker.setUsingSpear(false);
        this.attacker.getNavigator().clearPath();
        this.attackTime = 0;
    }
}