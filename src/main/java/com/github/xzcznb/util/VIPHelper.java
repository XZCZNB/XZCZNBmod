package com.github.xzcznb.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import java.util.UUID;

public class VIPHelper {
    private static final UUID VIP_UUID = UUID.fromString("f2c59e74-d0a6-48b2-b88a-e4512b2b003f");

    public static boolean isVIP(EntityPlayer player) {
        return player != null && player.getUniqueID().equals(VIP_UUID);
    }
    public static boolean isVIPActive(EntityPlayer player, Class<? extends Item> itemClass) {return isVIP(player) && !ItemHelper.getHeldItem(player.getHeldItemMainhand(), player.getHeldItemOffhand(), itemClass).isEmpty();}
}