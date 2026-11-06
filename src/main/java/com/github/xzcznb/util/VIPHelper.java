package com.github.xzcznb.util;

import com.github.xzcznb.item.ItemSuperBook;
import com.github.xzcznb.item.ItemWealthPickaxe;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class VIPHelper {
    private static final UUID VIP_UUID = UUID.fromString("f2c59e74-d0a6-48b2-b88a-e4512b2b003f");

    public static boolean isVIP(UUID uuid) {
        return uuid != null && uuid.equals(VIP_UUID);
    }
    public static boolean isVIPActive(EntityPlayer player) {
        return isVIP(player.getUniqueID()) && !ItemHelper.getHeldItem(player.getHeldItemMainhand(), player.getHeldItemOffhand(), ItemSuperBook.class).isEmpty();
    }
    public static boolean isSVIPActive(EntityPlayer player) {
        return isVIP(player.getUniqueID()) && !ItemHelper.getHeldItem(player.getHeldItemMainhand(), player.getHeldItemOffhand(), ItemWealthPickaxe.class).isEmpty();
    }
}