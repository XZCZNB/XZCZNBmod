package com.github.xzcznb.inventory;

import net.minecraft.item.ItemStack;

import java.util.List;

public class ShopCategory {
    public final String name;
    public final ItemStack icon;
    public final List<Trade> trades;
    public int count;

    public ShopCategory(String name, ItemStack icon, List<Trade> trades, int count) {
        this.name = name;
        this.icon = icon;
        this.trades = trades;
        this.count = count;
    }

    public boolean isSoldOut() {
        return count <= 0;
    }

    public void decrement() {
        if (count > 0) count--;
    }
}