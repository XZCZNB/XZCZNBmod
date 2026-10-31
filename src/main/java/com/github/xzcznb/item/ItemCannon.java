package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityShell;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemCannon extends Item {
    private static final long COOLDOWN_TICKS = 100;

    public ItemCannon() {
        this.setTranslationKey("xzcznb_cannon");
        this.setMaxDamage(256);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        NBTTagCompound data = player.getEntityData();
        long currentTime = world.getTotalWorldTime();
        long lastThrowTime = data.getLong("xzcznb_cannon_last_throw_time");
        if (lastThrowTime != 0 && currentTime - lastThrowTime < COOLDOWN_TICKS) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Cooldown: " +
                    TextFormatting.GOLD + (COOLDOWN_TICKS - (currentTime - lastThrowTime)) +
                    TextFormatting.GREEN + " Ticks Remaining!"));
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        boolean isCreativeMode = player.isCreative();
        boolean hasInfinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        if (isCreativeMode || hasInfinity) {
            for (int i = 0; i < 4; i++) spawnShell(world, player);
            if (!isCreativeMode) stack.damageItem(1, player);
            data.setLong("xzcznb_cannon_last_throw_time", currentTime);
        } else {
            ItemStack ammoStack = findAmmo(player);
            if (!ammoStack.isEmpty()) {
                for (int i = 0; i < 4; i++) spawnShell(world, player);
                ammoStack.shrink(1);
                stack.damageItem(1, player);
                data.setLong("xzcznb_cannon_last_throw_time", currentTime);
            } else {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient ammunition"));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private ItemStack findAmmo(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemShell) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private void spawnShell(World world, EntityPlayer player) {
        EntityShell shell = new EntityShell(world, player);
        shell.shoot(player, player.rotationPitch, player.rotationYaw, 0, 3.6f, 1.0f);
        world.spawnEntity(shell);
    }
}