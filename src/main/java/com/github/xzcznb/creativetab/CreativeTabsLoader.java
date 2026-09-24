package com.github.xzcznb.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CreativeTabsLoader
{
    public static CreativeTabs tabXZCZNB;

    public CreativeTabsLoader(FMLPreInitializationEvent event)
    {
        tabXZCZNB = new CreativeTabsXZCZNB();
    }
}