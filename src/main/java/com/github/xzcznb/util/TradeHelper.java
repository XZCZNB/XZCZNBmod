package com.github.xzcznb.util;

import com.github.xzcznb.inventory.Trade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class TradeHelper {

    public static boolean hasEnough(EntityPlayer player, ItemStack price) {
        if (price.isEmpty()) return true;
        int need = price.getCount();
        int total = 0;
        total += countInStack(player.getHeldItemOffhand(), price);
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < 36; i++) {
            total += countInStack(inv.mainInventory.get(i), price);
        }
        return total >= need;
    }

    public static void consumeItem(EntityPlayer player, ItemStack price) {
        if (price.isEmpty()) return;
        int remaining = price.getCount();
        ItemStack offHand = player.getHeldItemOffhand();
        if (countInStack(offHand, price) > 0) {
            int take = Math.min(offHand.getCount(), remaining);
            offHand.shrink(take);
            remaining -= take;
            if (remaining <= 0) return;
        }
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = inv.mainInventory.get(i);
            if (countInStack(stack, price) <= 0) continue;
            int take = Math.min(stack.getCount(), remaining);
            stack.shrink(take);
            remaining -= take;
            if (stack.isEmpty()) {
                inv.mainInventory.set(i, ItemStack.EMPTY);
            }
            if (remaining <= 0) return;
        }
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

    public static boolean trade(EntityPlayer player, Trade trade) {
        if (trade.price.isEmpty() && trade.price2.isEmpty() || VIPHelper.isVip(player.getUniqueID())) {
            giveProduct(player, trade.product);
            return true;
        }
        if (!hasEnough(player, trade.price)) return false;
        if (trade.hasSecondPrice()) {
            if (!hasEnough(player, trade.price2)) return false;
            consumeItem(player, trade.price2);
        }
        consumeItem(player, trade.price);
        giveProduct(player, trade.product);
        return true;
    }
}