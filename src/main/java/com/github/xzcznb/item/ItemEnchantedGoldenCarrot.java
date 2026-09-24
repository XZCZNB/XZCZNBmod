package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemEnchantedGoldenCarrot extends ItemFood
{
    public ItemEnchantedGoldenCarrot()
    {
        super(8, 1.5f, false);
        this.setMaxStackSize(64);
        this.setTranslationKey("EnchantedGoldenCarrot");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setPotionEffect(new PotionEffect(MobEffects.SATURATION, 9600, 0), 1.0f);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            EGCEffects(player);
        }
        super.onFoodEaten(stack, worldIn, player);
    }

    public static void EGCEffects(EntityLivingBase entity) {
        if (entity == null) return;
        entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 3600, 0));
        entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, 3600, 1));
        entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 3600, 2));
        entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3600, 2));
        entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 36000, 0));
        entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 36000, 0));
        entity.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 36000, 1));
        entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 36000, 2));
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}