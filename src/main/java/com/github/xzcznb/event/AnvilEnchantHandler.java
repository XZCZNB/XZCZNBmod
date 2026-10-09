package com.github.xzcznb.event;

import com.github.xzcznb.item.ItemLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnvilEnchantHandler {
    public AnvilEnchantHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.isEmpty() || right.isEmpty()) return;
        if (right.getItem() != ItemLoader.shop) return;
        ItemStack result = left.copy();
        NBTTagCompound tag = result.getTagCompound();
        int repairCost = tag == null ? 0 : tag.getInteger("RepairCost");
        if (repairCost < 8) return;
        result.getTagCompound().setInteger("RepairCost", 0);
        event.setOutput(result);
        event.setCost(1);
        event.setMaterialCost(1);
    }
}
