package com.github.xzcznb.client.render;

import com.github.xzcznb.entity.EntityShell;
import com.github.xzcznb.item.ItemLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderShell extends RenderSnowball<EntityShell> {
    public RenderShell(RenderManager renderManager) {
        super(renderManager, ItemLoader.shell, Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public void doRender(EntityShell entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Random rand = entity.world.rand;
        for (int i = 0; i < 2; i++) {
            double px = entity.posX + (rand.nextDouble() - 0.5) * 0.5;
            double py = entity.posY + (rand.nextDouble() - 0.5) * 0.2;
            double pz = entity.posZ + (rand.nextDouble() - 0.5) * 0.5;
            double vx = -entity.motionX * 0.2 + (rand.nextDouble() - 0.5) * 0.1;
            double vy = -entity.motionY * 0.2 + (rand.nextDouble() - 0.5) * 0.1;
            double vz = -entity.motionZ * 0.2 + (rand.nextDouble() - 0.5) * 0.1;
            entity.world.spawnParticle(EnumParticleTypes.FLAME, px, py, pz, vx, vy, vz);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}