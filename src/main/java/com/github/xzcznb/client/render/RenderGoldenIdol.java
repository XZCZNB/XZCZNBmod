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
        if (player != null && goldenIdol.getPos().distanceSq(player.posX, player.posY, player.posZ) > 64.0 * 64.0) return;
        EntityLivingBase target = goldenIdol.getFakeEntity();
        if (target == null) return;
        target.ticksExisted = 0;
        target.prevPosX = target.posX;
        target.prevPosY = target.posY;
        target.prevPosZ = target.posZ;
        target.lastTickPosX = target.posX;
        target.lastTickPosY = target.posY;
        target.lastTickPosZ = target.posZ;
        target.prevRotationYaw = target.rotationYaw;
        target.prevRotationPitch = target.rotationPitch;
        target.prevRenderYawOffset = target.renderYawOffset;
        target.prevRotationYawHead = target.rotationYawHead;
        target.prevLimbSwingAmount = target.limbSwingAmount;
        target.prevSwingProgress = target.swingProgress;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 0.85f, 0.25f, 1.0f);
        Minecraft.getMinecraft().getRenderManager().renderEntity(target, x + target.posX, y + target.posY, z + target.posZ, target.rotationYaw, 0, false);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
}