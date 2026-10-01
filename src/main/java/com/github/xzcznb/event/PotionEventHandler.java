package com.github.xzcznb.event;

import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.PotionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionEventHandler {
    public PotionEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        PotionEffect corruption = event.getEntityLiving().getActivePotionEffect(PotionLoader.corruption);
        if (corruption == null) return;
        float multiplier = Math.max(0.0f, 0.5f - corruption.getAmplifier() * 0.1f);
        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.isPotionActive(PotionLoader.purification)) {
            if (effect.getPotion().isBadEffect()) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }
        if (effect.getPotion() != PotionLoader.corruption) return;
        PotionEffect existing = entity.getActivePotionEffect(PotionLoader.corruption);
        if (existing == null) return;
        event.setResult(Event.Result.DENY);
        PotionHelper.PotionResult result = PotionHelper.potionCalculation(
                existing.getDuration(), existing.getAmplifier(),
                effect.getDuration(), effect.getAmplifier()
        );
        entity.removePotionEffect(PotionLoader.corruption);
        entity.addPotionEffect(new PotionEffect(PotionLoader.corruption, result.duration, result.amplifier));
    }

    @SubscribeEvent
    public void onPotionExpiry(PotionEvent.PotionExpiryEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (effect == null || effect.getPotion() != PotionLoader.corruption) return;
        EntityLivingBase entity = event.getEntityLiving();
        int amplifier = effect.getAmplifier();
        NBTTagCompound data = entity.getEntityData();
        if (amplifier <= 0) {
            data.removeTag("CorruptionLevelDuration");
            return;
        }
        int newDuration = Math.max(amplifier * 1024, data.getInteger("CorruptionLevelDuration") * 2);
        entity.removePotionEffect(PotionLoader.corruption);
        entity.addPotionEffect(new PotionEffect(PotionLoader.corruption, newDuration, amplifier - 1));
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (effect.getPotion() != PotionLoader.corruption) return;
        NBTTagCompound data = event.getEntityLiving().getEntityData();
        data.setInteger("CorruptionLevelDuration", effect.getDuration());
    }
}