package com.github.xzcznb.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class NavigateSlot extends Slot {

    private final ShopCategory category;

    public NavigateSlot(IInventory inventory, int index, ShopCategory category, int x, int y) {
        super(inventory, index, x, y);
        this.category = category;
    }

    @Override
    public boolean isItemValid(ItemStack stack) { return false; }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) { return false; }

    public ShopCategory getCategory() { return category; }

    public static ItemStack createNavDisplay(ShopCategory category, boolean selected) {
        ItemStack stack = category.icon.copy();
        NBTTagCompound display = stack.getOrCreateSubCompound("display");
        display.setString("Name", "\u00A7e" + category.name);
        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString("\u00A77Remaining: \u00A7a" + category.count));
        display.setTag("Lore", lore);
        if (selected) {
            stack.addEnchantment(Enchantments.UNBREAKING, 1);
            NBTTagCompound root = stack.getTagCompound();
            if (root == null) {
                root = new NBTTagCompound();
                stack.setTagCompound(root);
            }
            root.setInteger("HideFlags", 63);
        }
        return stack;
    }
}