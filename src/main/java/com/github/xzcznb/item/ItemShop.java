package com.github.xzcznb.item;

import com.github.xzcznb.XZCZNB;
import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.inventory.ShopGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemShop extends ItemFood {

    public ItemShop() {
        super(4, 8, false);
        this.setAlwaysEdible();
        this.setMaxStackSize(1);
        this.setTranslationKey("shop");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.openGui(XZCZNB.instance, ShopGuiHandler.GUI_ID_SHOP, world, 0, 0, 0);
        }
        super.onFoodEaten(stack, world, player);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}