package com.github.xzcznb.inventory;

import net.minecraft.item.ItemStack;

public class Trade {
    public final ItemStack price;
    public final ItemStack price2;
    public final ItemStack product;

    public Trade(ItemStack price, ItemStack price2, ItemStack product) {
        if (!price.isEmpty() && !price2.isEmpty()
                && ItemStack.areItemsEqual(price, price2)
                && ItemStack.areItemStackTagsEqual(price, price2)) {
            this.price = price.copy();
            this.price.grow(price2.getCount());
            this.price2 = ItemStack.EMPTY;
        } else {
            this.price = price;
            this.price2 = price2;
        }
        this.product = product;
    }

    public Trade(ItemStack price, ItemStack product) {
        this(price, ItemStack.EMPTY, product);
    }

    public boolean hasSecondPrice() {
        return !price2.isEmpty();
    }
}