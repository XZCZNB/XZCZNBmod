package com.github.xzcznb.entity;

import com.github.xzcznb.XZCZNB;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityLoader
{
    public EntityLoader()
    {
        registerEntity(EntityGrenade.class, "grenade", 0, 64, 1, true);
        registerEntity(EntityShell.class, "shell", 1, 64, 1, true);
        registerEntity(EntityLoyalZombie.class, "loyalzombie", 2, 64, 1, true);
        registerEntity(EntityLoyalZombieDecoy.class, "loyalzombie_decoy", 3, 64, 1, true);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int id,
                                       int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        EntityRegistry.registerModEntity(new ResourceLocation(XZCZNB.MODID, name), entityClass, name, id,
                XZCZNB.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
    }
}