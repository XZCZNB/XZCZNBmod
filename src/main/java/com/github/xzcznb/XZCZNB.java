package com.github.xzcznb;

import com.github.xzcznb.command.CommandReloadDIYMobs;
import com.github.xzcznb.common.CommonProxy;

import com.github.xzcznb.util.DIYMobsHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

@Mod(modid = XZCZNB.MODID, name = XZCZNB.NAME, version = XZCZNB.VERSION, acceptedMinecraftVersions = "[1.12.2]")
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
        File DIYMobsConfig = new File(event.getModConfigurationDirectory(), "xzcznb/DIYMobs.cfg");
        DIYMobsHelper.load(DIYMobsConfig);
        MinecraftForge.EVENT_BUS.register(new DIYMobsHelper());
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

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandReloadDIYMobs());
    }

    @SidedProxy(clientSide = "com.github.xzcznb.client.ClientProxy",
            serverSide = "com.github.xzcznb.common.CommonProxy")
    public static CommonProxy proxy;
}