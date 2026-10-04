package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityRoyalGuard;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRoyalGuard extends Item {

    public ItemRoyalGuard() {
        this.setTranslationKey("royal_guard");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(pos.offset(side), side, stack)) return EnumActionResult.FAIL;
        pos = pos.offset(side);
        EntityRoyalGuard zombie = EntityRoyalGuard.create(world);
        zombie.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        zombie.setTamed(true);
        zombie.setOwnerId(player.getUniqueID());
        world.spawnEntity(zombie);
        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}