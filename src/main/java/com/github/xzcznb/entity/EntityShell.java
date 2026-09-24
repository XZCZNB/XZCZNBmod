package com.github.xzcznb.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityShell extends EntitySnowball {

    public EntityShell(World world) {
        super(world);
    }
    public EntityShell(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }
    public EntityShell(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            this.world.createExplosion(this.getThrower(), this.posX, this.posY, this.posZ, 4.5f, false);
            this.setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.01f;
    }
}