package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemSuperBook extends ItemFood {

    public ItemSuperBook() {
        super(6, 6.0f, false);
        this.setAlwaysEdible();
        this.setMaxStackSize(64);
        this.setTranslationKey("xzcznb_super_book");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addExperienceLevel(36);
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 7200, 5));
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}