package com.github.xzcznb.util;

import com.github.xzcznb.network.PacketHandler;
import com.github.xzcznb.network.PacketParticle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.Random;

public class ParticleHelper {

    public static void spawnParticles(EntityLivingBase attacker, EnumParticleTypes particleType, double x, double y, double z, double dx, double dy, double dz, double vx, double vy, double vz, int count) {
        spawnParticles(attacker, particleType, x, y, z, dx, dy, dz, vx, vy, vz, count, new int[0]);
    }

    public static void spawnParticles(EntityLivingBase attacker, EnumParticleTypes particleType,
                                      double x, double y, double z, double dx, double dy, double dz,
                                      double vx, double vy, double vz, int count, int... extra) {
        Random rand = attacker.world.rand;
        double[][] positions = new double[count][3];
        for (int i = 0; i < count; i++) {
            positions[i][0] = x + (rand.nextDouble() - 0.5) * dx;
            positions[i][1] = y + (rand.nextDouble() - 0.5) * dy;
            positions[i][2] = z + (rand.nextDouble() - 0.5) * dz;
        }
        boolean randomize = particleType != EnumParticleTypes.REDSTONE
                && particleType != EnumParticleTypes.SPELL
                && particleType != EnumParticleTypes.SPELL_MOB;
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketParticle(particleType, positions, vx, vy, vz, randomize, extra),
                new NetworkRegistry.TargetPoint(attacker.dimension, x, y, z, 64.0)
        );
    }

    public static void spawnArcParticles(EntityLivingBase attacker, EnumParticleTypes type,
                                         double radius, int count, double yOffset, double angleRange,
                                         double vx, double vy, double vz) {
        double rad = Math.toRadians(attacker.rotationYaw);
        double[][] positions = new double[count][3];
        for (int i = 0; i < count; i++) {
            double theta = rad - angleRange * 0.5 + angleRange * i / count;
            positions[i][0] = attacker.posX - Math.sin(theta) * radius;
            positions[i][1] = attacker.posY + yOffset;
            positions[i][2] = attacker.posZ + Math.cos(theta) * radius;
        }
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketParticle(type, positions, vx, vy, vz, false),
                new NetworkRegistry.TargetPoint(attacker.dimension, attacker.posX, attacker.posY, attacker.posZ, 64)
        );
    }
}