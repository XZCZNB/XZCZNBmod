package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityRoyalGuard;
import com.github.xzcznb.util.TeamHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRoyalGuard extends Item {

    public ItemRoyalGuard() {
        this.setTranslationKey("xzcznb_royal_guard");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) entity;
            if (!(target instanceof EntityTameable) || !((EntityTameable) target).isTamed()) {
                TeamHelper.joinTeam(target, "B");
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return EnumActionResult.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);
        pos = pos.offset(side);
        if (!player.canPlayerEdit(pos, side, stack)) return EnumActionResult.FAIL;
        EntityRoyalGuard guard = EntityRoyalGuard.create(world);
        guard.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        guard.setTamed(true);
        guard.setOwnerId(player.getUniqueID());
        world.spawnEntity(guard);
        if (!player.isCreative()) {
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
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}