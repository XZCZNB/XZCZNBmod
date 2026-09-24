package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemCookedEgg extends ItemFood
{
    public ItemCookedEgg()
    {
        super(4, 0.5f, false);
        this.setMaxStackSize(64);
        this.setTranslationKey("cookedEgg");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setPotionEffect(new PotionEffect(MobEffects.STRENGTH, 10, 1), 1.0f);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
        }
        super.onFoodEaten(stack, worldIn, player);
    }
}