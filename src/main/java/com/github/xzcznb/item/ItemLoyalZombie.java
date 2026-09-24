package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityLoyalZombie;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLoyalZombie extends Item {

    public ItemLoyalZombie() {
        this.setTranslationKey("loyal_zombie");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setMaxStackSize(1);
        this.setMaxDamage(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return EnumActionResult.SUCCESS;
        if (!player.canPlayerEdit(pos.offset(side), side, stack)) return EnumActionResult.FAIL;
        pos = pos.offset(side);
        double d0 = 0;
        if (side == EnumFacing.UP && world.getBlockState(pos).getBlock() instanceof BlockFence) {
            d0 = 0.5;
        }
        EntityLoyalZombie zombie = EntityLoyalZombie.create(world);
        zombie.setPosition((double) pos.getX() + 0.5, (double) pos.getY() + d0, (double) pos.getZ() + 0.5);
        zombie.setTamed(true);
        zombie.setOwnerId(player.getUniqueID());
        world.spawnEntity(zombie);
        if (!player.capabilities.isCreativeMode) {
            stack.damageItem(1, player);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public int getItemEnchantability() {
        return 30;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}