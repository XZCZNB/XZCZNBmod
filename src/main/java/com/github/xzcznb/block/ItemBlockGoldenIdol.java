package com.github.xzcznb.block;

import com.github.xzcznb.tile.TileEntityGoldenIdol;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockGoldenIdol extends ItemBlock {

    public ItemBlockGoldenIdol(Block block) {
        super(block);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state)) return false;
        if (world.isRemote) return true;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGoldenIdol) {
                TileEntityGoldenIdol goldenIdol = (TileEntityGoldenIdol) tileEntity;
                goldenIdol.readCustomNBT(tag);
                world.notifyBlockUpdate(pos, state, state, 3);
                tileEntity.markDirty();
            }
        }
        return true;
    }
}