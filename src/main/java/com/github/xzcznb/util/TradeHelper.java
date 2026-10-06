package com.github.xzcznb.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class TradeHelper {

    public static boolean consumeItem(EntityPlayer player, ItemStack price) {
        if (price.isEmpty()) return false;
        int need = price.getCount();
        ItemStack offHand = player.getHeldItemOffhand();
        int total = 0;
        total += countInStack(offHand, price);
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < 36; i++) {
            total += countInStack(inv.mainInventory.get(i), price);
        }
        if (total < need) return false;
        int remaining = need;
        if (countInStack(offHand, price) > 0) {
            int take = Math.min(offHand.getCount(), remaining);
            offHand.shrink(take);
            remaining -= take;
            if (remaining <= 0) return true;
        }
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.mainInventory.get(i);
            if (countInStack(stack, price) <= 0) continue;
            int take = Math.min(stack.getCount(), remaining);
            stack.shrink(take);
            remaining -= take;
            if (stack.isEmpty()) {
                inv.mainInventory.set(i, ItemStack.EMPTY);
            }
            if (remaining <= 0) return true;
        }
        return true;
    }

    private static int countInStack(ItemStack stack, ItemStack price) {
        if (stack.isEmpty()) return 0;
        if (!stack.isItemEqual(price)) return 0;
        if (!ItemStack.areItemStackTagsEqual(stack, price)) return 0;
        return stack.getCount();
    }

    public static void giveProduct(EntityPlayer player, ItemStack product) {
        ItemStack copy = product.copy();
        if (!player.inventory.addItemStackToInventory(copy)) {
            player.dropItem(copy, false);
        }
    }

    public static boolean trade(EntityPlayer player, ItemStack price, ItemStack product) {
        if (price.isEmpty() || VIPHelper.isVip(player.getUniqueID())) {
            giveProduct(player, product);
            return true;
        }
        if (!consumeItem(player, price)) return false;
        giveProduct(player, product);
        return true;
    }
}