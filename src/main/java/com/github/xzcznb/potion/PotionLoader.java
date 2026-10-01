package com.github.xzcznb.potion;

import com.github.xzcznb.XZCZNB;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = XZCZNB.MODID)
public class PotionLoader {

    public static Potion corruption = new PotionCorruption();
    public static Potion purification = new PotionPurification();

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(corruption);
        event.getRegistry().register(purification);
    }
}