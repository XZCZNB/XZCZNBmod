package com.github.xzcznb.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Random;

public class PacketParticle implements IMessage {

    private EnumParticleTypes particleType;
    private boolean randomSpeed;
    private double[][] positions;
    private double vx, vy, vz;
    private int[] extra;

    public PacketParticle() {}

    public PacketParticle(EnumParticleTypes particleType, double[][] positions,
                          double vx, double vy, double vz, boolean randomizeSpeed, int... extra) {
        this.particleType = particleType;
        this.positions = positions;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.randomSpeed = randomizeSpeed;
        this.extra = extra;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.particleType = EnumParticleTypes.values()[buf.readInt()];
        this.randomSpeed = buf.readBoolean();
        this.vx = buf.readDouble();
        this.vy = buf.readDouble();
        this.vz = buf.readDouble();
        int count = buf.readInt();
        this.positions = new double[count][3];
        for (int i = 0; i < count; i++) {
            this.positions[i][0] = buf.readDouble();
            this.positions[i][1] = buf.readDouble();
            this.positions[i][2] = buf.readDouble();
        }
        int len = buf.readInt();
        this.extra = new int[len];
        for (int i = 0; i < len; i++) {
            this.extra[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(particleType.ordinal());
        buf.writeBoolean(randomSpeed);
        buf.writeDouble(vx);
        buf.writeDouble(vy);
        buf.writeDouble(vz);
        buf.writeInt(positions.length);
        for (double[] pos : positions) {
            buf.writeDouble(pos[0]);
            buf.writeDouble(pos[1]);
            buf.writeDouble(pos[2]);
        }
        buf.writeInt(extra == null ? 0 : extra.length);
        if (extra != null) {
            for (int i : extra) {
                buf.writeInt(i);
            }
        }
    }

    public boolean isRandomSpeed() { return randomSpeed; }
    public EnumParticleTypes getParticleType() { return particleType; }
    public double[][] getPositions() { return positions; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getVz() { return vz; }
    public int[] getExtra() { return extra; }

    public static class Handler implements IMessageHandler<PacketParticle, IMessage> {
        @Override
        public IMessage onMessage(final PacketParticle message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                if (world == null) return;
                EnumParticleTypes type = message.getParticleType();
                double[][] positions = message.getPositions();
                Random rand = world.rand;
                for (double[] pos : positions) {
                    double speedX = message.getVx();
                    double speedY = message.getVy();
                    double speedZ = message.getVz();
                    if (message.isRandomSpeed()) {
                        speedX = (rand.nextDouble() - 0.5) * message.getVx();
                        if (message.getVy() < 0) {
                            speedY = rand.nextDouble() * -message.getVy();
                        } else {
                            speedY = (rand.nextDouble() - 0.5) * message.getVy();
                        }
                        speedZ = (rand.nextDouble() - 0.5) * message.getVz();
                    }
                    world.spawnParticle(type, pos[0], pos[1], pos[2], speedX, speedY, speedZ, message.getExtra());
                }
            });
            return null;
        }
    }
}