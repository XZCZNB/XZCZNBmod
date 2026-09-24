package com.github.xzcznb.entity;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import static com.github.xzcznb.util.ParticleHelper.spawnParticles;

public class EntityLoyalZombieDecoy extends EntityLoyalZombie {

    private EntityLoyalZombie summoner;
    private int lifeTimer;

    public EntityLoyalZombieDecoy(World world) {
        super(world);
        this.isDecoy = true;
        this.summoner = null;
        this.experienceValue = 0;
        this.lifeTimer = 64 + this.getRNG().nextInt(256);
    }

    private EntityLoyalZombieDecoy(World world, EntityLoyalZombie summoner) {
        this(world);
        this.summoner = summoner;
    }

    private void vanish() {
        if (this.isDead) return;
        spawnParticles(this, EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + this.height / 2.0f, this.posZ, 1.0, 1.0, 1.0, 0.2, 0.2, 0.2, 20);
        spawnParticles(this, EnumParticleTypes.SPELL_INSTANT, this.posX, this.posY + this.height / 2.0f, this.posZ, 1.5, 1.5, 1.5, 0.3, 0.3, 0.3, 20);
        spawnParticles(this, EnumParticleTypes.SPELL_WITCH, this.posX, this.posY + this.height / 2.0f, this.posZ, 2.0, 2.0, 2.0, 0.4, 0.4, 0.4, 20);
        this.world.removeEntity(this);
    }

    @Override
    public void onKill() {
        if (this.summoner != null && this.summoner.isEntityAlive()) {
            this.summoner.onKill();
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.world.isRemote) return;
        EntityLivingBase target = this.getAttackTarget();
        if (target != null) {
            if (!target.isEntityAlive()) {
                this.setAttackTarget(null);
            }
        }
        if (--this.lifeTimer <= 0 || this.summoner != null && !this.summoner.isEntityAlive()) {
            vanish();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage() ||
                source == DamageSource.FALL ||
                source == DamageSource.DROWN ||
                source == DamageSource.CACTUS ||
                source == DamageSource.IN_WALL ||
                source == DamageSource.ANVIL ||
                source == DamageSource.FALLING_BLOCK ||
                source == DamageSource.STARVE ||
                source == DamageSource.LIGHTNING_BOLT ||
                source == DamageSource.WITHER ||
                source == DamageSource.DRAGON_BREATH ||
                source == DamageSource.CRAMMING ||
                source == DamageSource.FLY_INTO_WALL
        ) {
            return false;
        }
        if (!this.world.isRemote) {
            vanish();
        }
        return true;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return false;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
    }

    @Override
    protected void refillTotem() {}

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity) {}

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    public static EntityLoyalZombieDecoy create(World world, EntityLoyalZombie owner) {
        EntityLoyalZombieDecoy decoy = new EntityLoyalZombieDecoy(world, owner);
        decoy.setTamed(true);
        if (owner.isChild()) decoy.setGrowingAge(-24000);
        if (owner.isTamed() && owner.getOwnerId() != null) {
            decoy.setOwnerId(owner.getOwnerId());
        }
        decoy.setAttackTarget(owner.getAttackTarget());
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = owner.getItemStackFromSlot(slot);
            if (!stack.isEmpty()) {
                decoy.setItemStackToSlot(slot, stack.copy());
            }
        }
        return decoy;
    }
}