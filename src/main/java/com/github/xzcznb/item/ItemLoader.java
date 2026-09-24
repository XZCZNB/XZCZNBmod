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
    public static Item loyal_zombie = new ItemLoyalZombie();
    public static Item EnchantedGoldenCarrot = new ItemEnchantedGoldenCarrot();
    public static Item EnchantedGoldenHead = new ItemEnchantedGoldenHead();
    public static Item pvpSoup = new ItemPVPSoup();
    public static Item grenade = new ItemGrenade();
    public static Item cannon = new ItemCannon();
    public static Item shell = new ItemShell();
    public static Item cooked_egg = new ItemCookedEgg();
    public static Item EnchantedSpeckledMelon = new ItemEnchantedSpeckledMelon();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        register(event, goldenHead, "golden_head");
        register(event, obsidianSword, "obsidian_sword");
        register(event, mace, "mace");
        register(event, spear, "spear");
        register(event, loyal_zombie, "loyal_zombie");
        register(event, EnchantedGoldenCarrot, "enchanted_golden_carrot");
        register(event, EnchantedGoldenHead, "enchanted_golden_head");
        register(event, pvpSoup, "pvp_soup");
        register(event, grenade, "grenade");
        register(event, cannon, "cannon");
        register(event, shell, "shell");
        register(event, cooked_egg, "cooked_egg");
        register(event, EnchantedSpeckledMelon, "enchanted_speckled_melon");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerRenders(ModelRegistryEvent event)
    {
        registerRender(goldenHead);
        registerRender(obsidianSword);
        registerRender(mace);
        registerRender(spear);
        registerRender(loyal_zombie);
        registerRender(EnchantedGoldenCarrot);
        registerRender(EnchantedGoldenHead);
        registerRender(pvpSoup);
        registerRender(grenade);
        registerRender(cannon);
        registerRender(shell);
        registerRender(cooked_egg);
        registerRender(EnchantedSpeckledMelon);
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