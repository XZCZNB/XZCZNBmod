package com.github.xzcznb.client.render;

import com.github.xzcznb.tile.TileEntityGoldenIdol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class RenderGoldenIdol extends TileEntitySpecialRenderer<TileEntityGoldenIdol> {

    @Override
    public void render(TileEntityGoldenIdol goldenIdol, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && goldenIdol.getPos().distanceSq(player.posX, player.posY, player.posZ) > 64 * 64) return;
        EntityLivingBase target = goldenIdol.getFakeEntity();
        if (target == null) return;
        target.posX = 0;
        target.posY = 0;
        target.posZ = 0;
        target.prevPosX = 0;
        target.prevPosY = 0;
        target.prevPosZ = 0;
        target.lastTickPosX = 0;
        target.lastTickPosY = 0;
        target.lastTickPosZ = 0;
        target.prevRotationYaw = target.rotationYaw;
        target.prevRotationPitch = target.rotationPitch;
        target.prevRenderYawOffset = target.renderYawOffset;
        target.prevRotationYawHead = target.rotationYawHead;
        target.prevLimbSwingAmount = target.limbSwingAmount;
        target.prevSwingProgress = target.swingProgress;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.color(1.0f, 0.85f, 0.25f, 1.0f);
        Minecraft.getMinecraft().getRenderManager().renderEntity(target, 0, 0, 0, 0, partialTicks, false);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}