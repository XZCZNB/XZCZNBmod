package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityGrenade;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
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
    private static final long COOLDOWN_TICKS = 40;

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
        long lastThrowTime = data.getLong("xzcznb_grenade_last_throw_time");
        if (lastThrowTime != 0 && currentTime - lastThrowTime < COOLDOWN_TICKS) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Cooldown: " +
                    TextFormatting.GOLD + (COOLDOWN_TICKS - (currentTime - lastThrowTime)) +
                    TextFormatting.GREEN + " Ticks Remaining!"));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        EntityGrenade grenade = new EntityGrenade(world, player);
        grenade.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, 1.0f, 0.0f);
        world.spawnEntity(grenade);
        data.setLong("xzcznb_grenade_last_throw_time", currentTime);
        boolean hasInfinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        if (!player.isCreative() && !hasInfinity) {
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}