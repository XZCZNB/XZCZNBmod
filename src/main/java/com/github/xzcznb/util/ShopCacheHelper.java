package com.github.xzcznb.util;

import com.github.xzcznb.inventory.ShopCategory;
import com.github.xzcznb.inventory.Trade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ShopCacheHelper {

    public static NBTTagList serialize(List<ShopCategory> categories) {
        NBTTagList list = new NBTTagList();
        for (ShopCategory category : categories) {
            NBTTagCompound catTag = new NBTTagCompound();
            catTag.setString("Name", category.name);
            catTag.setTag("Icon", category.icon.writeToNBT(new NBTTagCompound()));
            catTag.setInteger("Count", category.count);
            NBTTagList tradeList = new NBTTagList();
            for (Trade trade : category.trades) {
                NBTTagCompound tradeTag = new NBTTagCompound();
                tradeTag.setTag("Price", trade.price.writeToNBT(new NBTTagCompound()));
                if (!trade.price2.isEmpty()) tradeTag.setTag("Price2", trade.price2.writeToNBT(new NBTTagCompound()));
                tradeTag.setTag("Product", trade.product.writeToNBT(new NBTTagCompound()));
                tradeList.appendTag(tradeTag);
            }
            catTag.setTag("Trades", tradeList);
            list.appendTag(catTag);
        }
        return list;
    }

    public static List<ShopCategory> deserialize(NBTTagList list) {
        List<ShopCategory> categories = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound catTag = list.getCompoundTagAt(i);
            String name = catTag.getString("Name");
            ItemStack icon = new ItemStack(catTag.getCompoundTag("Icon"));
            int count = catTag.getInteger("Count");
            List<Trade> trades = new ArrayList<>();
            NBTTagList tradeList = catTag.getTagList("Trades", 10);
            for (int j = 0; j < tradeList.tagCount(); j++) {
                NBTTagCompound tradeTag = tradeList.getCompoundTagAt(j);
                ItemStack price = new ItemStack(tradeTag.getCompoundTag("Price"));
                ItemStack price2 = tradeTag.hasKey("Price2") ? new ItemStack(tradeTag.getCompoundTag("Price2")) : ItemStack.EMPTY;
                ItemStack product = new ItemStack(tradeTag.getCompoundTag("Product"));
                trades.add(new Trade(price, price2, product));
            }
            categories.add(new ShopCategory(name, icon, trades, count));
        }
        return categories;
    }

    public static List<ShopCategory> load(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey("xzcznb_shop_cache")) return null;
        NBTTagList list = data.getTagList("xzcznb_shop_cache", 10);
        if (list.tagCount() == 0) return null;
        return deserialize(list);
    }

    public static void save(EntityPlayer player, List<ShopCategory> categories) {
        player.getEntityData().setTag("xzcznb_shop_cache", serialize(categories));
    }
}