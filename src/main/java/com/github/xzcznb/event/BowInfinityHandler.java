package com.github.xzcznb.event;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class BowInfinityHandler {

    public BowInfinityHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        ItemStack bow = event.getBow();
        if (bow.isEmpty()) return;
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) <= 0) return;
        event.getEntityPlayer().setActiveHand(event.getHand());
        event.setAction(new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.SUCCESS, bow));
    }
}