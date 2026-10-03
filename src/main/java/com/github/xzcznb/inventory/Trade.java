package com.github.xzcznb.inventory;

import net.minecraft.item.ItemStack;

public class Trade {
    public final ItemStack price;
    public final ItemStack product;

    public Trade(ItemStack price, ItemStack product) {
        this.price = price;
        this.product = product;
    }
}