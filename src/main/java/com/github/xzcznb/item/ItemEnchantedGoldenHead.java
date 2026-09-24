package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemEnchantedGoldenHead extends ItemFood
{
    public ItemEnchantedGoldenHead()
    {
        super(6, 1.2f, false);
        this.setAlwaysEdible();
        this.setMaxStackSize(64);
        this.setTranslationKey("EnchantedGoldenHead");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 600, 2), 1.0f);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3600, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 9600, 1));
        }
        super.onFoodEaten(stack, worldIn, player);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}