package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import java.util.List;

public class ItemMace extends ItemSword {
    public static final float BASE_DAMAGE = 2.0f;
    private static final int MAX_HOVER_TICKS = 5;
    private static final long COOLDOWN = 80;

    public static ToolMaterial Mace = EnumHelper.addToolMaterial("mace", 3, 4096, 30.0f, 0, 30);

    public ItemMace() {
        super(Mace);
        this.setTranslationKey("mace");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItemMainhand();
        player.swingArm(hand);
        if (!world.isRemote) {
            long currentTime = world.getTotalWorldTime();
            NBTTagCompound data = player.getEntityData();
            long lastAttackTime = data.getLong("MaceCooldown");
            if (lastAttackTime == 0 || currentTime - lastAttackTime >= COOLDOWN) {
                doSweepingEdge(stack, player, 1.0f);
                data.setLong("MaceCooldown", currentTime);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        if (!(target instanceof EntityLivingBase)) return false;
        float damage = onLeftClickAttack(stack, player, (EntityLivingBase) target, 1.0f);
        player.heal(damage * 0.05f);
        return damage > 0;
    }

    public float onLeftClickAttack(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, float damage) {
        World world = attacker.world;
        if (world.isRemote) return 0;
        int totalEnchantLevel = ItemHelper.getTotalEnchantLevel(stack);
        float bonus = MathHelper.sqrt(1.0f + totalEnchantLevel);
        float damagePerBlock = 2.0f + bonus;
        float fallDistance = attacker.fallDistance;
        float attackDamage = BASE_DAMAGE + damage;
        if (attacker.getRNG().nextFloat() < 0.2f) {
            fallDistance *= 5.0f;
        }
        float totalDamage = attackDamage + fallDistance * damagePerBlock;
        target.attackEntityFrom(DamageSource.causeMobDamage(attacker), totalDamage);
        float yaw = attacker.rotationYaw;
        double forwardX = -Math.sin(Math.toRadians(yaw));
        double forwardZ = Math.cos(Math.toRadians(yaw));
        if (fallDistance < 3.0f) {
            attacker.motionY = 0.8;
            attacker.fallDistance = 0;
            attacker.velocityChanged = true;
            target.motionY = 0.2;
        } else {
            attacker.motionY = totalEnchantLevel * 0.1;
            attacker.fallDistance = 0;
            attacker.velocityChanged = true;
            attacker.hurtResistantTime = MAX_HOVER_TICKS;
            world.createExplosion(
                    attacker,
                    target.posX,
                    target.posY + target.getEyeHeight(),
                    target.posZ,
                    0,
                    false
            );
            BlockPos posBelow = new BlockPos(target.posX, target.posY - 0.1, target.posZ);
            IBlockState state = world.getBlockState(posBelow);
            if (state.getMaterial() != Material.AIR) {
                int count = Math.min(20 + totalEnchantLevel * 4, 80);
                int stateId = Block.getStateId(state);
                ParticleHelper.spawnParticles(attacker, EnumParticleTypes.BLOCK_DUST,
                        target.posX, target.posY + 0.1, target.posZ,
                        2.0, 0.5, 2.0,
                        0.5, 0.5, 0.5,
                        count, stateId);
            }
            if (attacker instanceof EntityPlayer) {
                NBTTagCompound data = attacker.getEntityData();
                data.setBoolean("isHovering", true);
                data.setInteger("hoverTimer", 0);
            }
            target.addVelocity(forwardX * 0.0, attacker.motionY * 0.5 + 0.2, forwardZ * 0.0);
        }
        if (attacker instanceof EntityPlayer) stack.damageItem(1, attacker);
        return totalDamage;
    }

    private void sweepingAttack(ItemStack stack, EntityLivingBase attacker, float damage) {
        World world = attacker.world;
        if (world.isRemote) return;
        AxisAlignedBB bb = AttackBoundingBoxHelper.getAttackBB1(attacker, 4.0, 2.0, 1.0);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, bb, input -> input != attacker && input.isEntityAlive() && input.canBeCollidedWith());
        double rad = Math.toRadians(attacker.rotationYaw);
        float enchantBonus = MathHelper.sqrt(4.0f + ItemHelper.getTotalEnchantLevel(stack)) * 0.5f;
        float attackDamage = BASE_DAMAGE + damage;
        ParticleHelper.spawnArcParticles(attacker, EnumParticleTypes.LAVA, 6.0, 9, attacker.getEyeHeight() * 0.5, Math.toRadians(60), 0.5, 0.5, attacker.motionZ - 0.1);
        for (EntityLivingBase target : list) {
            if (attacker instanceof EntityPlayer && target instanceof EntityVillager || TeamHelper.isAlly(attacker, target)) continue;
            float speedBonus = MathHelper.sqrt(CombatHelper.getRelativeSpeed(attacker, target, 0.5, 0.1, 0.5) + 1.0);
            float totalDamage = enchantBonus * attackDamage * speedBonus;
            target.attackEntityFrom(DamageSource.causeMobDamage(attacker), totalDamage);
            target.addVelocity(
                    -Math.sin(rad) * 1.6,
                    0.4,
                    Math.cos(rad) * 1.6
            );
        }
        if (attacker instanceof EntityPlayer) stack.damageItem(1, attacker);
    }

    public void doSweepingEdge(ItemStack stack, EntityLivingBase attacker, float damage) {
        if (attacker == null || attacker.world.isRemote) return;
        if (stack == null || !(stack.getItem() instanceof ItemMace)) return;
        float yaw = attacker.rotationYaw;
        double rad = Math.toRadians(yaw);
        double forwardX = -Math.sin(rad);
        double forwardZ = Math.cos(rad);
        attacker.addVelocity(forwardX * 2.0, 0.2, forwardZ * 2.0);
        attacker.velocityChanged = true;
        sweepingAttack(stack, attacker, damage);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
        if (!isHeld || world.isRemote || !(entity instanceof EntityLivingBase)) return;
        EntityLivingBase attacker = (EntityLivingBase) entity;
        if (!(attacker instanceof EntityPlayer)) return;
        NBTTagCompound data = attacker.getEntityData();
        if (!data.getBoolean("isHovering")) return;
        if (attacker.motionY < 0) {
            attacker.motionY = 0;
        }
        attacker.fallDistance = 0;
        attacker.velocityChanged = true;
        int timer = data.getInteger("hoverTimer") + 1;
        data.setInteger("hoverTimer", timer);
        if (timer >= MAX_HOVER_TICKS) {
            data.setBoolean("isHovering", false);
            data.setInteger("hoverTimer", 0);
        }
    }
}