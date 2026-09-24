package com.github.xzcznb.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;

public class AttackBoundingBoxHelper {

    public static AxisAlignedBB getAttackBB1(EntityLivingBase entity, double length, double width, double height, double offset) {
        float yaw = entity.rotationYaw;
        float pitch = entity.rotationPitch;
        double sinYaw = -Math.sin(Math.toRadians(yaw));
        double cosYaw = Math.cos(Math.toRadians(yaw));
        double sinPitch = -Math.sin(Math.toRadians(pitch));
        double cosPitch = Math.cos(Math.toRadians(pitch));
        double dirX = sinYaw * cosPitch;
        double dirY = sinPitch;
        double dirZ = cosYaw * cosPitch;
        double cx = entity.posX - dirX * offset;
        double cy = entity.posY + entity.getEyeHeight();
        double cz = entity.posZ - dirZ * offset;
        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;
        double endX = cx + dirX * length;
        double endY = cy + dirY * length;
        double endZ = cz + dirZ * length;
        double minX = Math.min(cx, endX) - halfWidth;
        double maxX = Math.max(cx, endX) + halfWidth;
        double minY = Math.min(cy, endY) - halfHeight;
        double maxY = Math.max(cy, endY) + halfHeight;
        double minZ = Math.min(cz, endZ) - halfWidth;
        double maxZ = Math.max(cz, endZ) + halfWidth;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static AxisAlignedBB getAttackBB1(EntityLivingBase entity, double length, double width, double height) {
        return getAttackBB1(entity, length, width, height, 0);
    }
}