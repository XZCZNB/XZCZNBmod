package com.github.xzcznb.common;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.XZCZNB;
import com.github.xzcznb.crafting.CraftingLoader;
import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityLoader;
import com.github.xzcznb.event.CombatEventHandler;
import com.github.xzcznb.event.PotionEventHandler;
import com.github.xzcznb.inventory.ShopGuiHandler;
import com.github.xzcznb.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        new CreativeTabsLoader(event);
        new EntityLoader();
        new CombatEventHandler();
        new PotionEventHandler();
        MinecraftForge.EVENT_BUS.register(SoundLoader.class);
    }

    public void init(FMLInitializationEvent event)
    {
        new CraftingLoader();
        PacketHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(XZCZNB.instance, new ShopGuiHandler());
    }

    public void postInit(FMLPostInitializationEvent event)
    {}
}