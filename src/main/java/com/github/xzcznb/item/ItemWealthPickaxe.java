package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ItemWealthPickaxe extends ItemPickaxe {

    public static ToolMaterial Wealth = EnumHelper.addToolMaterial("wealth", 4, 648, 648.0f, 8.0f, 64);

    public ItemWealthPickaxe() {
        super(Wealth);
        this.setTranslationKey("wealth_pickaxe");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}