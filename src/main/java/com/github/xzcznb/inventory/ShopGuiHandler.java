package com.github.xzcznb.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ShopGuiHandler implements IGuiHandler {

    public static final int GUI_ID_SHOP = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ID_SHOP) {
            return new ShopContainer(player.inventory, player, x);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ID_SHOP) {
            return new ShopGui(new ShopContainer(player.inventory, player, x));
        }
        return null;
    }
}