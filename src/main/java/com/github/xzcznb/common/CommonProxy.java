package com.github.xzcznb.common;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.XZCZNB;
import com.github.xzcznb.crafting.CraftingLoader;
import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityLoader;
import com.github.xzcznb.entity.EntityRoyalGuard;
import com.github.xzcznb.event.*;
import com.github.xzcznb.inventory.ShopGuiHandler;
import com.github.xzcznb.network.PacketHandler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        new AnvilEnchantHandler();
        new ArmorSwapHandler();
        new BlockEventHandler();
        new CombatEventHandler();
        new CreativeTabsLoader(event);
        new EntityLoader();
        new PotionEventHandler();
        MinecraftForge.EVENT_BUS.register(SoundLoader.class);
    }

    public void init(FMLInitializationEvent event) {
        new CraftingLoader();
        PacketHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(XZCZNB.instance, new ShopGuiHandler());
        EntityLoader.registerSpawns();
    }

    public void postInit(FMLPostInitializationEvent event) {}
}