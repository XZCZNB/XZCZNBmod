package com.github.xzcznb.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class ShopInventory extends InventoryBasic {

    public ShopInventory() {
        super("shop", false, 54);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }
}