package com.github.xzcznb.crafting;

import com.github.xzcznb.block.BlockLoader;
import com.github.xzcznb.item.ItemLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingLoader
{
    public CraftingLoader()
    {
        registerRecipe();
        registerSmelting();
        registerFuel();
    }

    private static void registerRecipe() {
        ItemStack enchantedGrenade = new ItemStack(ItemLoader.grenade);
        enchantedGrenade.addEnchantment(Enchantments.INFINITY, 1);
        ItemStack enchantedCannon = new ItemStack(ItemLoader.cannon);
        enchantedCannon.addEnchantment(Enchantments.INFINITY, 1);
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "golden_head"), null, new ItemStack(ItemLoader.goldenHead, 4), new Object[]
                {
                        "###", "#*#", "###", '#', Items.GOLD_INGOT, '*', new ItemStack(Items.SKULL, 1, 3)
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "obsidian_sword"), null, new ItemStack(ItemLoader.obsidianSword), new Object[]
                {
                        " # ", " # ", " * ", '#', Blocks.OBSIDIAN, '*', Items.DIAMOND
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "enchanted_golden_carrot_2"), null, new ItemStack(ItemLoader.EnchantedGoldenCarrot, 2), new Object[]
                {
                        "###", "#*#", "###", '#', Items.GOLDEN_CARROT, '*', ItemLoader.EnchantedGoldenCarrot
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "enchanted_golden_carrot"), null, new ItemStack(ItemLoader.EnchantedGoldenCarrot), new Object[]
                {
                        "###", "#*#", "###", '#', Blocks.GOLD_BLOCK, '*', Items.GOLDEN_CARROT
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "enchanted_golden_head"), null, new ItemStack(ItemLoader.EnchantedGoldenHead), new Object[]
                {
                        "###", "#*#", "###", '#', Items.GOLDEN_CARROT, '*', new ItemStack(Items.SKULL, 1, 3)
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "enchanted_grenade"), null, enchantedGrenade, new Object[]
                {
                        "###", "###", '#', Blocks.IRON_BLOCK
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "enchanted_speckled_melon"), null, new ItemStack(ItemLoader.EnchantedSpeckledMelon, 9), new Object[]
                {
                        "###", "#*#", "###", '#', Items.GOLD_INGOT, '*', Items.SPECKLED_MELON
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "spear"), null, new ItemStack(ItemLoader.spear), new Object[]
                {
                        "#  ", " * ", "  *", '#', Blocks.OBSIDIAN, '*', Items.DIAMOND
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "cannon"), null, new ItemStack(ItemLoader.cannon), new Object[]
                {
                        "###", " *#", "   ", '#', Items.IRON_INGOT, '*', Items.REDSTONE
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "enchanted_cannon"), null, enchantedCannon, new Object[]
                {
                        "###", "#*#", "###", '#', Blocks.IRON_BLOCK, '*', ItemLoader.cannon
                });
        GameRegistry.addShapedRecipe(new ResourceLocation("xzcznb", "loyal_zombie"), null, new ItemStack(ItemLoader.loyal_zombie), new Object[]
                {
                        "###", "#*#", "###", '#', Items.BONE, '*', ItemLoader.EnchantedGoldenHead
                });
        GameRegistry.addShapelessRecipe(new ResourceLocation("xzcznb", "pvp_soup_4"), null,
                new ItemStack(ItemLoader.pvpSoup, 4),
                Ingredient.fromStacks(new ItemStack(Items.MUSHROOM_STEW)));
        GameRegistry.addShapelessRecipe(new ResourceLocation("xzcznb", "pvp_soup"), null,
                new ItemStack(ItemLoader.pvpSoup),
                Ingredient.fromStacks(new ItemStack(Items.BOWL)));
        GameRegistry.addShapelessRecipe(new ResourceLocation("xzcznb", "grenade"), null,
                new ItemStack(ItemLoader.grenade),
                Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)));
        GameRegistry.addShapelessRecipe(new ResourceLocation("xzcznb", "mine"), null,
                new ItemStack(BlockLoader.mine),
                Ingredient.fromStacks(enchantedGrenade));
        GameRegistry.addShapelessRecipe(new ResourceLocation("xzcznb", "shell_4"), null,
                new ItemStack(ItemLoader.shell, 4),
                Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)),
                Ingredient.fromStacks(new ItemStack(Items.GUNPOWDER)));
        GameRegistry.addShapelessRecipe(new ResourceLocation("xzcznb", "totem_of_undying_1"), null,
                new ItemStack(Items.TOTEM_OF_UNDYING, 1),
                Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE)));
    }

    private static void registerSmelting()
    {
        GameRegistry.addSmelting(Items.ROTTEN_FLESH, new ItemStack(Items.POTIONITEM, 3, 0), 1.0f);
        GameRegistry.addSmelting(Items.STRING, new ItemStack(Items.POTIONITEM, 3, 0), 1.0f);
        GameRegistry.addSmelting(Items.GLASS_BOTTLE, new ItemStack(Items.POTIONITEM, 3, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(Items.DYE, 1, 1), new ItemStack(Items.POTIONITEM, 3, 0), 1.0f);
        GameRegistry.addSmelting(new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.POTIONITEM, 3, 0), 1.0f);
        GameRegistry.addSmelting(Blocks.OBSIDIAN, new ItemStack(Blocks.OBSIDIAN, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.SOUL_SAND, new ItemStack(Blocks.SOUL_SAND, 2), 1.0f);
        GameRegistry.addSmelting(Items.GUNPOWDER, new ItemStack(Items.GUNPOWDER, 2), 1.0f);
        GameRegistry.addSmelting(Items.REDSTONE, new ItemStack(Items.REDSTONE, 2), 1.0f);
        GameRegistry.addSmelting(Items.GLOWSTONE_DUST, new ItemStack(Items.GLOWSTONE_DUST, 2), 1.0f);
        GameRegistry.addSmelting(Items.NETHER_WART, new ItemStack(Items.NETHER_WART, 2), 1.0f);
        GameRegistry.addSmelting(Items.GHAST_TEAR, new ItemStack(Items.GHAST_TEAR, 2), 1.0f);
        GameRegistry.addSmelting(Items.SUGAR, new ItemStack(Items.SUGAR, 2), 1.0f);
        GameRegistry.addSmelting(Items.BLAZE_POWDER, new ItemStack(Items.BLAZE_POWDER, 2), 1.0f);
        GameRegistry.addSmelting(Items.SPIDER_EYE, new ItemStack(Items.SPIDER_EYE, 2), 1.0f);
        GameRegistry.addSmelting(Items.FERMENTED_SPIDER_EYE, new ItemStack(Items.FERMENTED_SPIDER_EYE, 2), 1.0f);
        GameRegistry.addSmelting(Items.SPECKLED_MELON, new ItemStack(Items.SPECKLED_MELON, 2), 1.0f);
        GameRegistry.addSmelting(Items.RABBIT_FOOT, new ItemStack(Items.RABBIT_FOOT, 2), 1.0f);
        GameRegistry.addSmelting(Items.MAGMA_CREAM, new ItemStack(Items.MAGMA_CREAM, 2), 1.0f);
        GameRegistry.addSmelting(Items.LEATHER, new ItemStack(Items.LEATHER, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.HOPPER, new ItemStack(Blocks.HOPPER, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.REDSTONE_LAMP, new ItemStack(Blocks.REDSTONE_LAMP, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.SEA_LANTERN, new ItemStack(Blocks.SEA_LANTERN, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.LIT_PUMPKIN, new ItemStack(Blocks.LIT_PUMPKIN, 2), 1.0f);
        GameRegistry.addSmelting(Items.REPEATER, new ItemStack(Items.REPEATER, 2), 1.0f);
        GameRegistry.addSmelting(Items.COMPARATOR, new ItemStack(Items.COMPARATOR, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.STICKY_PISTON, new ItemStack(Blocks.STICKY_PISTON, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.PISTON, new ItemStack(Blocks.PISTON, 2), 1.0f);
        GameRegistry.addSmelting(Items.SLIME_BALL, new ItemStack(Items.SLIME_BALL, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.SLIME_BLOCK, new ItemStack(Blocks.SLIME_BLOCK, 2), 1.0f);
        GameRegistry.addSmelting(Blocks.DAYLIGHT_DETECTOR, new ItemStack(Blocks.DAYLIGHT_DETECTOR, 2), 1.0f);
        GameRegistry.addSmelting(new ItemStack(Items.SKULL, 1, 1), new ItemStack(Items.SKULL, 2, 1), 1.0f);
        GameRegistry.addSmelting(ItemLoader.goldenHead, new ItemStack(ItemLoader.goldenHead, 2), 1.0f);
        GameRegistry.addSmelting(ItemLoader.shell, new ItemStack(ItemLoader.shell, 2), 1.0f);
        GameRegistry.addSmelting(ItemLoader.grenade, new ItemStack(ItemLoader.grenade, 2), 1.0f);
        GameRegistry.addSmelting(Items.EGG, new ItemStack(ItemLoader.cooked_egg, 1), 1.0f);
        GameRegistry.addSmelting(new ItemStack(Items.POTIONITEM, 1, 16421), new ItemStack(ItemLoader.EnchantedSpeckledMelon, 4), 1.0f);
    }

    private static void registerFuel()
    {
        GameRegistry.registerFuelHandler(fuel -> fuel.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN) ? 32000 : 0);
    }
}