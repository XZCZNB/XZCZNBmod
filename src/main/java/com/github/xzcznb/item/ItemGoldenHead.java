package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemGoldenHead extends ItemFood
{
    public ItemGoldenHead()
    {
        super(4, 1.0f, false);
        this.setAlwaysEdible();
        this.setMaxStackSize(64);
        this.setTranslationKey("goldenHead");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
        this.setPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 120, 0), 1.0f);
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
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0));
        }
        super.onFoodEaten(stack, worldIn, player);
    }
}