package com.github.xzcznb.event;

import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.PotionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
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
            float multiplier = Math.max(0, 0.5f - corruption.getAmplifier() * 0.1f);
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
            data.removeTag("xzcznb_corruption_level_duration");
            return;
        }
        int newDuration = Math.max(amplifier * 1024, data.getInteger("xzcznb_corruption_level_duration") * 2);
        PotionHelper.removePotionEffectAndSync(entity, PotionLoader.corruption);
        entity.addPotionEffect(new PotionEffect(PotionLoader.corruption, newDuration, amplifier - 1));
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        PotionEffect effect = event.getPotionEffect();
        if (effect.getPotion() != PotionLoader.corruption) return;
        NBTTagCompound data = event.getEntityLiving().getEntityData();
        data.setInteger("xzcznb_corruption_level_duration", effect.getDuration());
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
    public void onPotionBrewPre(PotionBrewEvent.Pre event) {
        ItemStack ingredient = event.getItem(3);
        if (ingredient.isEmpty()) return;
        boolean isGunpowder = ingredient.getItem() == Items.GUNPOWDER;
        boolean isDragonBreath = ingredient.getItem() == Items.DRAGON_BREATH;
        if (!isGunpowder && !isDragonBreath) return;
        boolean hasCustom = false;
        for (int i = 0; i < 3; i++) {
            if (PotionHelper.isCustomPotion(event.getItem(i))) {
                hasCustom = true;
                break;
            }
        }
        if (!hasCustom) return;
        boolean anyConverted = false;
        for (int i = 0; i < 3; i++) {
            ItemStack potion = event.getItem(i);
            if (potion.isEmpty()) continue;
            ItemStack output = null;
            if (isGunpowder && potion.getItem() == Items.POTIONITEM) {
                output = new ItemStack(Items.SPLASH_POTION);
            } else if (isDragonBreath && potion.getItem() == Items.SPLASH_POTION) {
                output = new ItemStack(Items.LINGERING_POTION);
            }
            if (output == null) continue;
            NBTTagCompound tag = potion.getTagCompound();
            if (tag != null) {
                output.setTagCompound(tag.copy());
            }
            event.setItem(i, output);
            anyConverted = true;
        }
        if (anyConverted) {
            ingredient.shrink(1);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) return;
        if (!PotionHelper.isPotion(left)) return;
        if (right.getItem() == Items.REDSTONE) {
            if (PotionHelper.isTimeAdded(left)) return;
            ItemStack result = left.copy();
            NBTTagCompound tag = result.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                result.setTagCompound(tag);
            }
            NBTTagCompound display = tag.getCompoundTag("display");
            if (display.isEmpty()) {
                display = new NBTTagCompound();
                tag.setTag("display", display);
            }
            display.setString("Name", "\u00A7c" + left.getDisplayName());
            tag.setBoolean("xzcznb_is_time_added", true);
            event.setOutput(result);
            event.setCost(1);
            event.setMaterialCost(1);
            return;
        }
        if (left.getItem() != right.getItem()) return;
        List<PotionEffect> effectsA = PotionUtils.getEffectsFromStack(left);
        List<PotionEffect> effectsB = PotionUtils.getEffectsFromStack(right);
        if (effectsA.isEmpty() || effectsB.isEmpty()) return;
        boolean timeAdded = PotionHelper.isTimeAdded(left) && PotionHelper.isTimeAdded(right);
        boolean hasPurification = PotionHelper.findByPotion(effectsA, PotionLoader.purification) >= 0
                || PotionHelper.findByPotion(effectsB, PotionLoader.purification) >= 0;
        if (hasPurification) {
            effectsA = PotionHelper.removePotionEffects(effectsA, false, true);
            effectsB = PotionHelper.removePotionEffects(effectsB, false, true);
        }
        List<PotionEffect> merged = PotionHelper.mergeEffects(effectsA, effectsB, timeAdded);
        ItemStack result = new ItemStack(left.getItem(), 1, left.getMetadata());
        NBTTagCompound tag = new NBTTagCompound();
        PotionHelper.writeEffects(tag, merged);
        int color = PotionHelper.generatePotionColor(hasPurification);
        tag.setInteger("CustomPotionColor", color);
        result.setTagCompound(tag);
        event.setOutput(result);
        event.setCost(1);
        event.setMaterialCost(0);
    }
}