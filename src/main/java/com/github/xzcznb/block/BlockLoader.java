package com.github.xzcznb.block;

import com.github.xzcznb.XZCZNB;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = XZCZNB.MODID)
public class BlockLoader
{
    public static Block mine = new BlockMine();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(mine.setRegistryName(XZCZNB.MODID, "mine"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event)
    {
        registerRender(mine);
    }

    @SideOnly(Side.CLIENT)
    private static void registerRender(Block block)
    {
        net.minecraft.util.ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) return;
        ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model);
    }
}