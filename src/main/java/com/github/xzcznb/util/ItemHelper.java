package com.github.xzcznb.util;

import com.github.xzcznb.item.ItemMace;
import com.github.xzcznb.item.ItemSpear;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Collection;

public class ItemHelper {

    public static int getTotalEnchantLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return 0;
        NBTTagList enchants = tag.getTagList("ench", 10);
        int totalLevel = 0;
        for (int i = 0; i < enchants.tagCount(); i++) {
            NBTTagCompound enchant = enchants.getCompoundTagAt(i);
            totalLevel += enchant.getShort("lvl");
        }
        return totalLevel;
    }

    public static double getWeaponDamage(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        double baseDamage = 0;
        Collection<AttributeModifier> modifiers = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND)
                .get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        if (modifiers != null) for (AttributeModifier modifier : modifiers) {
            baseDamage += modifier.getAmount();
        }
        int sharpness = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int smite = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.SMITE, stack);
        int bane = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, stack);
        int power = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        int total = sharpness + smite + bane + power;
        if (stack.getItem() instanceof ItemBow) baseDamage = 7.5;
        if (stack.getItem() instanceof ItemSpear) baseDamage = 6.5;
        if (stack.getItem() instanceof ItemMace) baseDamage = 5.5;
        if (stack.getItem() == Items.TOTEM_OF_UNDYING) baseDamage = 4.5;
        return baseDamage + total * 1.25;
    }

    public static boolean isBetterItem(ItemStack currentItem, ItemStack newItem) {
        if (currentItem.isEmpty()) return true;
        if (newItem.isEmpty()) return false;
        if (currentItem.getItem() instanceof ItemArmor && newItem.getItem() instanceof ItemArmor) {
            int currentArmor = ((ItemArmor) currentItem.getItem()).damageReduceAmount;
            int newArmor = ((ItemArmor) newItem.getItem()).damageReduceAmount;
            if (newArmor != currentArmor) return newArmor > currentArmor;
            else return getTotalEnchantLevel(newItem) > getTotalEnchantLevel(currentItem);
        }
        double currentDamage = getWeaponDamage(currentItem);
        double newDamage = getWeaponDamage(newItem);
        if (newDamage != currentDamage) return newDamage > currentDamage;
        else return getTotalEnchantLevel(newItem) > getTotalEnchantLevel(currentItem);
    }
}