package com.github.xzcznb.item;

import com.github.xzcznb.creativetab.CreativeTabsLoader;
import com.github.xzcznb.util.CombatHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.util.EnumHelper;

public class ItemObsidianSword extends ItemSword
{
    public static ToolMaterial ObsidianSword = EnumHelper.addToolMaterial("ObsidianSword", 3, 8000, 30.0f, 4.0f, 30);
    public ItemObsidianSword()
    {
        super(ObsidianSword);
        this.setTranslationKey("obsidianSword");
        this.setCreativeTab(CreativeTabsLoader.tabXZCZNB);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (CombatHelper.tryExecute(target, attacker)) {
            stack.damageItem(1, attacker);
            return true;
        }
        return super.hitEntity(stack, target, attacker);
    }
}