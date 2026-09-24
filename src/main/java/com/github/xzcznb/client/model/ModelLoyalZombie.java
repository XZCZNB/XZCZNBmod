package com.github.xzcznb.client.model;

import com.github.xzcznb.entity.EntityLoyalZombie;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLoyalZombie extends ModelPlayer {

    public ModelLoyalZombie() {
        this(0, false);
    }

    public ModelLoyalZombie(float modelSize, boolean smallArms) {
        super(modelSize, smallArms);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (entityIn instanceof EntityLoyalZombie) {
            EntityLoyalZombie zombie = (EntityLoyalZombie) entityIn;
            if (zombie.isSwingingArms()) {
                this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            } else {
                this.rightArmPose = ModelBiped.ArmPose.EMPTY;
                this.leftArmPose = ModelBiped.ArmPose.EMPTY;
            }
        }
        if (entityIn instanceof EntityLivingBase) {
            this.swingProgress = ((EntityLivingBase) entityIn).getSwingProgress(scaleFactor);
        }
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        if (entityIn instanceof EntityLoyalZombie) {
            EntityLoyalZombie zombie = (EntityLoyalZombie) entityIn;
            int ticks = zombie.getSwingTicks();
            if (ticks > 0) {
                float progress = 1.0f - ticks / 6.0f;
                this.bipedRightArm.rotateAngleX = -MathHelper.sin(progress * (float) Math.PI) * 1.5f;
            }
            if (zombie.isUsingSpear()) {
                float pitch = zombie.getSpearAimPitch();
                this.bipedRightArm.rotateAngleX = (float) Math.toRadians(-90.0 + pitch);
            }
        }
    }
}