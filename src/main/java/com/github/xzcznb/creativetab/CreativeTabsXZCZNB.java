package com.github.xzcznb.creativetab;

import com.github.xzcznb.item.ItemLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.NonNullList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabsXZCZNB extends CreativeTabs
{
    public CreativeTabsXZCZNB()
    {
        super("xzcznb");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(ItemLoader.goldenHead);
    }
}