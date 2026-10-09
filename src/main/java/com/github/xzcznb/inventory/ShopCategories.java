package com.github.xzcznb.inventory;

import com.github.xzcznb.block.BlockLoader;
import com.github.xzcznb.item.ItemLoader;
import com.github.xzcznb.potion.PotionLoader;
import com.github.xzcznb.util.PotionHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.List;

public class ShopCategories {

    public static List<ShopCategory> getDefault() {
        List<ShopCategory> categories = new ArrayList<>();
        categories.add(new ShopCategory("dye", new ItemStack(Items.DYE), dye(), 64));
        categories.add(new ShopCategory("misc", new ItemStack(Items.ENDER_PEARL), misc(), 48));
        categories.add(new ShopCategory("resource", new ItemStack(Blocks.COBBLESTONE), resource(), 32));
        categories.add(new ShopCategory("wealth", new ItemStack(Items.EMERALD), wealth(), 16));
        categories.add(new ShopCategory("food", new ItemStack(Items.GOLDEN_APPLE), foods(), 12));
        categories.add(new ShopCategory("potion", new ItemStack(Items.BREWING_STAND), potions(), 6));
        if (Math.random() < 0.25) {
            categories.add(new ShopCategory("advanced", new ItemStack(Items.NETHER_STAR), advanced(), 3));
        }
        if (Math.random() < 0.05) {
            categories.add(new ShopCategory("Ultimate", new ItemStack(Blocks.DRAGON_EGG), Ultimate(), 2));
        }
        if (Math.random() < 0.01) {
            categories.add(new ShopCategory("Jackpot", new ItemStack(ItemLoader.shop), Jackpot(), 1));
        }
        return categories;
    }

    private static List<Trade> dye() {
        List<Trade> list = new ArrayList<>();
        list.add(new Trade(new ItemStack(Items.ROTTEN_FLESH, 1), new ItemStack(Items.CLAY_BALL, 4)));
        list.add(new Trade(new ItemStack(Items.ROTTEN_FLESH, 1), new ItemStack(Items.REEDS, 4)));
        for (int i = 0; i < 16; i++) {
            list.add(new Trade(new ItemStack(Items.ROTTEN_FLESH, 1), new ItemStack(Items.DYE, 4, i)));
        }
        return list;
    }

    private static List<Trade> misc() {
        List<Trade> list = new ArrayList<>();
        list.add(new Trade(new ItemStack(Items.WHEAT_SEEDS, 1), new ItemStack(Items.ENDER_PEARL, 1)));
        list.add(new Trade(new ItemStack(Items.RABBIT_HIDE, 1), new ItemStack(Items.RABBIT_FOOT, 1)));
        list.add(new Trade(new ItemStack(Items.SPIDER_EYE, 1), new ItemStack(Items.GHAST_TEAR, 2)));
        list.add(new Trade(new ItemStack(Items.NETHER_WART, 1), new ItemStack(Items.BLAZE_ROD, 1)));
        list.add(new Trade(new ItemStack(Items.GUNPOWDER, 1), new ItemStack(Items.GLOWSTONE_DUST, 64)));
        list.add(new Trade(new ItemStack(Items.LEATHER, 1), new ItemStack(Items.EXPERIENCE_BOTTLE, 64)));
        list.add(new Trade(new ItemStack(Items.EXPERIENCE_BOTTLE, 1), new ItemStack(Items.EXPERIENCE_BOTTLE, 64)));
        list.add(new Trade(new ItemStack(Items.GLASS_BOTTLE, 1), new ItemStack(Items.GLASS_BOTTLE, 16)));
        list.add(new Trade(new ItemStack(Blocks.END_STONE, 1), new ItemStack(Items.ENDER_PEARL, 1)));
        list.add(new Trade(new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.ENDER_PEARL, 1)));
        list.add(new Trade(new ItemStack(Items.STICK, 1), new ItemStack(Items.BOWL, 1)));
        list.add(new Trade(new ItemStack(Items.STRING, 1), new ItemStack(Items.STRING, 4)));
        list.add(new Trade(new ItemStack(Items.BOWL, 1), new ItemStack(Items.BOWL, 4)));
        return list;
    }

    private static List<Trade> resource() {
        List<Trade> list = new ArrayList<>();
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LOG, 16, 0)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LOG, 16, 1)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LOG, 16, 2)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LOG, 16, 3)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.SPONGE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.SAND, 16, 0)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.SAND, 16, 1)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LAPIS_ORE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.GOLD_ORE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.IRON_ORE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.DIAMOND_ORE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.REDSTONE_ORE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.GLOWSTONE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.QUARTZ_ORE, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.SEA_LANTERN, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LOG2, 16, 0)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.LOG2, 16, 1)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.PRISMARINE, 16, 0)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.PRISMARINE, 16, 1)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.PRISMARINE, 16, 2)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.SLIME_BLOCK, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.DISPENSER, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.REDSTONE_LAMP, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.DAYLIGHT_DETECTOR, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.OBSERVER, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.HOPPER, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.PISTON, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.STICKY_PISTON, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.DROPPER, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.GOLDEN_RAIL, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.DETECTOR_RAIL, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.RAIL, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.REPEATER, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.COMPARATOR, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.SLIME_BALL, 64)));
        return list;
    }

    private static List<Trade> wealth() {
        List<Trade> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(new Trade(new ItemStack(Blocks.STONE, 4, i), new ItemStack(Items.EMERALD, 16)));
        }
        list.add(new Trade(new ItemStack(Blocks.GRASS, 4), new ItemStack(Items.EMERALD, 16)));
        list.add(new Trade(new ItemStack(Blocks.DIRT, 4), new ItemStack(Items.EMERALD, 16)));
        list.add(new Trade(new ItemStack(Blocks.COBBLESTONE, 4), new ItemStack(Items.EMERALD, 16)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 4), new ItemStack(Blocks.EMERALD_ORE, 16)));
        return list;
    }

    private static List<Trade> foods() {
        List<Trade> list = new ArrayList<>();
        list.add(new Trade(new ItemStack(Items.MUSHROOM_STEW, 1), new ItemStack(Items.GOLDEN_CARROT, 6)));
        list.add(new Trade(new ItemStack(Items.COOKED_PORKCHOP, 1), new ItemStack(Items.GOLDEN_CARROT, 10)));
        list.add(new Trade(new ItemStack(Items.COOKED_FISH, 1, 0), new ItemStack(Items.GOLDEN_CARROT, 5)));
        list.add(new Trade(new ItemStack(Items.COOKED_FISH, 1, 1), new ItemStack(Items.GOLDEN_CARROT, 7)));
        list.add(new Trade(new ItemStack(Items.COOKIE, 1), new ItemStack(Items.GOLDEN_CARROT, 1)));
        list.add(new Trade(new ItemStack(Items.COOKED_BEEF, 1), new ItemStack(Items.GOLDEN_CARROT, 10)));
        list.add(new Trade(new ItemStack(Items.APPLE, 1), new ItemStack(Items.GOLDEN_APPLE, 3)));
        list.add(new Trade(new ItemStack(Items.BREAD, 1), new ItemStack(Items.GOLDEN_APPLE, 5)));
        list.add(new Trade(new ItemStack(Items.FISH, 1, 2), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.FISH, 1, 3), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.CAKE, 1), new ItemStack(Items.GOLDEN_APPLE, 7)));
        list.add(new Trade(new ItemStack(Items.MELON, 1), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.CHICKEN, 1), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.COOKED_CHICKEN, 1), new ItemStack(Items.GOLDEN_APPLE, 6)));
        list.add(new Trade(new ItemStack(Items.CARROT, 1), new ItemStack(Items.GOLDEN_CARROT, 3)));
        list.add(new Trade(new ItemStack(Items.BAKED_POTATO, 1), new ItemStack(Items.GOLDEN_CARROT, 5)));
        list.add(new Trade(new ItemStack(Items.POTATO, 1), new ItemStack(Items.GOLDEN_APPLE, 5)));
        list.add(new Trade(new ItemStack(Items.POISONOUS_POTATO, 1), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.PUMPKIN_PIE, 1), new ItemStack(Items.GOLDEN_APPLE, 6)));
        list.add(new Trade(new ItemStack(Items.COOKED_RABBIT, 1), new ItemStack(Items.GOLDEN_CARROT, 6)));
        list.add(new Trade(new ItemStack(Items.RABBIT_STEW, 1), new ItemStack(Items.GOLDEN_APPLE, 11)));
        list.add(new Trade(new ItemStack(Items.COOKED_MUTTON, 1), new ItemStack(Items.GOLDEN_CARROT, 7)));
        list.add(new Trade(new ItemStack(Items.WHEAT, 1), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.FEATHER, 1), new ItemStack(Items.GOLDEN_APPLE, 1)));
        list.add(new Trade(new ItemStack(Items.EGG, 1), new ItemStack(Items.GOLDEN_APPLE, 4)));
        list.add(new Trade(new ItemStack(Items.FLINT, 1), new ItemStack(Items.GOLDEN_APPLE, 4)));
        list.add(new Trade(new ItemStack(Items.GOLD_NUGGET, 1), new ItemStack(Items.GOLDEN_CARROT, 1)));
        list.add(new Trade(new ItemStack(Items.ARROW, 16), new ItemStack(Items.GOLDEN_APPLE, 16)));
        return list;
    }

    private static List<Trade> potions() {
        List<Trade> list = new ArrayList<>();
        int corruption = Potion.getIdFromPotion(PotionLoader.corruption);
        int purification = Potion.getIdFromPotion(PotionLoader.purification);
        for (int i = 0; i < 3; i++) {
            int amplifier = (int) (Math.random() * 4);
            ItemStack potion = PotionHelper.customPotion(Items.SPLASH_POTION, false, new int[][]{{corruption, amplifier, 9600}});
            list.add(new Trade(new ItemStack(Items.EMERALD, 1), potion));
        }
        for (int i = 0; i < 3; i++) {
            int amplifier = (int) (Math.random() * 4);
            ItemStack potion = PotionHelper.customPotion(Items.POTIONITEM, true, new int[][]{{purification, amplifier, 9600}});
            list.add(new Trade(new ItemStack(Items.EMERALD, 1), potion));
        }
        return list;
    }

    private static List<Trade> advanced() {
        List<Trade> list = new ArrayList<>();
        list.add(new Trade(new ItemStack(Items.GOLDEN_APPLE, 1, 1), new ItemStack(Items.TOTEM_OF_UNDYING, 64)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Items.GOLDEN_APPLE, 4, 1)));
        list.add(new Trade(new ItemStack(Items.EMERALD, 1), new ItemStack(Blocks.GOLD_BLOCK, 64)));
        list.add(new Trade(new ItemStack(ItemLoader.shop, 64), new ItemStack(ItemLoader.scythe, 1)));
        return list;
    }

    private static List<Trade> Ultimate() {
        List<Trade> list = new ArrayList<>();
        list.add(new Trade(new ItemStack(Blocks.DRAGON_EGG, 1), new ItemStack(BlockLoader.wealth, 64), new ItemStack(ItemLoader.royalGuard, 1)));
        return list;
    }

    private static List<Trade> Jackpot() {
        List<Trade> list = new ArrayList<>();
        double random = Math.random();
        if (random < 0.01) {
            list.add(new Trade(new ItemStack(Items.AIR, 1), new ItemStack(BlockLoader.wealth, 64)));
        } else if (random < 0.05) {
            list.add(new Trade(new ItemStack(Items.AIR, 1), new ItemStack(ItemLoader.shop, 64)));
        } else if (random < 0.25) {
            list.add(new Trade(new ItemStack(Items.AIR, 1), new ItemStack(Items.GOLDEN_APPLE, 64, 1)));
        } else {
            list.add(new Trade(new ItemStack(Items.AIR, 1), new ItemStack(Items.GOLDEN_APPLE, 4 + (int) (Math.random() * 16.0), 1)));
        }
        return list;
    }
}