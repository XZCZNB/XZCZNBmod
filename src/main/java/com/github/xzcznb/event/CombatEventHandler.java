package com.github.xzcznb.event;

import com.github.xzcznb.entity.EntityRoyalGuard;
import com.github.xzcznb.entity.EntityRoyalGuardDecoy;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
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
            PotionEffect corruption = target.getActivePotionEffect(PotionLoader.corruption);
            if (corruption != null) {
                float multiplier = 1.5f + corruption.getAmplifier() * 0.5f;
                event.setAmount(event.getAmount() * multiplier);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        Entity attacker = event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        if (target instanceof EntityRoyalGuardDecoy) return;
        if (attacker instanceof EntityRoyalGuard) {
            ((EntityRoyalGuard) attacker).onKill();
        }
    }
}