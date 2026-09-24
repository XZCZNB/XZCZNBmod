package com.github.xzcznb;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = XZCZNB.MODID)
public class SoundLoader {

    public static final SoundEvent SWORD_KILL = createSound("item.sword.kill");
    public static final SoundEvent LOYAL_ZOMBIE_SAY = createSound("mob.loyal_zombie.say");
    public static final SoundEvent LOYAL_ZOMBIE_HURT = createSound("mob.loyal_zombie.hurt");
    public static final SoundEvent LOYAL_ZOMBIE_DEATH = createSound("mob.loyal_zombie.death");
    public static final SoundEvent LOYAL_ZOMBIE_FALL_BIG = createSound("mob.loyal_zombie.fall_big");
    public static final SoundEvent LOYAL_ZOMBIE_FALL_SMALL = createSound("mob.loyal_zombie.fall_small");
    public static final SoundEvent LOYAL_ZOMBIE_EAT = createSound("mob.loyal_zombie.eat");
    public static final SoundEvent LOYAL_ZOMBIE_FULL = createSound("mob.loyal_zombie.full");
    public static final SoundEvent LOYAL_ZOMBIE_RUN = createSound("mob.loyal_zombie.run");
    public static final SoundEvent LOYAL_ZOMBIE_TP = createSound("mob.loyal_zombie.tp");

    private static SoundEvent createSound(String name) {
        ResourceLocation id = new ResourceLocation(XZCZNB.MODID, name);
        SoundEvent event = new SoundEvent(id);
        event.setRegistryName(id);
        return event;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                SWORD_KILL,
                LOYAL_ZOMBIE_SAY,
                LOYAL_ZOMBIE_HURT,
                LOYAL_ZOMBIE_DEATH,
                LOYAL_ZOMBIE_FALL_BIG,
                LOYAL_ZOMBIE_FALL_SMALL,
                LOYAL_ZOMBIE_EAT,
                LOYAL_ZOMBIE_FULL,
                LOYAL_ZOMBIE_RUN,
                LOYAL_ZOMBIE_TP
        );
    }
}