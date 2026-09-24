package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.util.CombatHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.EnumHelper;

import java.util.List;

import static com.github.xzcznb.util.ItemHelper.getTotalEnchantLevel;
import static com.github.xzcznb.util.TeamHelper.isAlly;

public class ItemSpear extends ItemSword {
    private static final float ATTACK_RANGE = 4.5f;
    private static final float ATTACK_DAMAGE = 4.0f;
    public static ToolMaterial Spear = EnumHelper.addToolMaterial("spear", 3, 1024, 30.0f, 0, 30);

    public ItemSpear() {
        super(Spear);
        this.setTranslationKey("spear");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return true;
    }

    public void doChargeAttack(EntityLivingBase attacker, ItemStack stack, float damage, Class<?>... excludedClasses) {
        if (attacker == null || attacker.world.isRemote || !attacker.isEntityAlive()) return;
        float attackRange = ATTACK_RANGE;
        if (attacker instanceof EntityTameable) attackRange += 2.0f;
        List<EntityLivingBase> targets = CombatHelper.rayTraceEntities(attacker, attackRange);
        if (targets.isEmpty()) return;
        float totalEnchantLevel = getTotalEnchantLevel(stack) * 0.3f + 1.0f;
        float attackDamage = ATTACK_DAMAGE + damage;
        if (attacker instanceof EntityTameable && attacker.getRNG().nextFloat() < 0.2f) attackDamage *= 5.0f;
        boolean hit = false;
        for (EntityLivingBase target : targets) {
            boolean shouldSkip = isAlly(attacker, target);
            for (Class<?> excluded : excludedClasses) {
                if (excluded == null) continue;
                if (excluded.isAssignableFrom(target.getClass())) {
                    shouldSkip = true;
                    break;
                }
            }
            if (shouldSkip) continue;
            hit = true;
            float speedBonus = MathHelper.sqrt(CombatHelper.getRelativeSpeed(attacker, target, 0.6, 0.1, 0.6) + 1.0f);
            float totalDamage = totalEnchantLevel * speedBonus * attackDamage;
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), totalDamage);
            target.addVelocity(attacker.motionX * 0.6, 0.1, attacker.motionZ * 0.6);
        }
        if(hit && attacker instanceof EntityPlayer) stack.damageItem(1, attacker);
    }

    public void doChargeAttack(EntityLivingBase attacker, ItemStack stack, Class<?>... excludedClasses) {
        doChargeAttack(attacker, stack, 0, excludedClasses);
    }

    public void doChargeAttack(EntityLivingBase attacker, ItemStack stack, float damage) {
        doChargeAttack(attacker, stack, damage, new Class<?>[0]);
    }

    public void doChargeAttack(EntityLivingBase attacker, ItemStack stack) {
        doChargeAttack(attacker, stack, 0, new Class<?>[0]);
    }

    public void doChargeMove(EntityLivingBase attacker, ItemStack stack) {
        if (attacker == null || attacker.world.isRemote || !attacker.isEntityAlive()) return;
        float yaw = attacker.rotationYaw;
        double speed = MathHelper.sqrt(getTotalEnchantLevel(stack) + 4) / 4;
        attacker.addVelocity(
                -Math.sin(Math.toRadians(yaw)) * speed,
                0,
                Math.cos(Math.toRadians(yaw)) * speed
        );
        attacker.velocityChanged = true;
    }
}