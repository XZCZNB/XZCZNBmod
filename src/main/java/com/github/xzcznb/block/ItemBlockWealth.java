package com.github.xzcznb.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockWealth extends ItemBlock {

    public ItemBlockWealth(Block block) {
        super(block);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}