package com.github.xzcznb.item;

import com.github.xzcznb.XZCZNB;
import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.inventory.ShopGuiHandler;
import com.github.xzcznb.util.VIPHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemShop extends Item {

    public ItemShop() {
        this.setMaxStackSize(64);
        this.setTranslationKey("shop");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openGui(XZCZNB.instance, ShopGuiHandler.GUI_ID_SHOP, world, 0, 0, 0);
            if (!player.capabilities.isCreativeMode && !VIPHelper.isVip(player.getUniqueID())) {
                stack.shrink(1);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}