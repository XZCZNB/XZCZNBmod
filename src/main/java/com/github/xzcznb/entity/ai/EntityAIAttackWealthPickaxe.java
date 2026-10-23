package com.github.xzcznb.entity.ai;

import com.github.xzcznb.item.ItemWealthPickaxe;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.CombatHelper;
import com.github.xzcznb.util.GoldenIdolHelper;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackWealthPickaxe extends EntityAIBase {

    private final EntityCreature attacker;
    private EntityLivingBase target;
    private int attackTime = 0;
    private static final int ATTACK_INTERVAL = 6;
    private static final int AURIFY_INTERVAL = 79;
    private static final float ATTACK_RANGE = 4.5f;
    private static final float MAX_RANGE = 16.0f;

    public EntityAIAttackWealthPickaxe(EntityCreature attacker) {
        this.attacker = attacker;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.attacker.getAttackTarget();
        if (this.target == null || !this.target.isEntityAlive()) return false;
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
        float distance = this.attacker.getDistance(this.target);
        float width = Math.min(this.target.width, 1.0f);
        float attackRange = ATTACK_RANGE + width * 2;
        double dx = this.target.posX - this.attacker.posX;
        double dz = this.target.posZ - this.attacker.posZ;
        if (distance > MAX_RANGE) {
            this.attacker.getNavigator().tryMoveToEntityLiving(this.target, 1.2);
            this.attackTime = 0;
        } else if (distance > attackRange) {
            if (--this.attackTime <= 0) {
                float chance = 0.125f + ItemHelper.getTotalEnchantLevel(held) * 0.0625f;
                if (this.attacker.getRNG().nextFloat() < chance && GoldenIdolHelper.aurify(this.attacker, this.target)) {
                    int duration = 64 + 4 * this.attacker.getRNG().nextInt(16);
                    int amplifier = this.attacker.getRNG().nextInt(4);
                    this.attacker.addPotionEffect(new PotionEffect(PotionLoader.purification, duration, amplifier));
                } else {
                    this.attacker.attackEntityAsMob(this.target);
                }
                this.attackTime = AURIFY_INTERVAL;
            }
            double horizDist = MathHelper.sqrt(dx * dx + dz * dz);
            if (horizDist < 0.0001) horizDist = 0.0001;
            this.attacker.getNavigator().tryMoveToXYZ(
                    this.attacker.posX - dx / horizDist * 6.0,
                    this.attacker.posY,
                    this.attacker.posZ - dz / horizDist * 6.0,
                    1.2
            );
        } else {
            CombatHelper.strafeTowards(this.attacker, dx, dz, 1.2, 0.6, 40.0, 20.0);
            if (--this.attackTime <= 0) {
                this.attacker.attackEntityAsMob(this.target);
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