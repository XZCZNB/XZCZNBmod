package com.github.xzcznb.entity;

import com.github.xzcznb.XZCZNB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntityLoader {
    public EntityLoader() {
        registerEntity(EntityGrenade.class, "grenade", 0, 64, 1, true);
        registerEntity(EntityShell.class, "shell", 1, 64, 1, true);
        registerEntity(EntityCustomArrow.class, "custom_arrow", 2, 64, 1, true);
        registerEntity(EntityExplosiveArrow.class, "explosive_arrow", 3, 64, 1, true);
        registerEntity(EntityRoyalGuard.class, "royal_guard", 4, 64, 1, true);
        registerEntity(EntityRoyalGuardDecoy.class, "royal_guard_decoy", 5, 64, 1, true);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(new ResourceLocation(XZCZNB.MODID, name), entityClass, name, id,
                XZCZNB.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    public static void registerSpawns() {
        EntityRegistry.addSpawn(EntityRoyalGuard.class, 1, 2, 4,
                EnumCreatureType.CREATURE,
                ForgeRegistries.BIOMES.getValuesCollection().toArray(new Biome[0]));
    }
}