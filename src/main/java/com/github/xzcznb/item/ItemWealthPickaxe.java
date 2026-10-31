package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.util.GoldenIdolHelper;
import com.github.xzcznb.util.VIPHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ItemWealthPickaxe extends ItemPickaxe {
    public static ToolMaterial Wealth = EnumHelper.addToolMaterial("wealth", 4, 33550336, 648.0f, 8.0f, 64);

    public ItemWealthPickaxe() {
        super(Wealth);
        this.setTranslationKey("xzcznb_wealth_pickaxe");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        EntityLivingBase target = (EntityLivingBase) entity;
        float chance = GoldenIdolHelper.aurifyChance(stack, target);
        if (VIPHelper.isVip(attacker.getUniqueID())) chance *= 5.0f;
        return attacker.getRNG().nextFloat() < chance && GoldenIdolHelper.tryAurify(chance, attacker, target);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}