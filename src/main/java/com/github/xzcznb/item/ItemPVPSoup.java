package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPVPSoup extends ItemFood {
    public ItemPVPSoup() {
        super(0, 0, false);
        this.setAlwaysEdible();
        this.setMaxStackSize(64);
        this.setTranslationKey("pvpSoup");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.heal(6.0f);
        }
        if (stack.isEmpty()) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.BOWL));
        } else {
            if (!player.inventory.addItemStackToInventory(new ItemStack(Items.BOWL))) {
                player.dropItem(new ItemStack(Items.BOWL), false);
            }
        }
        super.onFoodEaten(stack, world, player);
    }
}