package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.item.Item;

public class ItemShell extends Item {

    public ItemShell()
    {
        this.setTranslationKey("xzcznb_shell");
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }
}