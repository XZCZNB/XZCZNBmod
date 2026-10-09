package com.github.xzcznb.block;

import com.github.xzcznb.XZCZNB;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
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
    public static Block wealth = new BlockWealth();

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(mine.setRegistryName(XZCZNB.MODID, "mine"));
        event.getRegistry().register(wealth.setRegistryName(XZCZNB.MODID, "wealth"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(mine).setRegistryName(XZCZNB.MODID, "mine"));
        event.getRegistry().register(new ItemBlockWealth(wealth).setRegistryName(XZCZNB.MODID, "wealth"));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event)
    {
        registerRender(mine);
        registerRender(wealth);
    }

    @SideOnly(Side.CLIENT)
    private static void registerRender(Block block)
    {
        ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) return;
        ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model);
    }
}