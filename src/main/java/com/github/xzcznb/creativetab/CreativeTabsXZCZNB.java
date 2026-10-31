package com.github.xzcznb.creativetab;

import com.github.xzcznb.item.ItemLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabsXZCZNB extends CreativeTabs {
    private static ItemStack icon;

    public CreativeTabsXZCZNB()
    {
        super("xzcznb");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        if (icon == null) {
            icon = new ItemStack(ItemLoader.goldenHead);
        }
        return icon;
    }
}