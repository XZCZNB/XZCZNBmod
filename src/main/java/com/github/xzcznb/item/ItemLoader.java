package com.github.xzcznb.item;

import com.github.xzcznb.XZCZNB;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = XZCZNB.MODID)
public class ItemLoader
{
    public static Item goldenHead = new ItemGoldenHead();
    public static Item obsidianSword = new ItemObsidianSword();
    public static Item mace = new ItemMace();
    public static Item spear = new ItemSpear();
    public static Item scythe = new ItemScythe();
    public static Item royalGuard = new ItemRoyalGuard();
    public static Item enchantedGoldenCarrot = new ItemEnchantedGoldenCarrot();
    public static Item enchantedGoldenHead = new ItemEnchantedGoldenHead();
    public static Item enchantedSpeckledMelon = new ItemEnchantedSpeckledMelon();
    public static Item pvpSoup = new ItemPVPSoup();
    public static Item grenade = new ItemGrenade();
    public static Item cannon = new ItemCannon();
    public static Item shell = new ItemShell();
    public static Item cookedEgg = new ItemCookedEgg();
    public static Item shop = new ItemShop();
    public static Item superBow = new ItemSuperBow();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        register(event, goldenHead, "golden_head");
        register(event, obsidianSword, "obsidian_sword");
        register(event, mace, "mace");
        register(event, spear, "spear");
        register(event, scythe, "scythe");
        register(event, royalGuard, "royal_guard");
        register(event, enchantedGoldenCarrot, "enchanted_golden_carrot");
        register(event, enchantedGoldenHead, "enchanted_golden_head");
        register(event, enchantedSpeckledMelon, "enchanted_speckled_melon");
        register(event, pvpSoup, "pvp_soup");
        register(event, grenade, "grenade");
        register(event, cannon, "cannon");
        register(event, shell, "shell");
        register(event, cookedEgg, "cooked_egg");
        register(event, shop, "shop");
        register(event, superBow, "super_bow");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event)
    {
        registerRender(goldenHead);
        registerRender(obsidianSword);
        registerRender(mace);
        registerRender(spear);
        registerRender(scythe);
        registerRender(royalGuard);
        registerRender(enchantedGoldenCarrot);
        registerRender(enchantedGoldenHead);
        registerRender(enchantedSpeckledMelon);
        registerRender(pvpSoup);
        registerRender(grenade);
        registerRender(cannon);
        registerRender(shell);
        registerRender(cookedEgg);
        registerRender(shop);
        registerRender(superBow);
    }

    private static void register(RegistryEvent.Register<Item> event, Item item, String name)
    {
        event.getRegistry().register(item.setRegistryName(XZCZNB.MODID, name));
    }

    @SideOnly(Side.CLIENT)
    private static void registerRender(Item item)
    {
        ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, model);
    }
}