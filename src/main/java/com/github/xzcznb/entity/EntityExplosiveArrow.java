package com.github.xzcznb.entity;

import com.github.xzcznb.util.ArrowTrajectoryHelper;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityExplosiveArrow extends EntityTippedArrow {
    public EntityExplosiveArrow(World worldIn) { super(worldIn); }
    public EntityExplosiveArrow(World worldIn, EntityTippedArrow base) {
        super(worldIn);
        if (this.world.isRemote) return;
        this.shootingEntity = base.shootingEntity;
        this.pickupStatus = base.pickupStatus;
        this.setDamage(base.getDamage() * 0.75);
        ArrowTrajectoryHelper.shootScatter(this, base, this.rand);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.entityHit != null) {
                this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, 1.0f, false, false);
            }
        }
        super.onHit(result);
    }

    @Override
    protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity hit = super.findEntityOnPath(start, end);
        if (hit instanceof EntityLivingBase && this.shootingEntity instanceof EntityLivingBase) {
            EntityLivingBase shooter = (EntityLivingBase) this.shootingEntity;
            if (TeamHelper.isAlly(shooter, (EntityLivingBase) hit)) return null;
        }
        return hit;
    }
}
