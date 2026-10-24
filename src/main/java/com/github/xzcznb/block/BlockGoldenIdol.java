package com.github.xzcznb.block;

import com.github.xzcznb.item.ItemWealthPickaxe;
import com.github.xzcznb.tile.TileEntityGoldenIdol;
import com.github.xzcznb.util.GoldenIdolHelper;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class BlockGoldenIdol extends BlockContainer {

    private static final AxisAlignedBB FULL = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1.0, 0.75);

    public BlockGoldenIdol() {
        super(Material.IRON);
        setTranslationKey("golden_idol");
        setHardness(5.0f);
        setResistance(50.0f);
        setLightLevel(1.0f);
        setHarvestLevel("pickaxe", 3);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityGoldenIdol();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return FULL;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (world.isRemote) return;
        world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = player.getHeldItemMainhand();
        if (!ItemHelper.holding(held, ItemWealthPickaxe.class)) return false;
        if (world.isRemote) return true;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityGoldenIdol)) return false;
        TileEntityGoldenIdol goldenIdol = (TileEntityGoldenIdol) tileEntity;
        NBTTagCompound nbt = goldenIdol.getEntityNbt();
        String type = goldenIdol.getEntityType();
        if (nbt == null || type == null || type.isEmpty()) return false;
        if (GoldenIdolHelper.isValidUUID(type)) return false;
        if ("minecraft:ender_dragon".equals(type)) return false;
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(type), world);
        if (!(entity instanceof EntityLivingBase)) return false;
        EntityLivingBase target = (EntityLivingBase) entity;
        target.readFromNBT(nbt);
        UUID uuid = target.getUniqueID();
        if (GoldenIdolHelper.findEntityByUUID(world, uuid) != null) {
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
            return true;
        }
        target.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        world.spawnEntity(target);
        world.setBlockToAir(pos);
        return true;
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityGoldenIdol)) return;
        if (player.isCreative() || !player.canHarvestBlock(state)) return;
        TileEntityGoldenIdol goldenIdol = (TileEntityGoldenIdol) tileEntity;
        ItemStack held = player.getHeldItemMainhand();
        if (ItemHelper.holding(held, ItemWealthPickaxe.class)) {
            ItemStack drop = new ItemStack(this);
            NBTTagCompound tag = new NBTTagCompound();
            goldenIdol.writeCustomNBT(tag);
            drop.setTagCompound(tag);
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
        } else {
            int count = 1 + player.getRNG().nextInt(2);
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Blocks.GOLD_BLOCK, count)));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {}

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityGoldenIdol)) {
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
            return;
        }
        TileEntityGoldenIdol goldenIdol = (TileEntityGoldenIdol) tileEntity;
        if (!GoldenIdolHelper.isGoldenIdolValid(goldenIdol)) {
            world.setBlockState(pos, Blocks.GOLD_BLOCK.getDefaultState());
        }
    }
}