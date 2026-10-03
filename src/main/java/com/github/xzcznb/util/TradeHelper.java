package com.github.xzcznb.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class TradeHelper {

    public static boolean consumeItem(EntityPlayer player, ItemStack required) {
        if (required.isEmpty()) return false;
        int remaining = required.getCount();
        remaining = consumeFromStack(player.getHeldItemMainhand(), required, remaining);
        if (remaining <= 0) return true;
        remaining = consumeFromStack(player.getHeldItemOffhand(), required, remaining);
        if (remaining <= 0) return true;
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.mainInventory.get(i);
            remaining = consumeFromStack(stack, required, remaining);
            if (stack.isEmpty()) {
                inv.mainInventory.set(i, ItemStack.EMPTY);
            }
            if (remaining <= 0) return true;
        }
        return false;
    }

    private static int consumeFromStack(ItemStack stack, ItemStack required, int remaining) {
        if (stack.isEmpty()) return remaining;
        if (!stack.isItemEqual(required)) return remaining;
        if (!ItemStack.areItemStackTagsEqual(stack, required)) return remaining;
        int take = Math.min(stack.getCount(), remaining);
        stack.shrink(take);
        return remaining - take;
    }

    public static void giveItem(EntityPlayer player, ItemStack product) {
        ItemStack copy = product.copy();
        if (!player.inventory.addItemStackToInventory(copy)) {
            player.dropItem(copy, false);
        }
    }

    public static boolean trade(EntityPlayer player, ItemStack required, ItemStack product) {
        if (!consumeItem(player, required)) return false;
        giveItem(player, product);
        return true;
    }
}