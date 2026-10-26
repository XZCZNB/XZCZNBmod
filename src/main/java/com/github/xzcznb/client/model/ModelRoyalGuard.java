package com.github.xzcznb.client.model;

import com.github.xzcznb.entity.EntityRoyalGuard;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRoyalGuard extends ModelPlayer {

    public ModelRoyalGuard() {
        this(0, false);
    }

    public ModelRoyalGuard(float modelSize, boolean smallArms) {
        super(modelSize, smallArms);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        if (entity instanceof EntityRoyalGuard) {
            EntityRoyalGuard guard = (EntityRoyalGuard) entity;
            if (guard.isSwingingArms() && guard.getSwingTicks() <= 0) {
                this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            } else {
                this.rightArmPose = ModelBiped.ArmPose.EMPTY;
                this.leftArmPose = ModelBiped.ArmPose.EMPTY;
            }
            this.swingProgress = guard.getSwingProgress(scaleFactor);
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
            if (guard.getCastingTicks() > 0) {
                float raise = (float) Math.toRadians(-150);
                float angle = (float) Math.toRadians(30);
                float waveX = (float) Math.sin(ageInTicks * 0.25f) * angle * 0.5f;
                float waveZ = (float) Math.cos(ageInTicks * 0.125f) * angle * 0.25f;
                this.bipedRightArm.rotateAngleX = raise + waveX;
                this.bipedLeftArm.rotateAngleX = raise - waveX;
                this.bipedRightArm.rotateAngleZ = -angle + waveZ;
                this.bipedLeftArm.rotateAngleZ = angle - waveZ;
                this.bipedRightArm.rotateAngleY = 0;
                this.bipedLeftArm.rotateAngleY = 0;
            } else if (guard.isUsingSpear()) {
                float pitch = guard.getSpearAimPitch();
                this.bipedRightArm.rotateAngleX = (float) Math.toRadians(-90.0 + pitch);
            } else if (guard.getSwingTicks() > 0) {
                int ticks = guard.getSwingTicks();
                float progress = 1.0f - ticks / 6.0f;
                this.bipedRightArm.rotateAngleX = -MathHelper.sin(progress * (float) Math.PI) * 1.5f;
            }
        } else {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks,
                    netHeadYaw, headPitch, scaleFactor, entity);
        }
    }
}