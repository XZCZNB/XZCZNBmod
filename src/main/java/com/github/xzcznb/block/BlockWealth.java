package com.github.xzcznb.block;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockWealth extends Block {

    public BlockWealth() {
        super(Material.ROCK);
        this.setTranslationKey("wealth");
        this.setHardness(648.0f);
        this.setResistance(1080.0f);
        this.setHarvestLevel("pickaxe", 3);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(BlockLoader.wealth);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess world, BlockPos pos, BlockPos beaconPos) {
        return true;
    }
}