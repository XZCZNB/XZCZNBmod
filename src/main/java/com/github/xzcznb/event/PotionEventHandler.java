package com.github.xzcznb.event;

import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.PotionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class PotionEventHandler {
    public PotionEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote) return;
        PotionEffect purification = entity.getActivePotionEffect(PotionLoader.purification);
        PotionEffect corruption = entity.getActivePotionEffect(PotionLoader.corruption);
        if (purification != null) {
            float multiplier = 1.5f + purification.getAmplifier() * 0.5f;
            event.setAmount(event.getAmount() * multiplier);
        } else if (corruption != null) {
            float multiplier = Math.max(0.0f, 0.5f - corruption.getAmplifier() * 0.1f);
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote) return;
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
        PotionHelper.removePotionEffectAndSync(entity, PotionLoader.corruption);
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
        PotionHelper.removePotionEffectAndSync(entity, PotionLoader.corruption);
        entity.addPotionEffect(new PotionEffect(PotionLoader.corruption, newDuration, amplifier - 1));
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        PotionEffect effect = event.getPotionEffect();
        if (effect.getPotion() != PotionLoader.corruption) return;
        NBTTagCompound data = event.getEntityLiving().getEntityData();
        data.setInteger("CorruptionLevelDuration", effect.getDuration());
    }

    @SubscribeEvent
    public void onPotionRemove(PotionEvent.PotionRemoveEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (effect == null) return;
        if (effect.getPotion() == PotionLoader.corruption) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) return;
        if (!PotionHelper.isPotion(left)) return;
        if (left.getItem() != right.getItem()) return;
        List<PotionEffect> effectsA = PotionUtils.getEffectsFromStack(left);
        List<PotionEffect> effectsB = PotionUtils.getEffectsFromStack(right);
        if (effectsA.isEmpty() || effectsB.isEmpty()) return;
        List<PotionEffect> merged = PotionHelper.mergeEffects(effectsA, effectsB);
        ItemStack result = new ItemStack(left.getItem(), 1, left.getMetadata());
        PotionHelper.writeEffects(result, merged);
        event.setOutput(result);
        event.setCost(1);
        event.setMaterialCost(0);
    }
}