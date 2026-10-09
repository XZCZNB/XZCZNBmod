package com.github.xzcznb.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorSwapHandler {
    public ArmorSwapHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.world.isRemote) return;
        if (event.getHand() != EnumHand.MAIN_HAND) return;
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) return;
        EntityEquipmentSlot slot = null;
        if (held.getItem() instanceof ItemArmor) {
            slot = ((ItemArmor) held.getItem()).armorType;
        } else if (held.getItem() instanceof ItemElytra) {
            slot = EntityEquipmentSlot.CHEST;
        } else {
            EntityEquipmentSlot otherSlot = held.getItem().getEquipmentSlot(held);
            if (otherSlot != null && otherSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                slot = otherSlot;
            }
        }
        if (slot == null) return;
        ItemStack current = player.getItemStackFromSlot(slot);
        player.setItemStackToSlot(slot, held.copy());
        player.setHeldItem(EnumHand.MAIN_HAND, current.copy());
        player.inventory.markDirty();
        player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.PLAYERS, 1.0f, 1.0f);
        event.setCanceled(true);
    }
}