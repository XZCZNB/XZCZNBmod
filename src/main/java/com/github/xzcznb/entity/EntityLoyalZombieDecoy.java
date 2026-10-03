package com.github.xzcznb.entity;

import com.github.xzcznb.util.DecoyHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityLoyalZombieDecoy extends EntityLoyalZombie {

    protected EntityLoyalZombie summoner;

    public EntityLoyalZombieDecoy(World world) {
        super(world);
        this.isDecoy = true;
        this.summoner = null;
        this.experienceValue = 0;
        DecoyHelper.setLifeTimer(this, 32 + 8 * this.getRNG().nextInt(32));
    }

    private EntityLoyalZombieDecoy(World world, EntityLoyalZombie summoner) {
        this(world);
        this.summoner = summoner;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
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
        DecoyHelper.tick(this);
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
            DecoyHelper.vanish(this);
        }
        return true;
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        if (entityIn == this.summoner) return true;
        if (entityIn instanceof EntityLoyalZombieDecoy) {
            return ((EntityLoyalZombieDecoy) entityIn).summoner == this.summoner;
        }
        return super.isOnSameTeam(entityIn);
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
    public EntityLoyalZombie getComboOwner() {
        return this.summoner != null ? this.summoner : this;
    }

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