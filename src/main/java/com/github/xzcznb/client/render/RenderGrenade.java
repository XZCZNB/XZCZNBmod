package com.github.xzcznb.client.render;

import com.github.xzcznb.entity.EntityGrenade;
import com.github.xzcznb.item.ItemLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGrenade extends RenderSnowball<EntityGrenade> {
    public RenderGrenade(RenderManager renderManager) {
        super(renderManager, ItemLoader.grenade, Minecraft.getMinecraft().getRenderItem());
    }
}