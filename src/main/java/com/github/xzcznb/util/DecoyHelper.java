package com.github.xzcznb.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;

public class DecoyHelper {

    private static final String TAG = "DecoyLifeTimer";

    public static void setLifeTimer(EntityLivingBase entity, int ticks) {
        entity.getEntityData().setInteger(TAG, ticks);
    }

    public static int getLifeTimer(EntityLivingBase entity) {
        return entity.getEntityData().getInteger(TAG);
    }

    public static void tick(EntityLivingBase entity) {
        if (entity.world.isRemote) return;
        NBTTagCompound data = entity.getEntityData();
        int timer = data.getInteger(TAG);
        if (timer <= 0) return;
        timer--;
        data.setInteger(TAG, timer);
        if (timer <= 0) {
            vanish(entity);
        }
    }

    public static void vanish(EntityLivingBase entity) {
        if (entity == null || entity.isDead) return;
        entity.getEntityData().removeTag(TAG);
        ParticleHelper.spawnParticles(entity, EnumParticleTypes.SMOKE_LARGE,
                entity.posX, entity.posY + entity.height / 2.0f, entity.posZ,
                1.0, 1.0, 1.0, 0.2, 0.2, 0.2, 20);
        ParticleHelper.spawnParticles(entity, EnumParticleTypes.SPELL_INSTANT,
                entity.posX, entity.posY + entity.height / 2.0f, entity.posZ,
                1.5, 1.5, 1.5, 0.4, 0.4, 0.4, 20);
        ParticleHelper.spawnParticles(entity, EnumParticleTypes.SPELL_WITCH,
                entity.posX, entity.posY + entity.height / 2.0f, entity.posZ,
                2.0, 2.0, 2.0, 0.6, 0.6, 0.6, 20);
        entity.world.removeEntity(entity);
    }
}