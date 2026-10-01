package com.github.xzcznb.event;

import com.github.xzcznb.entity.EntityLoyalZombie;
import com.github.xzcznb.entity.EntityLoyalZombieDecoy;
import com.github.xzcznb.item.ItemScythe;
import com.github.xzcznb.util.ItemHelper;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatEventHandler {
    public CombatEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        Entity entity = event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        if (entity instanceof EntityLivingBase && target != null) {
            EntityLivingBase attacker = (EntityLivingBase) entity;
            if (TeamHelper.isAlly(attacker, target)) {
                event.setCanceled(true);
                return;
            }
            ItemStack held = attacker.getHeldItemMainhand();
            if (ItemHelper.holding(held, ItemScythe.class)) {
                float damage = event.getAmount();
                float bonus = MathHelper.sqrt(4.0f + ItemHelper.getTotalEnchantLevel(held)) * 0.05f;
                attacker.heal(damage * bonus);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        Entity attacker = event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        if (target instanceof EntityLoyalZombieDecoy) return;
        if (attacker instanceof EntityLoyalZombie) {
            ((EntityLoyalZombie) attacker).onKill();
        }
    }
}