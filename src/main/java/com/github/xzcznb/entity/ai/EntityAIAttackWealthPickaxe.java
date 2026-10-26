package com.github.xzcznb.entity.ai;

import com.github.xzcznb.entity.IBlockValue;
import com.github.xzcznb.entity.ICast;
import com.github.xzcznb.item.ItemWealthPickaxe;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.*;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackWealthPickaxe extends EntityAIBase {
    private final EntityCreature attacker;
    private final ICast spellCaster;
    private EntityLivingBase target;
    private int attackTime = 0;
    private static final int ATTACK_INTERVAL = 6;
    private static final int AURIFY_INTERVAL = 79;
    private static final float ATTACK_RANGE_SQ = 36.0f;
    private static final float SAFE_RANGE_SQ = 144.0f;
    private static final float MAX_RANGE_SQ = 576.0f;

    public EntityAIAttackWealthPickaxe(EntityCreature attacker) {
        this.attacker = attacker;
        this.spellCaster = attacker instanceof ICast ? (ICast) attacker : null;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        if (this.spellCaster == null) return false;
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) {
            this.target = null;
            this.attacker.setAttackTarget(null);
            return false;
        }
        return ItemHelper.holding(this.attacker.getHeldItemMainhand(), ItemWealthPickaxe.class);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void updateTask() {
        if (this.target == null || !this.target.isEntityAlive()) return;
        ItemStack held = this.attacker.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemWealthPickaxe.class)) return;
        double distanceSq = this.attacker.getDistanceSq(this.target);
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        this.attacker.getLookHelper().setLookPositionWithEntity(this.target, 30.0f, 30.0f);
        if (distanceSq > MAX_RANGE_SQ) {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.8);
        } else {
            if (--this.attackTime <= 0) {
                if (distanceSq > ATTACK_RANGE_SQ) {
                    float chance = GoldenIdolHelper.aurifyChance(held, this.target);
                    this.attackTime = AURIFY_INTERVAL;
                    this.spellCaster.setCastingTicks(AURIFY_INTERVAL);
                    EntityLivingBase beneficiary = SummonHelper.getBeneficiary(this.attacker);
                    if (GoldenIdolHelper.tryAurify(chance, this.attacker, this.target)) {
                        int duration = 96 + this.attacker.getRNG().nextInt(64);
                        int amplifier = this.attacker.getRNG().nextInt(2);
                        beneficiary.addPotionEffect(new PotionEffect(PotionLoader.purification, duration, amplifier));
                    } else {
                        this.attacker.attackEntityAsMob(this.target);
                        float rand = this.attacker.getRNG().nextFloat();
                        if (rand < 0.0625f) {
                            this.attackTime /= 16;
                            CombatHelper.healByMissingHealth(beneficiary);
                            PotionHelper.removePotionEffects(beneficiary, false, true);
                            PotionHelper.removePotionEffects(this.target, true, false);
                        } else if (rand < 0.25f) {
                            this.attackTime /= 4;
                            int duration = 16 + this.attacker.getRNG().nextInt(16);
                            int amplifier = this.attacker.getRNG().nextInt(4);
                            this.target.addPotionEffect(new PotionEffect(PotionLoader.corruption, duration, amplifier));
                        } else {
                            if (this.attacker instanceof IBlockValue) {
                                IBlockValue entity = (IBlockValue) beneficiary;
                                float amount = entity.getBlockValue() + this.attacker.getRNG().nextInt(64);
                                entity.setBlockValue(amount);
                            }
                        }
                    }
                } else {
                    this.spellCaster.setCastingTicks(0);
                    this.attacker.attackEntityAsMob(this.target);
                    this.attackTime = ATTACK_INTERVAL;
                }
            }
            if (distanceSq < SAFE_RANGE_SQ) {
                this.attackTime -= ATTACK_INTERVAL;
                double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
                if (horizDist < 0.0001) horizDist = 0.0001;
                this.attacker.getNavigator().tryMoveToXYZ(
                        this.attacker.posX - dx / horizDist * 6.0,
                        this.attacker.posY,
                        this.attacker.posZ - dz / horizDist * 6.0,
                        1.8
                );
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