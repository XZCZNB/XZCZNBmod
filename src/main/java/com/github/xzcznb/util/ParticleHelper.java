package com.github.xzcznb.util;

import com.github.xzcznb.network.PacketHandler;
import com.github.xzcznb.network.PacketParticle;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.Random;

public class ParticleHelper {

    public static void spawnParticles(Entity entity, EnumParticleTypes particleType, double x, double y, double z, double dx, double dy, double dz, double vx, double vy, double vz, int count) {
        spawnParticles(entity, particleType, x, y, z, dx, dy, dz, vx, vy, vz, count, new int[0]);
    }

    public static void spawnParticles(Entity entity, EnumParticleTypes particleType, double x, double y, double z, double dx, double dy, double dz, double vx, double vy, double vz, int count, int... extra) {
        Random rand = entity.world.rand;
        for (int i = 0; i < count; i++) {
            double px = x + (rand.nextDouble() - 0.5) * dx;
            double py = y + (rand.nextDouble() - 0.5) * dy;
            double pz = z + (rand.nextDouble() - 0.5) * dz;
            double speedX = vx, speedY = vy, speedZ = vz;
            if (particleType != EnumParticleTypes.REDSTONE && particleType != EnumParticleTypes.SPELL && particleType != EnumParticleTypes.SPELL_MOB) {
                speedX = (rand.nextDouble() - 0.5) * vx;
                if (vy < 0) {
                    speedY = rand.nextDouble() * -vy;
                } else {
                    speedY = (rand.nextDouble() - 0.5) * vy;
                }
                speedZ = (rand.nextDouble() - 0.5) * vz;
            }
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketParticle(particleType, px, py, pz, speedX, speedY, speedZ, 1, extra),
                    new NetworkRegistry.TargetPoint(entity.dimension, x, y, z, 64.0)
            );
        }
    }
}