package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemSuperBow extends ItemBow {

    public ItemSuperBow() {
        this.setTranslationKey("super_bow");
        this.setMaxDamage(8680);
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        int charge = this.getMaxItemUseDuration(stack) - timeLeft;
        float velocity = (float) charge / 10.0f;
        if (velocity < 0.1f) return;
        if (velocity > 1.0f) velocity = 1.0f;
        if (!world.isRemote) {
            Vec3d look = player.getLookVec();
            double bonus = 1.0 + 0.1 * ItemHelper.getTotalEnchantLevel(stack);
            double speed = 1.5 * velocity * bonus;
            if (player.motionY < 0) player.motionY = 0;
            player.addVelocity(look.x * speed, 0.1 + look.y * speed, look.z * speed);
            player.velocityChanged = true;
            player.fallDistance = 0;
            if (!player.capabilities.isCreativeMode) {
                stack.damageItem(1, player);
            }
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            player.addStat(StatList.getObjectUseStats(this));
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}