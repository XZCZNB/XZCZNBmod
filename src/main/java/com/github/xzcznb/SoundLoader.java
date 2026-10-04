package com.github.xzcznb;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = XZCZNB.MODID)
public class SoundLoader {

    public static final SoundEvent SWORD_KILL = createSound("item.sword.kill");
    public static final SoundEvent ROYAL_GUARD_SAY = createSound("mob.royal_guard.say");
    public static final SoundEvent ROYAL_GUARD_HURT = createSound("mob.royal_guard.hurt");
    public static final SoundEvent ROYAL_GUARD_HAPPY = createSound("mob.royal_guard.happy");
    public static final SoundEvent ROYAL_GUARD_DEATH = createSound("mob.royal_guard.death");
    public static final SoundEvent ROYAL_GUARD_FALL_BIG = createSound("mob.royal_guard.fall_big");
    public static final SoundEvent ROYAL_GUARD_FALL_SMALL = createSound("mob.royal_guard.fall_small");
    public static final SoundEvent ROYAL_GUARD_EAT = createSound("mob.royal_guard.eat");
    public static final SoundEvent ROYAL_GUARD_FULL = createSound("mob.royal_guard.full");
    public static final SoundEvent ROYAL_GUARD_RUN = createSound("mob.royal_guard.run");
    public static final SoundEvent ROYAL_GUARD_TP = createSound("mob.royal_guard.tp");

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
                ROYAL_GUARD_SAY,
                ROYAL_GUARD_HURT,
                ROYAL_GUARD_HAPPY,
                ROYAL_GUARD_DEATH,
                ROYAL_GUARD_FALL_BIG,
                ROYAL_GUARD_FALL_SMALL,
                ROYAL_GUARD_EAT,
                ROYAL_GUARD_FULL,
                ROYAL_GUARD_RUN,
                ROYAL_GUARD_TP
        );
    }
}