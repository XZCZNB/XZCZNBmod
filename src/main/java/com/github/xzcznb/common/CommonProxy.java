package com.github.xzcznb.common;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.crafting.CraftingLoader;
import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityLoader;
import com.github.xzcznb.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        new CreativeTabsLoader(event);
        new EventLoader();
        new EntityLoader();
        MinecraftForge.EVENT_BUS.register(SoundLoader.class);
    }

    public void init(FMLInitializationEvent event)
    {
        new CraftingLoader();
        PacketHandler.init();
    }

    public void postInit(FMLPostInitializationEvent event)
    {

    }
}