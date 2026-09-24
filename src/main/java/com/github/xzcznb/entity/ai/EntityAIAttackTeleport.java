package com.github.xzcznb.entity.ai;

import com.github.xzcznb.SoundLoader;
import com.github.xzcznb.entity.EntityLoyalZombie;
import com.github.xzcznb.entity.EntityLoyalZombieDecoy;
import com.github.xzcznb.item.ItemMace;
import com.github.xzcznb.item.ItemSpear;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.github.xzcznb.util.ParticleHelper.spawnParticles;

public class EntityAIAttackTeleport extends EntityAIBase {

    private final EntityCreature attacker;
    private final World world;
    private EntityLivingBase target;
    private int teleportCooldown = 0;
    private static final int minInterval = 30;
    private static final int maxInterval = 60;
    private double lastGroundX = 0;
    private double lastGroundY = 0;
    private double lastGroundZ = 0;
    private boolean hasLastGroundPos = false;

    public EntityAIAttackTeleport(EntityCreature attacker) {
        this.attacker = attacker;
        this.world = attacker.world;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.attacker.posY < -5.0) return true;
        this.target = this.attacker.getAttackTarget();
        return this.target != null && this.target.isEntityAlive();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
        this.teleportCooldown = 0;
    }

    @Override
    public void updateTask() {
        if (this.attacker.posY < -5.0 && this.hasLastGroundPos) {
            this.attacker.setPositionAndUpdate(lastGroundX, lastGroundY, lastGroundZ);
            this.attacker.motionY = 0;
            this.attacker.velocityChanged = true;
            this.attacker.fallDistance = 0;
            spawnTeleportParticles(lastGroundX, lastGroundY + this.attacker.height / 2.0, lastGroundZ);
            this.world.playSound(null, lastGroundX, lastGroundY, lastGroundZ, SoundLoader.LOYAL_ZOMBIE_TP, SoundCategory.HOSTILE, 1.0f, 1.0f);
            return;
        }
        if (this.attacker.onGround) {
            BlockPos belowPos = new BlockPos(this.attacker.posX, this.attacker.posY - 0.1, this.attacker.posZ);
            IBlockState belowState = this.world.getBlockState(belowPos);
            if (belowState.getMaterial().blocksMovement() && isSafePosition(this.attacker.posX, this.attacker.posY, this.attacker.posZ)) {
                lastGroundX = this.attacker.posX;
                lastGroundY = this.attacker.posY;
                lastGroundZ = this.attacker.posZ;
                hasLastGroundPos = true;
            }
        }
        if (this.target == null || !this.target.isEntityAlive()) return;
        if (--this.teleportCooldown > 0) return;
        float distance = this.attacker.getDistance(this.target);
        if (distance > 16.0f) {
            return;
        }
        float ratio = distance / 16.0f;
        if (holding(ItemMace.class)) ratio -= 0.1f;
        else if (holding(ItemSpear.class)) ratio -= 0.2f;
        else if (holding(ItemBow.class)) ratio -= 0.4f;
        this.teleportToTarget(this.target);
        this.teleportCooldown = minInterval + (int)((maxInterval - minInterval) * ratio);
    }

    private boolean holding(Class<?> itemClass) {
        ItemStack held = this.attacker.getHeldItemMainhand();
        return !held.isEmpty() && itemClass.isInstance(held.getItem());
    }

    private void teleportToTarget(EntityLivingBase target) {
        if (target == null || !target.isEntityAlive()) return;
        double oldX = this.attacker.posX;
        double oldY = this.attacker.posY;
        double oldZ = this.attacker.posZ;
        double extra, factor;
        boolean isSummoner = this.attacker instanceof EntityLoyalZombie && !((EntityLoyalZombie) this.attacker).isDecoy();
        boolean hasBow = holding(ItemBow.class);
        boolean hasMace = holding(ItemMace.class) && isSummoner;
        boolean hasSpear = holding(ItemSpear.class);
        if (hasBow) {extra = 8.0; factor = 8.0;}
        else if (hasMace) {extra = 0; factor = 0.5;}
        else if (hasSpear) {extra = 2.0; factor = 4.0;}
        else {extra = 4.0; factor = 1.0;}
        for (int attempt = 0; attempt < 4; attempt++) {
            double distance = this.attacker.getRNG().nextDouble() * factor + extra;
            double angle = (Math.PI / 3.0 + this.attacker.getRNG().nextDouble() * Math.PI * 4.0 / 3.0) + Math.toRadians(target.rotationYawHead);
            double targetX = target.posX + Math.cos(angle) * distance;
            double targetZ = target.posZ + Math.sin(angle) * distance;
            double targetY = findGroundY(targetX, targetZ);
            if (hasMace) targetY += this.attacker.getRNG().nextDouble() * 2.0 + 2.0;
            if (targetY == Double.NEGATIVE_INFINITY) continue;
            if (isSafePosition(targetX, targetY, targetZ)) {
                if (isSummoner) {
                    EntityLoyalZombieDecoy decoy = EntityLoyalZombieDecoy.create(this.world, (EntityLoyalZombie) this.attacker);
                    decoy.setPositionAndUpdate(oldX, oldY, oldZ);
                    decoy.fallDistance = 0;
                    this.world.spawnEntity(decoy);
                }
                this.attacker.setPositionAndUpdate(targetX, targetY, targetZ);
                this.attacker.motionY = target.motionY;
                if (hasMace) {
                    this.attacker.motionY -= 0.2;
                    this.attacker.velocityChanged = true;
                    this.attacker.fallDistance += 8.0f + this.attacker.getRNG().nextFloat() * 8.0f;
                }
                this.attacker.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 80, 4));
                spawnTeleportParticles(oldX, oldY + this.attacker.height / 2.0, oldZ);
                spawnTeleportParticles(targetX, targetY + this.attacker.height / 2.0, targetZ);
                this.world.playSound(null, oldX, oldY, oldZ, SoundLoader.LOYAL_ZOMBIE_TP, SoundCategory.HOSTILE, 1.0f, 1.0f);
                return;
            }
        }
    }

    private double findGroundY(double x, double z) {
        if (this.target == null || !this.target.isEntityAlive()) return this.attacker.posY;
        if (this.target.isInWater() || this.target.isInLava()) {return this.target.posY + 0.5;}
        BlockPos pos = new BlockPos(x, this.target.posY, z);
        for (int i = 0; i < 8; i++) {
            if (pos.getY() <= 0) break;
            BlockPos below = pos.down();
            IBlockState belowState = this.world.getBlockState(below);
            if (belowState.getMaterial().blocksMovement() && this.world.isAirBlock(pos) && this.world.isAirBlock(pos.up())) {
                return pos.getY();
            }
            pos = pos.down();
        }
        if (this.target.posY > 0) return this.target.posY + 0.5;
        return this.attacker.posY;
    }

    private boolean isSafePosition(double x, double y, double z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!this.world.isBlockLoaded(pos)) return false;
        IBlockState headState = this.world.getBlockState(pos.up());
        return !headState.getMaterial().isSolid();
    }

    private void spawnTeleportParticles(double x, double y, double z) {
        spawnParticles(this.attacker, EnumParticleTypes.PORTAL, x, y, z, this.attacker.width * 4, this.attacker.height, this.attacker.width * 4.0f, 0.2, 0.1, 0.2, 20);
        spawnParticles(this.attacker, EnumParticleTypes.REDSTONE, x, y, z, 5.0, 1.5, 5.0, 0.5, 0.2, 0.8, 20);
    }
}