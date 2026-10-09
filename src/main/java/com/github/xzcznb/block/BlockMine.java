package com.github.xzcznb.block;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityGrenade;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMine extends Block
{
    public BlockMine()
    {
        super(Material.GROUND);
        this.setTranslationKey("mine");
        this.setHardness(5.0f);
        this.setResistance(100.0f);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            for (int i = 0; i < 5; i++) {
                spawnGrenade(world, pos);
            }
            world.setBlockToAir(pos);
        }
    }

    public void spawnGrenade(World world, BlockPos pos)
    {
        EntityGrenade grenade = new EntityGrenade(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.3);
        grenade.motionY = 0.3;
        grenade.motionX = world.rand.nextGaussian() * 0.2;
        grenade.motionZ = world.rand.nextGaussian() * 0.2;
        world.spawnEntity(grenade);
    }
}