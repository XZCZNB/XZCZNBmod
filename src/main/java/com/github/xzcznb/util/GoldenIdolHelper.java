package com.github.xzcznb.util;

import com.github.xzcznb.block.BlockGoldenIdol;
import com.github.xzcznb.block.BlockLoader;
import com.github.xzcznb.entity.IDecoy;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.tile.TileEntityGoldenIdol;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class GoldenIdolHelper {
    public static boolean isGoldenIdolValid(TileEntityGoldenIdol goldenIdol) {
        NBTTagCompound nbt = goldenIdol.getEntityNbt();
        String type = goldenIdol.getEntityType();
        if (nbt == null || type == null || type.isEmpty()) return false;
        if (isValidUUID(type)) return true;
        return EntityList.getClass(new ResourceLocation(type)) != null;
    }

    public static boolean isValidUUID(String string) {
        if (string == null || string.isEmpty()) return false;
        try {
            UUID.fromString(string);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static EntityLivingBase findEntityByUUID(World world, UUID uuid) {
        for (Entity entity : world.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase && entity.getUniqueID().equals(uuid)) {
                return (EntityLivingBase) entity;
            }
        }
        return null;
    }

    public static boolean tryAurify(float chance, EntityLivingBase attacker, EntityLivingBase target) {
        if (attacker == null || target == null || !target.isEntityAlive()) return false;
        World world = attacker.world;
        if (world.isRemote) return false;
        if (attacker.getRNG().nextFloat() >= chance) return false;
        if (target instanceof IDecoy && ((IDecoy) target).isDecoy() || target.isPotionActive(PotionLoader.purification)) return false;
        BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
        IBlockState oldState = world.getBlockState(pos);
        if (oldState.getBlock() instanceof BlockGoldenIdol) return false;
        world.setBlockState(pos, BlockLoader.goldenIdol.getDefaultState());
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityGoldenIdol)) {
            world.setBlockState(pos, oldState);
            return false;
        }
        TileEntityGoldenIdol goldenIdol = (TileEntityGoldenIdol) tileEntity;
        if (target instanceof EntityPlayer) {
            goldenIdol.setFromPlayer((EntityPlayer) target);
            target.onKillCommand();
        } else {
            goldenIdol.setFromEntity(target);
            if (target instanceof IEntityMultiPart) target.setHealth(0);
            else DecoyHelper.vanish(target);
        }
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        return true;
    }

    public static float aurifyChance(ItemStack held, EntityLivingBase target) {
        float chance = 0.0625f + ItemHelper.getTotalEnchantLevel(held) * 0.015625f;
        if (target != null) {
            if (target.getActivePotionEffect(PotionLoader.purification) != null) return 0;
            if (!target.isPotionApplicable(new PotionEffect(PotionLoader.corruption, 1, 0))) chance *= 4.0f;
            else {
                PotionEffect corruption = target.getActivePotionEffect(PotionLoader.corruption);
                if (corruption != null) chance += 0.0625f * (corruption.getAmplifier() + 1);
            }
            float bonus = 2.0f - target.getHealth() / target.getMaxHealth();
            chance *= bonus;
        }
        return chance;
    }
}
