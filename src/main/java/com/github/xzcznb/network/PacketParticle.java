package com.github.xzcznb.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketParticle implements IMessage {
    private EnumParticleTypes particleType;
    private double x, y, z;
    private double vx, vy, vz;
    private int count;
    private int[] extra;

    public PacketParticle() {}

    public PacketParticle(EnumParticleTypes particleType, double x, double y, double z,
                          double vx, double vy, double vz, int count, int... extra) {
        this.particleType = particleType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.count = count;
        this.extra = extra;
    }

    public PacketParticle(EnumParticleTypes particleType, double x, double y, double z,
                          double vx, double vy, double vz, int count) {
        this.particleType = particleType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.count = count;
        this.extra = new int[0];
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.particleType = EnumParticleTypes.values()[buf.readInt()];
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.vx = buf.readDouble();
        this.vy = buf.readDouble();
        this.vz = buf.readDouble();
        this.count = buf.readInt();
        int len = buf.readInt();
        this.extra = new int[len];
        for (int i = 0; i < len; i++) {
            this.extra[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(particleType.ordinal());
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(vx);
        buf.writeDouble(vy);
        buf.writeDouble(vz);
        buf.writeInt(count);
        buf.writeInt(extra == null ? 0 : extra.length);
        if (extra != null) {
            for (int i : extra) {
                buf.writeInt(i);
            }
        }
    }

    public EnumParticleTypes getParticleType() { return particleType; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getVz() { return vz; }
    public int getCount() { return count; }
    public int[] getExtra() { return extra; }

    public static class Handler implements IMessageHandler<PacketParticle, IMessage> {
        @Override
        public IMessage onMessage(final PacketParticle message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) return;
                EnumParticleTypes type = message.getParticleType();
                world.spawnParticle(
                        type,
                        message.getX(),
                        message.getY(),
                        message.getZ(),
                        message.getVx(),
                        message.getVy(),
                        message.getVz(),
                        message.getExtra()
                );
            });
            return null;
        }
    }
}