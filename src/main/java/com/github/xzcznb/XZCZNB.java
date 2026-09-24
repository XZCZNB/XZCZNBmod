package com.github.xzcznb;

import com.github.xzcznb.common.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = XZCZNB.MODID, name = XZCZNB.NAME, version = XZCZNB.VERSION,
        acceptedMinecraftVersions = "[1.12.2]")
public class XZCZNB
{
    public static final String MODID = "xzcznb";
    public static final String NAME = "XZCZNB";
    public static final String VERSION = "1.12.2";

    @Instance(XZCZNB.MODID)
    public static XZCZNB instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    @SidedProxy(clientSide = "com.github.xzcznb.client.ClientProxy",
            serverSide = "com.github.xzcznb.common.CommonProxy")
    public static CommonProxy proxy;
}