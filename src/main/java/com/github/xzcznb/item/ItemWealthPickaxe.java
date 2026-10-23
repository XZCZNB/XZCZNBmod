package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.util.GoldenIdolHelper;
import com.github.xzcznb.util.ItemHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ItemWealthPickaxe extends ItemPickaxe {

    public static ToolMaterial Wealth = EnumHelper.addToolMaterial("wealth", 4, 648, 648.0f, 8.0f, 64);

    public ItemWealthPickaxe() {
        super(Wealth);
        this.setTranslationKey("wealth_pickaxe");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        float chance = 0.1f + 0.02f * ItemHelper.getTotalEnchantLevel(stack);
        return player.getRNG().nextFloat() < chance && GoldenIdolHelper.aurify(player, (EntityLivingBase) entity);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}