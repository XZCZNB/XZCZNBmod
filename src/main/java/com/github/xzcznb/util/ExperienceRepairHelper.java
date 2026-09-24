package com.github.xzcznb.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExperienceRepairHelper {

    public static boolean repairHeldItemWithXp(EntityLivingBase entity, EntityXPOrb xpOrb) {
        List<ItemStack> candidates = new ArrayList<>();
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR &&
                    slot.getSlotType() != EntityEquipmentSlot.Type.HAND) continue;
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.isEmpty()) continue;
            if (!stack.isItemEnchanted() || !stack.isItemDamaged()) continue;
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) <= 0) continue;
            candidates.add(stack);
        }
        if (candidates.isEmpty()) return false;
        ItemStack target = candidates.get(entity.getRNG().nextInt(candidates.size()));
        int xpValue = xpOrb.getXpValue();
        int repairAmount = xpValue * 2;
        int currentDamage = target.getItemDamage();
        int newDamage = Math.max(0, currentDamage - repairAmount);
        target.setItemDamage(newDamage);
        xpOrb.setDead();
        return true;
    }
}