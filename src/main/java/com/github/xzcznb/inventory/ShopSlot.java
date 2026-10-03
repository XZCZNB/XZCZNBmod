package com.github.xzcznb.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.List;

public class ShopSlot extends Slot {

    public ShopSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }

    @Override
    public void putStack(ItemStack stack) {
        this.inventory.setInventorySlotContents(this.getSlotIndex(), stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack oldStack, ItemStack newStack) {}

    public static ItemStack createDisplay(Trade trade, boolean showPriceFirst) {
        ItemStack stack = showPriceFirst ? trade.price.copy() : trade.product.copy();
        List<String> lore = new ArrayList<>();
        if (showPriceFirst) {
            lore.add("\u00A77Exchange: \u00A7e" + trade.product.getDisplayName() + " x" + trade.product.getCount());
        } else {
            lore.add("\u00A77Price: \u00A7e" + trade.price.getDisplayName() + " x" + trade.price.getCount());
        }
        NBTTagCompound display = stack.getOrCreateSubCompound("display");
        NBTTagList loreList = new NBTTagList();
        for (String s : lore) {
            loreList.appendTag(new NBTTagString(s));
        }
        display.setTag("Lore", loreList);
        return stack;
    }

    public static ItemStack createSoldOut() {
        ItemStack stack = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 7);
        NBTTagCompound tag = stack.getOrCreateSubCompound("display");
        tag.setString("Name", "\u00A7cSold Out");
        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString("\u00A74This Category Is Sold Out"));
        tag.setTag("Lore", lore);
        NBTTagCompound root = stack.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            stack.setTagCompound(root);
        }
        root.setInteger("HideFlags", 63);
        return stack;
    }
}