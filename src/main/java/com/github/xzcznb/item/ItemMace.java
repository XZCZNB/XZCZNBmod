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
    public static final int RESET_TICKS = 64;
    public static final int MAX_COOLDOWN_TICKS = 4;
    private static final String COMBO_TAG = "comboSeq";
    private static final String TIME_TAG = "lastActionTime";
    private static final String IS_HOVERING_TAG = "isHovering";
    private static final String HOVER_TIMER_TAG = "hoverTimer";
    private static final int MAX_HOVER_TICKS = 4;

    public static ToolMaterial Mace = EnumHelper.addToolMaterial("mace", 3, 4000, 30.0f, 0, 30);

    public ItemMace() {
        super(Mace);
        this.setTranslationKey("mace");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    public enum ComboSequence {
        NONE,
        A1,
        A2,
        B,
        SMASH
    }

    private static NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static void setCombo(ItemStack stack, ComboSequence seq) {
        NBTTagCompound tag = getTag(stack);
        tag.setInteger(COMBO_TAG, seq.ordinal());
    }

    public static ComboSequence getCombo(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return ComboSequence.NONE;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(COMBO_TAG)) {
            return ComboSequence.NONE;
        }
        int ordinal = tag.getInteger(COMBO_TAG);
        if (ordinal < 0 || ordinal >= ComboSequence.values().length) {
            return ComboSequence.NONE;
        }
        return ComboSequence.values()[ordinal];
    }

    private long getLastActionTime(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag.hasKey(TIME_TAG) ? tag.getLong(TIME_TAG) : 0;
    }

    private void setLastActionTime(ItemStack stack, long time) {
        NBTTagCompound tag = getTag(stack);
        tag.setLong(TIME_TAG, time);
    }

    private void setIsHovering(ItemStack stack, boolean isHovering) {
        NBTTagCompound tag = getTag(stack);
        tag.setBoolean(IS_HOVERING_TAG, isHovering);
    }

    private boolean getIsHovering(ItemStack stack) {
        if (!stack.hasTagCompound()) return false;
        NBTTagCompound tag = stack.getTagCompound();
        return tag.hasKey(IS_HOVERING_TAG) && tag.getBoolean(IS_HOVERING_TAG);
    }

    private void setHoverTimer(ItemStack stack, int ticks) {
        NBTTagCompound tag = getTag(stack);
        tag.setInteger(HOVER_TIMER_TAG, ticks);
    }

    private int getHoverTimer(ItemStack stack) {
        if (!stack.hasTagCompound()) return 0;
        NBTTagCompound tag = stack.getTagCompound();
        return tag.hasKey(HOVER_TIMER_TAG) ? tag.getInteger(HOVER_TIMER_TAG) : 0;
    }

    public ComboSequence getNextCombo(ItemStack stack, boolean isRightClick, EntityPlayer player) {
        ComboSequence current = getCombo(stack);
        long now = player.world.getTotalWorldTime();
        long last = getLastActionTime(stack);
        long elapsed = now - last;
        if (elapsed > RESET_TICKS) {
            setCombo(stack, ComboSequence.NONE);
            return isRightClick ? ComboSequence.A1 : ComboSequence.SMASH;
        }
        if (isRightClick) {
            switch (current) {
                case NONE:  return ComboSequence.A1;
                case A1: return ComboSequence.A2;
                case A2: if (elapsed < MAX_COOLDOWN_TICKS) return ComboSequence.B;
                default:    return ComboSequence.A1;
            }
        } else {
            return ComboSequence.SMASH;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!world.isRemote) {
            ComboSequence combo = getNextCombo(stack, true, player);
            setCombo(stack, combo);
            setLastActionTime(stack, world.getTotalWorldTime());
            doPlayerMotion(player, combo);
            if (combo == ComboSequence.B) {
                sweepingAttack(stack, player, 0);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void doPlayerMotion(EntityPlayer player, ComboSequence combo) {
        double rad = Math.toRadians(player.rotationYaw);
        double forwardX = -Math.sin(rad);
        double forwardZ = Math.cos(rad);
        switch (combo) {
            case A1:
                if(player.motionY < 0) player.motionY = 0;
                player.addVelocity(forwardX * 0.0, 0.6, forwardZ * 0.0);
                player.fallDistance = 0;
                break;
            case A2:
                player.addVelocity(forwardX * 0.0, 0.4, forwardZ * 0.0);
                player.fallDistance = 0;
                break;
            case B:
                player.addVelocity(forwardX * 1.6, 0.2, forwardZ * 1.6);
                player.fallDistance = 0;
                break;
            default:
                break;
        }
        player.velocityChanged = true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        if (!(target instanceof EntityLivingBase)) return false;
        return onLeftClickAttack(stack, player, (EntityLivingBase) target, 1.0f) > 0;
    }

    public float onLeftClickAttack(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, float damage) {
        World world = attacker.world;
        if (world.isRemote) return 0;
        int totalEnchantLevel = ItemHelper.getTotalEnchantLevel(stack);
        float bonus = MathHelper.sqrt(1.0f + totalEnchantLevel);
        float damagePerBlock = 2.0f + bonus;
        float fallDistance = attacker.fallDistance;
        float attackDamage = BASE_DAMAGE + damage;
        if(attacker.getRNG().nextFloat() < 0.2f) {
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
                setIsHovering(stack, true);
                setHoverTimer(stack, 0);
            }
            target.addVelocity(forwardX * 0.0, attacker.motionY / 2 + 0.6, forwardZ * 0.0);
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
        if (getIsHovering(stack)) {
            if (attacker.motionY < 0) {
                attacker.motionY = 0;
            }
            attacker.fallDistance = 0;
            attacker.velocityChanged = true;
            int timer = getHoverTimer(stack);
            timer++;
            setHoverTimer(stack, timer);
            if (timer >= MAX_HOVER_TICKS) {
                setIsHovering(stack, false);
                setHoverTimer(stack, 0);
            }
        }
    }
}