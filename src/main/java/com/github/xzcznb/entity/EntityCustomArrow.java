package com.github.xzcznb.entity;

import com.github.xzcznb.util.ArrowTrajectoryHelper;
import com.github.xzcznb.util.PotionHelper;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityCustomArrow extends EntityTippedArrow {
    public EntityCustomArrow(World worldIn) {super(worldIn);}
    public EntityCustomArrow(World worldIn, EntityCustomArrow base) {
        super(worldIn);
        if (this.world.isRemote) return;
        this.shootingEntity = base.shootingEntity;
        this.pickupStatus = base.pickupStatus;
        ArrowTrajectoryHelper.shootScatter(this, base, this.rand);
        if (this.rand.nextFloat() < 0.2f) this.applyRandomPotionEffect();
    }

    public EntityCustomArrow(World worldIn, EntityLivingBase attacker, EntityLivingBase target, float velocity, float inaccuracy) {
        super(worldIn);
        if (this.world.isRemote) return;
        this.shootingEntity = attacker;
        if (attacker == null || target == null) {
            this.setDead();
            return;
        }
        this.pickupStatus = PickupStatus.CREATIVE_ONLY;
        ArrowTrajectoryHelper.shootAtTarget(this, attacker, target, velocity, inaccuracy);
        if (this.rand.nextFloat() < 0.5f) this.applyRandomPotionEffect();
    }

    private void applyRandomPotionEffect() {
        Potion id = PotionHelper.randomPotion(PotionHelper.ARROW_DEBUFF, this.rand);
        int duration = 20 + this.rand.nextInt(64);
        int amplifier = this.rand.nextInt(4);
        this.addEffect(new PotionEffect(id, duration, amplifier));
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
