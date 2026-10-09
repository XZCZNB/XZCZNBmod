package com.github.xzcznb.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PotionHelper {

    public static class PotionResult {
        public final int amplifier;
        public final int duration;

        public PotionResult(int amplifier, int duration) {
            this.amplifier = amplifier;
            this.duration = duration;
        }
    }

    public static boolean isPotion(ItemStack stack) {
        return stack.getItem() == Items.POTIONITEM
                || stack.getItem() == Items.SPLASH_POTION
                || stack.getItem() == Items.LINGERING_POTION;
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

    public static List<PotionEffect> mergeEffects(List<PotionEffect> effectsA, List<PotionEffect> effectsB) {
        List<PotionEffect> merged = new ArrayList<>(effectsA);
        for (PotionEffect e : effectsB) {
            int index = findByPotion(merged, e.getPotion());
            if (index < 0) {
                merged.add(e);
            } else {
                PotionEffect existing = merged.get(index);
                PotionResult result = potionCalculation2(
                        existing.getDuration(), existing.getAmplifier(),
                        e.getDuration(), e.getAmplifier()
                );
                merged.set(index, new PotionEffect(e.getPotion(), result.duration, result.amplifier));
            }
        }
        return merged;
    }

    private static int findByPotion(List<PotionEffect> list, Potion potion) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPotion() == potion) return i;
        }
        return -1;
    }

    public static void writeEffects(ItemStack stack, List<PotionEffect> merged) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagList effectList = new NBTTagList();
        for (PotionEffect e : merged) {
            NBTTagCompound effectTag = new NBTTagCompound();
            effectTag.setByte("Id", (byte) Potion.getIdFromPotion(e.getPotion()));
            effectTag.setByte("Amplifier", (byte) e.getAmplifier());
            effectTag.setInteger("Duration", e.getDuration());
            effectList.appendTag(effectTag);
        }
        tag.setTag("CustomPotionEffects", effectList);
    }

    public static ItemStack customPotion(Item item, boolean positive, int[][] effects) {
        ItemStack stack = new ItemStack(item);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList effectList = new NBTTagList();
        for (int[] e : effects) {
            NBTTagCompound effect = new NBTTagCompound();
            effect.setByte("Id", (byte) e[0]);
            effect.setByte("Amplifier", (byte) e[1]);
            effect.setInteger("Duration", e[2]);
            effectList.appendTag(effect);
        }
        tag.setTag("CustomPotionEffects", effectList);
        int color = PotionHelper.generatePotionColor(positive);
        tag.setInteger("CustomPotionColor", color);
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
}