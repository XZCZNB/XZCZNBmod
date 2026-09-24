package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityGrenade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemGrenade extends ItemSnowball {
    private static final int COOLDOWN_TICKS = 40;

    public ItemGrenade() {
        this.setTranslationKey("grenade");
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        NBTTagCompound data = player.getEntityData();
        long currentTime = world.getTotalWorldTime();
        long lastThrowTime = data.getLong("GrenadeLastThrowTime");
        if (lastThrowTime < 0 || currentTime < lastThrowTime) {
            lastThrowTime = currentTime - COOLDOWN_TICKS;
            data.setLong("GrenadeLastThrowTime", lastThrowTime);
        }
        long elapsed = currentTime - lastThrowTime;
        if (elapsed < COOLDOWN_TICKS) {
            long remaining = COOLDOWN_TICKS - elapsed;
            player.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Cooldown : " +
                            TextFormatting.GOLD + remaining +
                            TextFormatting.GREEN + " Tick"
            ));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        EntitySnowball grenade = new EntityGrenade(world, player);
        grenade.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.0F, 0.0F);
        world.spawnEntity(grenade);
        data.setLong("GrenadeLastThrowTime", currentTime);
        boolean hasInfinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, player.getHeldItem(hand)) > 0;
        if (!player.capabilities.isCreativeMode && !hasInfinity) {
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}