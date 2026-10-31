package com.github.xzcznb.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PotionHelper {
    public static final List<PotionHelper.PotionWeight> BUFF = Arrays.asList(
            new PotionHelper.PotionWeight(MobEffects.REGENERATION, 4),
            new PotionHelper.PotionWeight(MobEffects.SPEED, 3),
            new PotionHelper.PotionWeight(MobEffects.STRENGTH, 3),
            new PotionHelper.PotionWeight(MobEffects.RESISTANCE, 2),
            new PotionHelper.PotionWeight(MobEffects.HEALTH_BOOST, 2),
            new PotionHelper.PotionWeight(MobEffects.ABSORPTION, 1),
            new PotionHelper.PotionWeight(MobEffects.JUMP_BOOST, 1)
    );

    public static final List<PotionHelper.PotionWeight> DEBUFF = Arrays.asList(
            new PotionHelper.PotionWeight(MobEffects.WITHER, 4),
            new PotionHelper.PotionWeight(MobEffects.SLOWNESS, 3),
            new PotionHelper.PotionWeight(MobEffects.WEAKNESS, 3),
            new PotionHelper.PotionWeight(MobEffects.MINING_FATIGUE, 2),
            new PotionHelper.PotionWeight(MobEffects.POISON, 2),
            new PotionHelper.PotionWeight(MobEffects.BLINDNESS, 1),
            new PotionHelper.PotionWeight(MobEffects.LEVITATION, 1)
    );

    public static final List<PotionWeight> ARROW_DEBUFF = Arrays.asList(
            new PotionWeight(MobEffects.WITHER, 6),
            new PotionWeight(MobEffects.WEAKNESS, 3),
            new PotionWeight(MobEffects.SLOWNESS, 3),
            new PotionWeight(MobEffects.POISON, 1),
            new PotionWeight(MobEffects.MINING_FATIGUE, 1),
            new PotionWeight(MobEffects.BLINDNESS, 1),
            new PotionWeight(MobEffects.LEVITATION, 1)
    );


    public static class PotionResult {
        public final int amplifier;
        public final int duration;

        public PotionResult(int amplifier, int duration) {
            this.amplifier = amplifier;
            this.duration = duration;
        }
    }

    public static class PotionWeight {
        public final Potion potion;
        public final int weight;

        public PotionWeight(Potion potion, int weight) {
            this.potion = potion;
            this.weight = weight;
        }
    }

    public static boolean isPotion(ItemStack stack) {
        return stack.getItem() == Items.POTIONITEM
                || stack.getItem() == Items.SPLASH_POTION
                || stack.getItem() == Items.LINGERING_POTION;
    }

    public static boolean isCustomPotion(ItemStack stack) {
        if (!isPotion(stack)) return false;
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("CustomPotionEffects");
    }

    public static boolean isTimeAdded(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("xzcznb_is_time_added");
    }

    public static PotionResult potionCalculation(int oldDuration, int oldAmplifier, int newDuration, int newAmplifier) {
        float oldLevel = oldAmplifier + 1;
        float newLevel = newAmplifier + 1;
        int resultAmplifier;
        float resultDuration;
        if (oldLevel >= newLevel) {
            resultAmplifier = oldAmplifier;
            resultDuration = oldDuration + newLevel * newDuration / oldLevel;
        } else {
            resultAmplifier = newAmplifier;
            resultDuration = newDuration + oldLevel * oldDuration / newLevel;
        }
        return new PotionResult(resultAmplifier, (int) resultDuration);
    }

    public static PotionResult potionCalculation2(int oldDuration, int oldAmplifier, int newDuration, int newAmplifier) {
        if (oldAmplifier == newAmplifier) {
            if (oldAmplifier >= 127) {
                return new PotionResult(127, oldDuration + newDuration);
            }
            int resultAmplifier = oldAmplifier + 1;
            int resultDuration = (oldDuration + newDuration) * resultAmplifier / (resultAmplifier + 1);
            return new PotionResult(resultAmplifier, resultDuration);
        }
        return potionCalculation(oldDuration, oldAmplifier, newDuration, newAmplifier);
    }

    public static List<PotionEffect> mergeEffects(List<PotionEffect> effectsA, List<PotionEffect> effectsB, boolean isTimeAdded) {
        List<PotionEffect> merged = new ArrayList<>(effectsA);
        for (PotionEffect potionEffect : effectsB) {
            int index = findByPotion(merged, potionEffect.getPotion());
            if (index < 0) {
                merged.add(potionEffect);
            } else {
                PotionEffect existing = merged.get(index);
                PotionResult result;
                if (isTimeAdded) result = potionCalculation(existing.getDuration(), existing.getAmplifier(), potionEffect.getDuration(), potionEffect.getAmplifier());
                else result = potionCalculation2(existing.getDuration(), existing.getAmplifier(), potionEffect.getDuration(), potionEffect.getAmplifier());
                merged.set(index, new PotionEffect(potionEffect.getPotion(), result.duration, result.amplifier));
            }
        }
        return merged;
    }

    public static int findByPotion(List<PotionEffect> list, Potion potion) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPotion() == potion) return i;
        }
        return -1;
    }

    public static Potion randomPotion(List<PotionWeight> list, Random rand) {
        int total = 0;
        for (PotionWeight potion : list) total += potion.weight;
        if (total <= 0) return list.get(0).potion;
        int roll = rand.nextInt(total);
        int acculate = 0;
        for (PotionWeight potion : list) {
            acculate += potion.weight;
            if (roll < acculate) return potion.potion;
        }
        return list.get(0).potion;
    }

    public static void writeEffects(NBTTagCompound tag, List<PotionEffect> merged) {
        NBTTagList effectList = new NBTTagList();
        for (PotionEffect potionEffect : merged) {
            NBTTagCompound effectTag = new NBTTagCompound();
            effectTag.setByte("Id", (byte) Potion.getIdFromPotion(potionEffect.getPotion()));
            effectTag.setByte("Amplifier", (byte) potionEffect.getAmplifier());
            effectTag.setInteger("Duration", potionEffect.getDuration());
            effectList.appendTag(effectTag);
        }
        tag.setTag("CustomPotionEffects", effectList);
    }

    public static ItemStack customPotion(ItemStack stack, boolean positive, List<PotionEffect> potionEffects) {
        NBTTagCompound tag = new NBTTagCompound();
        writeEffects(tag, potionEffects);
        tag.setInteger("CustomPotionColor", generatePotionColor(positive));
        stack.setTagCompound(tag);
        return stack;
    }

    public static int generatePotionColor(boolean positive) {
        float hue = (float) Math.random() * 360f;
        float saturation;
        float brightness;
        if (positive) {
            saturation = 0.5f + (float) Math.random() * 0.3f;
            brightness = 0.8f + (float) Math.random() * 0.2f;
        } else {
            hue = hue * 0.5f + 140f * 0.5f;
            saturation = 0.7f + (float) Math.random() * 0.3f;
            brightness = 0.15f + (float) Math.random() * 0.15f;
        }
        int rgb = Color.HSBtoRGB(hue / 360f, saturation, brightness);
        return rgb & 0xffffff;
    }

    public static void removePotionEffectAndSync(EntityLivingBase entity, Potion potion) {
        PotionEffect removed = entity.removeActivePotionEffect(potion);
        if (removed == null) return;
        entity.onFinishedPotionEffect(removed);
    }

    public static void removePotionEffects(EntityLivingBase entity, boolean removePositive, boolean removeNegative) {
        List<PotionEffect> effects = new ArrayList<>();
        for (PotionEffect effect : entity.getActivePotionEffects()) {
            boolean bad = effect.getPotion().isBadEffect();
            if (bad && removeNegative) effects.add(effect);
            if (!bad && removePositive) effects.add(effect);
        }
        for (PotionEffect effect : effects) {
            removePotionEffectAndSync(entity, effect.getPotion());
        }
    }

    public static List<PotionEffect> removePotionEffects(List<PotionEffect> effects, boolean removePositive, boolean removeNegative) {
        List<PotionEffect> result = new ArrayList<>();
        for (PotionEffect effect : effects) {
            boolean bad = effect.getPotion().isBadEffect();
            if (bad && removeNegative) continue;
            if (!bad && removePositive) continue;
            result.add(effect);
        }
        return result;
    }
}