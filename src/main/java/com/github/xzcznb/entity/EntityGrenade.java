package com.github.xzcznb.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGrenade extends EntitySnowball {

    public EntityGrenade(World world) {
        super(world);
    }
    public EntityGrenade(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }
    public EntityGrenade(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05f;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            this.world.createExplosion(this.getThrower(), this.posX, this.posY, this.posZ, 3.0f, false);
            this.setDead();
        }
    }
}