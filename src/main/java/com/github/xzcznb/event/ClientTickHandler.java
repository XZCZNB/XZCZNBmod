package com.github.xzcznb.event;

import com.github.xzcznb.item.ItemSpear;
import com.github.xzcznb.network.PacketHandler;
import com.github.xzcznb.network.PacketSpearAction;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTickHandler {
    private int attackCooldown = 0;
    private int moveCooldown = 0;

    public ClientTickHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        if (attackCooldown > 0) attackCooldown--;
        if (moveCooldown > 0) moveCooldown--;
        ItemStack held = mc.player.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemSpear.class)) return;
        if (mc.gameSettings.keyBindUseItem.isKeyDown() && attackCooldown <= 0) {
            PacketHandler.INSTANCE.sendToServer(new PacketSpearAction(true));
            attackCooldown = 8;
        }
        while (mc.gameSettings.keyBindAttack.isPressed()) {
            if (moveCooldown <= 0) {
                PacketHandler.INSTANCE.sendToServer(new PacketSpearAction(false));
                moveCooldown = 4;
            }
        }
    }
}