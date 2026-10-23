package com.github.xzcznb.util;

import com.github.xzcznb.block.BlockLoader;
import com.github.xzcznb.entity.IDecoy;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.tile.TileEntityGoldenIdol;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class GoldenIdolHelper {
    public static boolean isGoldenIdolValid(TileEntityGoldenIdol goldenIdol) {
        NBTTagCompound tag = goldenIdol.getEntityNbt();
        String type = goldenIdol.getEntityType();
        if (tag == null || type == null || type.isEmpty()) return false;
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

    public static boolean aurify(EntityLivingBase attacker, EntityLivingBase target) {
        World world = attacker.world;
        if (world.isRemote) return false;
        if (target == null || !target.isEntityAlive()) return false;
        if (target instanceof IDecoy && ((IDecoy) target).isDecoy() || target.isPotionActive(PotionLoader.purification)) return false;
        BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
        if (world.getBlockState(pos).getBlock() == BlockLoader.goldenIdol) return false;
        IBlockState oldState = world.getBlockState(pos);
        world.setBlockState(pos, BlockLoader.goldenIdol.getDefaultState());
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityGoldenIdol)) {
            world.setBlockState(pos, oldState);
            return false;
        }
        TileEntityGoldenIdol goldenIdol = (TileEntityGoldenIdol) tileEntity;
        if (target instanceof EntityPlayer) {
            goldenIdol.setFromPlayer((EntityPlayer) target);
            target.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        } else {
            goldenIdol.setFromEntity(target);
            DecoyHelper.vanish(target);
        }
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        return true;
    }
}
