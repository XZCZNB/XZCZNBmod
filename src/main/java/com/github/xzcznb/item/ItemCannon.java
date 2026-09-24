package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.entity.EntityShell;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemCannon extends Item {
    public ItemCannon() {
        this.setTranslationKey("cannon");
        this.setMaxDamage(256);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }
    private static final int COOLDOWN_TICKS = 100;
    private long lastThrowTime = 0;
    private int lastFoundAmmoSlot = -1;

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        long currentTime = world.getTotalWorldTime();
        if (currentTime - lastThrowTime < COOLDOWN_TICKS) {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Cooldown: " +
                        TextFormatting.GOLD + (COOLDOWN_TICKS - (currentTime - lastThrowTime)) +
                        TextFormatting.GREEN + " Ticks Remaining!"));
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        boolean isCreativeMode = player.capabilities.isCreativeMode;
        boolean hasInfinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        if (!world.isRemote) {
            if (isCreativeMode || hasInfinity) {
                for (int i = 0; i < 4; i++) {
                    spawnShell(world, player);
                }
                if (!isCreativeMode) {
                    handleDamage(stack, player);
                }
                lastThrowTime = currentTime;
            } else {
                ItemStack ammoStack = findAmmo(player);
                if (!ammoStack.isEmpty()) {
                    for (int i = 0; i < 4; i++) {
                        spawnShell(world, player);
                    }
                    ammoStack.shrink(1);
                    if (ammoStack.isEmpty()) {
                        player.inventory.setInventorySlotContents(lastFoundAmmoSlot, ItemStack.EMPTY);
                        lastFoundAmmoSlot = -1;
                    }
                    handleDamage(stack, player);
                    lastThrowTime = currentTime;
                } else {
                    player.sendMessage(new TextComponentString(
                            TextFormatting.RED + "Insufficient ammunition"));
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void spawnShell(World world, EntityPlayer player) {
        EntityShell shell = new EntityShell(world, player);
        shell.shoot(player, player.rotationPitch, player.rotationYaw, 0, 3.0f, 1.0f);
        world.spawnEntity(shell);
    }

    private void handleDamage(ItemStack stack, EntityPlayer player) {
        stack.damageItem(1, player);
        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    private ItemStack findAmmo(EntityPlayer player) {
        if (lastFoundAmmoSlot != -1) {
            ItemStack item = player.inventory.getStackInSlot(lastFoundAmmoSlot);
            if (!item.isEmpty() && item.getItem() instanceof ItemShell) {
                return item;
            }
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemShell) {
                lastFoundAmmoSlot = i;
                return stack;
            }
        }
        lastFoundAmmoSlot = -1;
        return ItemStack.EMPTY;
    }
}