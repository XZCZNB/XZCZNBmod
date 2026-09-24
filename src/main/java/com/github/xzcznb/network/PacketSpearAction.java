package com.github.xzcznb.network;

import com.github.xzcznb.item.ItemSpear;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSpearAction implements IMessage {
    private boolean isAttack;

    public PacketSpearAction() {}
    public PacketSpearAction(boolean isAttack) { this.isAttack = isAttack; }

    @Override
    public void fromBytes(ByteBuf buf) { this.isAttack = buf.readBoolean(); }
    @Override
    public void toBytes(ByteBuf buf) { buf.writeBoolean(isAttack); }

    public static class Handler implements IMessageHandler<PacketSpearAction, IMessage> {
        @Override
        public IMessage onMessage(PacketSpearAction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() instanceof ItemSpear) {
                    ItemSpear spear = (ItemSpear) stack.getItem();
                    if (message.isAttack) {
                        spear.doChargeAttack(player, stack, EntityVillager.class);
                    } else {
                        spear.doChargeMove(player, stack);
                    }
                }
            });
            return null;
        }
    }
}
